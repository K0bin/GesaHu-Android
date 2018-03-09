package rhedox.gesahuvertretungsplan.service

import android.accounts.Account
import android.app.Service
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import org.joda.time.DateTimeConstants
import org.joda.time.DurationFieldType
import org.joda.time.LocalDate
import retrofit2.Response
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.api.GesaHu
import rhedox.gesahuvertretungsplan.model.api.SubstitutesList
import rhedox.gesahuvertretungsplan.model.database.StubSubstitutesContentProvider
import rhedox.gesahuvertretungsplan.model.database.dao.AnnouncementsDao
import rhedox.gesahuvertretungsplan.model.database.dao.SubstitutesDao
import rhedox.gesahuvertretungsplan.model.database.dao.SupervisionsDao
import rhedox.gesahuvertretungsplan.model.database.entity.Announcement
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute
import rhedox.gesahuvertretungsplan.model.database.entity.Supervision
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Created by robin on 18.10.2016.
 */
class SubstitutesSyncService : Service() {

    companion object {
        private var syncAdapter: SyncAdapter? = null;

        fun setIsSyncEnabled(account: Account, isEnabled: Boolean) {
            if(isEnabled) {
                ContentResolver.setIsSyncable(account, StubSubstitutesContentProvider.authority, 1);
                ContentResolver.setSyncAutomatically(account, StubSubstitutesContentProvider.authority, true);
                ContentResolver.addPeriodicSync(account, StubSubstitutesContentProvider.authority, Bundle.EMPTY, 2 * 60 * 60)
            } else {
                ContentResolver.setIsSyncable(account, StubSubstitutesContentProvider.authority, 0)
                ContentResolver.setSyncAutomatically(account, StubSubstitutesContentProvider.authority, false);
                ContentResolver.removePeriodicSync(account,  StubSubstitutesContentProvider.authority, Bundle.EMPTY)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        synchronized(Companion) {
            if (syncAdapter == null)
                syncAdapter = SyncAdapter(applicationContext, true)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return syncAdapter!!.syncAdapterBinder;
    }

    class SyncAdapter(context: Context, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context, autoInitialize, true) {

        private val gesaHu = GesaHu(context);

        private val substitutesDao = context.appKodein().instance<SubstitutesDao>()
        private val supervisionsDao = context.appKodein().instance<SupervisionsDao>()
        private val announcementsDao = context.appKodein().instance<AnnouncementsDao>()

        companion object {
            const val extraSingleDay = "day";
            const val extraDate = "date";
            const val extraIgnorePast = "extraIgnorePast";
        }

        override fun onPerformSync(account: Account, extras: Bundle?, authority: String, provider: ContentProviderClient, syncResult: SyncResult?) {
            if(Thread.interrupted()) {
                return;
            }
            val hasDate = extras?.containsKey(extraDate) ?: false
            val singleDay = extras?.getBoolean(extraSingleDay, false) ?: false
            val date = if(hasDate) localDateFromUnix(extras!!.getInt(extraDate)) else SchoolWeek.nextFromNow()

            val dates = mutableListOf<LocalDate>()
            val substitutes = mutableListOf<Substitute>()
            val supervisions = mutableListOf<Supervision>()
            val announcements = mutableListOf<Announcement>()

            if(hasDate && singleDay) {
                Log.d("SubstitutesSync", "Sync triggered for $date")
                val result = loadSubstitutesForDay(account, date)
                if (result != null) {
                    dates.add(result.date)
                    substitutes.addAll(result.substitutes)
                    supervisions.addAll(result.supervisions)
                    announcements.add(result.announcement)
                }
            } else {
                Log.d("SubstitutesSync", "Sync triggered for week starting with $date")

                val days = if (hasDate) 7 else 14;
                for (i in 0 until days) {
                    if(Thread.interrupted()) {
                        return;
                    }

                    val day = date.withFieldAdded(DurationFieldType.days(), i)
                    if (day.dayOfWeek == DateTimeConstants.SATURDAY || date.dayOfWeek == DateTimeConstants.SUNDAY) {
                        continue
                    }

                    val result = loadSubstitutesForDay(account, day)
                    if (result != null) {
                        dates.add(result.date)
                        substitutes.addAll(result.substitutes)
                        supervisions.addAll(result.supervisions)
                        announcements.add(result.announcement)
                    }
                }
            }

            if(Thread.interrupted()) {
                return;
            }
            val oldest = LocalDate.now().withFieldAdded(DurationFieldType.months(), -6);
            substitutesDao.insertAndClear(substitutes, oldest, dates)
            supervisionsDao.insertAndClear(supervisions, oldest, dates)
            announcementsDao.insertAndClear(announcements, oldest, dates)
        }

        private fun loadSubstitutesForDay(account: Account, date: LocalDate): SubstitutesList? {
            val call = gesaHu.substitutes(account.name ?: "", date)

            var response: Response<SubstitutesList>? = null
            try {
                response = call.execute()
            } catch (e: Exception) {
                if (e !is IOException && e !is SocketTimeoutException && !BuildConfig.DEBUG) {
                    Crashlytics.logException(e)
                } else {
                    if (e.message != null) {
                        Log.e("SubstitutesSync", e.message)
                    }
                }
            }
            if (response != null && response.code() == 403) {
                BoardsSyncService.setIsSyncEnabled(account, false)
                CalendarSyncService.setIsSyncEnabled(account, false)
                SubstitutesSyncService.setIsSyncEnabled(account, false)

                GesaHuAccountService.GesaHuAuthenticator.askForLogin(context)
                return null;
            }
            return response?.body();
        }
    }
}