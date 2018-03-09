package rhedox.gesahuvertretungsplan.model.database

import android.accounts.Account
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.content.ContentResolver
import android.content.Context
import android.content.SyncRequest
import android.os.Build
import android.os.Bundle
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.service.SubstitutesSyncService
import rhedox.gesahuvertretungsplan.util.Open
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 29.10.2016.
 */
@Open
class SubstitutesRepository(context: Context) {
    private val substitutesDao = context.appKodein().instance<SubstitutesDao>()
    private val supervisionsDao = context.appKodein().instance<SupervisionsDao>()
    private val announcementsDao = context.appKodein().instance<AnnouncementsDao>()

    fun destroy() {
    }

    fun loadSubstitutesForDay(date: LocalDate): LiveData<List<Substitute>> = substitutesDao.get(date);

    fun loadAnnouncementForDay(date: LocalDate): LiveData<Announcement> = Transformations.map(announcementsDao.get(date),
            { it.firstOrNull() }
    )

    fun loadSupervisionsForDay(date: LocalDate) = supervisionsDao.get(date)

    fun requestUpdate(account: Account, date: LocalDate, singleDay: Boolean) {
        if(!ContentResolver.isSyncActive(account, StubSubstitutesContentProvider.authority) && !ContentResolver.isSyncPending(account, StubSubstitutesContentProvider.authority)) {
            val extras = Bundle()
            extras.putInt(SubstitutesSyncService.SyncAdapter.extraDate, date.unixTimeStamp)
            extras.putBoolean(SubstitutesSyncService.SyncAdapter.extraSingleDay, singleDay)
            extras.putBoolean(SubstitutesSyncService.SyncAdapter.extraIgnorePast, true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val syncRequest = SyncRequest.Builder()
                        .setSyncAdapter(account, StubSubstitutesContentProvider.authority)
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

                ContentResolver.requestSync(account, StubSubstitutesContentProvider.authority, bundle)
            }
        }
    }

    companion object {
        @JvmStatic
        fun loadSubstitutesForDaySync(context: Context, date: LocalDate, onlyRelevant: Boolean = false): List<Substitute> {
            /*val filter = if(onlyRelevant) "${SubstitutesContract.Table.columnIsRelevant} = '1'" else null;
            val cursor = context.contentResolver.query(SubstitutesContract.uriWithDate(date), SubstitutesContract.Table.columns.toTypedArray(), filter, null, "${SubstitutesContract.Table.columnIsRelevant} DESC, ${SubstitutesContract.Table.columnLessonBegin} ASC, ${SubstitutesContract.Table.columnDuration} ASC, ${SubstitutesContract.Table.columnCourse}");
            val substitutes = SubstituteAdapter.listFromCursor(cursor)
            cursor.close()
            return substitutes;*/
            TODO()
        }
    }
}