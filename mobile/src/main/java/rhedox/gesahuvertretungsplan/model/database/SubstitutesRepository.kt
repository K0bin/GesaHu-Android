package rhedox.gesahuvertretungsplan.model.database

import android.accounts.Account
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.content.ContentResolver
import android.content.SyncRequest
import android.os.Build
import android.os.Bundle
import androidx.os.bundleOf
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.dependencyInjection.PresenterScope
import rhedox.gesahuvertretungsplan.model.database.dao.AnnouncementsDao
import rhedox.gesahuvertretungsplan.model.database.dao.SubstitutesDao
import rhedox.gesahuvertretungsplan.model.database.dao.SupervisionsDao
import rhedox.gesahuvertretungsplan.model.database.entity.Announcement
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute
import rhedox.gesahuvertretungsplan.service.SubstitutesSyncService
import rhedox.gesahuvertretungsplan.util.Open
import rhedox.gesahuvertretungsplan.util.unixTimeStamp
import javax.inject.Inject

/**
 * Created by robin on 29.10.2016.
 */
@Open
@PresenterScope
class SubstitutesRepository @Inject constructor(private val substitutesDao: SubstitutesDao, private val supervisionsDao: SupervisionsDao, private val announcementsDao: AnnouncementsDao) {
    fun loadSubstitutesForDay(date: LocalDate): LiveData<List<Substitute>> = substitutesDao.get(date);

    fun loadAnnouncementForDay(date: LocalDate): LiveData<Announcement> = Transformations.map(announcementsDao.get(date),
            { it.firstOrNull() }
    )

    fun loadSupervisionsForDay(date: LocalDate) = supervisionsDao.get(date)

    fun loadSubstitutesForDaySync(date: LocalDate, onlyRelevant: Boolean = false): List<Substitute> = substitutesDao.getSync(date, onlyRelevant)

    fun requestUpdate(account: Account, date: LocalDate, singleDay: Boolean) {
        if(!ContentResolver.isSyncActive(account, StubSubstitutesContentProvider.authority) && !ContentResolver.isSyncPending(account, StubSubstitutesContentProvider.authority)) {
            val extras = bundleOf(
                    SubstitutesSyncService.SyncAdapter.extraDate to date.unixTimeStamp,
                    SubstitutesSyncService.SyncAdapter.extraSingleDay to singleDay
            )

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
}