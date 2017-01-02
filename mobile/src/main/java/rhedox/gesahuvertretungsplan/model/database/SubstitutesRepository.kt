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
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesAdapter
import rhedox.gesahuvertretungsplan.util.Open

/**
 * Created by robin on 29.10.2016.
 */
@Open
class SubstitutesRepository(context: Context) : android.support.v4.content.Loader.OnLoadCompleteListener<Cursor> {
    private val context: Context = context.applicationContext
    private val observer: Observer;

    private val substituteLoaders = mutableMapOf<Int, CursorLoader>()
    private val announcementLoaders = mutableMapOf<Int, CursorLoader>()

    var substitutesCallback: ((date: LocalDate, substitutes: List<Substitute>) -> Unit)? = null;
    var announcementCallback: ((date: LocalDate, text: String) -> Unit)? = null;

    init {
        observer = Observer {
            if(it.pathSegments.size > 1 && it.pathSegments[1] == "date") {
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
        context.contentResolver.unregisterContentObserver(observer)

        for((date, loader) in substituteLoaders) {
            loader.unregisterListener(this)
            if(loader.isStarted) {
                loader.cancelLoad()
                loader.stopLoading()
                loader.reset()
            }
        }
        substituteLoaders.clear()
    }

    fun loadSubstitutesForDay(date: LocalDate) {
        val id = date.unixTimeStamp
        var loader = substituteLoaders[id]
        if(loader == null) {
            loader = CursorLoader(context.applicationContext, SubstitutesContract.uriWithDate(date), SubstitutesContract.Table.columns.toTypedArray(), null, null, "${SubstitutesContract.Table.columnIsRelevant} DESC, ${SubstitutesContract.Table.columnLessonBegin} ASC, ${SubstitutesContract.Table.columnLessonEnd} ASC, ${SubstitutesContract.Table.columnCourse}");
            loader.registerListener(id, this)
            substituteLoaders[id] = loader
        } else {
            loader.reset()
        }
        loader.startLoading();
    }

    fun loadAnnouncementForDay(date: LocalDate) {
        val id = date.unixTimeStamp
        var loader = announcementLoaders[id]
        if(loader == null) {
            loader = CursorLoader(context.applicationContext, AnnouncementsContract.uriWithDate(date), AnnouncementsContract.Table.columns.toTypedArray(), null, null, null);
            loader.registerListener(id, this)
            announcementLoaders[id] = loader
        } else {
            loader.reset()
        }
        loader.startLoading();
    }

    override fun onLoadComplete(loader: android.support.v4.content.Loader<Cursor>?, data: Cursor?) {
        if(loader == null || data == null)
            return;

        if(loader == substituteLoaders[loader.id]) {
            substitutesCallback?.invoke(localDateFromUnix(loader.id), SubstituteAdapter.listFromCursor(data))
        } else if(loader == announcementLoaders[loader.id]) {
            announcementCallback?.invoke(localDateFromUnix(loader.id), AnnouncementAdapter.fromCursor(data))
        }
        data.close()
    }

    fun requestUpdate(account: Account, date: LocalDate) {
        if(!ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority) && !ContentResolver.isSyncPending(account, SubstitutesContentProvider.authority)) {
            val extras = Bundle()
            extras.putInt(SubstitutesSyncService.SyncAdapter.extraDate, date.unixTimeStamp)
            extras.putBoolean(SubstitutesSyncService.SyncAdapter.extraSingleDay, false)
            //extras.putLong(SyncAdapter.extraDate, date.toDateTime(LocalTime(0)).millis)
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
            val cursor = context.contentResolver.query(SubstitutesContract.uriWithDate(date), SubstitutesContract.Table.columns.toTypedArray(), filter, null, "${SubstitutesContract.Table.columnIsRelevant} DESC, ${SubstitutesContract.Table.columnLessonBegin} ASC, ${SubstitutesContract.Table.columnLessonEnd} ASC, ${SubstitutesContract.Table.columnCourse}");
            val substitutes = SubstituteAdapter.listFromCursor(cursor)
            cursor.close()
            return substitutes;
        }

    }
}