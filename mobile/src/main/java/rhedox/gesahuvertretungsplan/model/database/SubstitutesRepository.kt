package rhedox.gesahuvertretungsplan.model.database

import android.accounts.Account
import android.content.ContentResolver
import android.content.Context
import android.content.Loader
import android.content.SyncRequest
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.database.tables.AnnouncementAdapter
import rhedox.gesahuvertretungsplan.model.database.tables.AnnouncementsContract
import rhedox.gesahuvertretungsplan.model.database.tables.SubstituteAdapter
import rhedox.gesahuvertretungsplan.model.database.tables.SubstitutesContract
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp
import rhedox.gesahuvertretungsplan.service.SubstitutesSyncService
import rhedox.gesahuvertretungsplan.ui.adapter.SubstitutesAdapter
import rhedox.gesahuvertretungsplan.util.Open
import java.util.concurrent.Future

/**
 * Created by robin on 29.10.2016.
 */
@Open
class SubstitutesRepository(context: Context) {
    private val context: Context = context.applicationContext
    private val observer: Observer;
    private val contentResolver = context.contentResolver

    private val futures = mutableMapOf<Int, Future<Unit>>();

    var substitutesCallback: ((date: LocalDate, substitutes: List<Substitute>) -> Unit)? = null
    var announcementCallback: ((date: LocalDate, text: String) -> Unit)? = null

    init {
        observer = Observer {
            if(it.pathSegments.size > 1 && (it.pathSegments[1] == SubstitutesContract.datePath || it.pathSegments[1] == AnnouncementsContract.datePath)) {
                if(it.pathSegments[0] == SubstitutesContract.path)
                    loadSubstitutesForDay(localDateFromUnix(it.lastPathSegment.toInt()));
                else if(it.pathSegments[0]== AnnouncementsContract.path)
                    loadAnnouncementForDay(localDateFromUnix(it.lastPathSegment.toInt()));
            }
        }
        context.contentResolver.registerContentObserver(SubstitutesContract.dateUri, true, observer);
        context.contentResolver.registerContentObserver(AnnouncementsContract.dateUri, true, observer);
    }

    fun destroy() {
        substitutesCallback = null;
        announcementCallback = null;
        context.contentResolver.unregisterContentObserver(observer)

        for ((key, value) in futures) {
            value.cancel(true)
        }
        futures.clear()
    }

    fun loadSubstitutesForDay(date: LocalDate) {
        load({
            val cursor = contentResolver.query(SubstitutesContract.uriWithDate(date), SubstitutesContract.Table.columns.toTypedArray(), null, null, "${SubstitutesContract.Table.columnIsRelevant} DESC, ${SubstitutesContract.Table.columnLessonBegin} ASC, ${SubstitutesContract.Table.columnDuration} ASC, ${SubstitutesContract.Table.columnCourse}")
            val list = SubstituteAdapter.listFromCursor(cursor)
            cursor.close()
            return@load list
        }, {
            substitutesCallback?.invoke(date, it)
        })
    }

    fun loadAnnouncementForDay(date: LocalDate) {
        load({
            val cursor = contentResolver.query(AnnouncementsContract.uriWithDate(date), AnnouncementsContract.Table.columns.toTypedArray(), null, null, null)
            val announcement = AnnouncementAdapter.fromCursor(cursor)
            cursor.close()
            return@load announcement
        }, {
            announcementCallback?.invoke(date, it)
        })
    }

    private fun <T>load(async: () -> T, done: (data: T) -> Unit) {
        var addedToList = false
        var key = 0
        while (futures[key] != null) {
            key++;
        }

        val future = doAsync {
            val data = async();
            uiThread {
                done(data)
                if(addedToList) {
                    futures.remove(key)
                }
            }
        }
        futures.put(key, future)
        addedToList = true;
    }

    fun requestUpdate(account: Account, date: LocalDate, singleDay: Boolean) {
        if(!ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority) && !ContentResolver.isSyncPending(account, SubstitutesContentProvider.authority)) {
            val extras = Bundle()
            extras.putInt(SubstitutesSyncService.SyncAdapter.extraDate, date.unixTimeStamp)
            extras.putBoolean(SubstitutesSyncService.SyncAdapter.extraSingleDay, singleDay)
            extras.putBoolean(SubstitutesSyncService.SyncAdapter.extraIgnorePast, true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val syncRequest = SyncRequest.Builder()
                        .setSyncAdapter(account, SubstitutesContentProvider.authority)
                        .setExpedited(true)
                        .setManual(true)
                        .setDisallowMetered(false)
                        .setIgnoreSettings(true)
                        .setIgnoreBackoff(true)
                        .setNoRetry(true)
                        .setExtras(extras)
                        .syncOnce()
                        .build()
                ContentResolver.requestSync(syncRequest)
            } else {
                val bundle = Bundle()
                bundle.putAll(extras)
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true)
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true)
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, true)

                ContentResolver.requestSync(account, SubstitutesContentProvider.authority, bundle)
            }
        }
    }

    class Observer(private val callback: (uri: Uri) -> Unit): ContentObserver(Handler()) {

        override fun onChange(selfChange: Boolean) {
            onChange(selfChange, null);
        }

        override fun onChange(selfChange: Boolean, uri: Uri?) {
            Log.i("SubstitutesObserver", "onChange: $uri");

            if(uri != null)
                callback(uri)
        }
    }

    companion object {
        @JvmStatic
        fun loadSubstitutesForDaySync(context: Context, date: LocalDate, onlyRelevant: Boolean = false): List<Substitute> {
            val filter = if(onlyRelevant) "${SubstitutesContract.Table.columnIsRelevant} = '1'" else null;
            val cursor = context.contentResolver.query(SubstitutesContract.uriWithDate(date), SubstitutesContract.Table.columns.toTypedArray(), filter, null, "${SubstitutesContract.Table.columnIsRelevant} DESC, ${SubstitutesContract.Table.columnLessonBegin} ASC, ${SubstitutesContract.Table.columnDuration} ASC, ${SubstitutesContract.Table.columnCourse}");
            val substitutes = SubstituteAdapter.listFromCursor(cursor)
            cursor.close()
            return substitutes;
        }

    }
}