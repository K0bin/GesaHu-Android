package rhedox.gesahuvertretungsplan.service

import android.accounts.Account
import android.app.Service
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.crashlytics.android.Crashlytics
import org.joda.time.DateTimeConstants
import org.joda.time.DurationFieldType
import org.joda.time.LocalDate
import retrofit2.Response
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.api.GesaHu
import rhedox.gesahuvertretungsplan.model.api.SubstitutesList
import rhedox.gesahuvertretungsplan.model.api.Test
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.model.database.tables.*
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp
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
                ContentResolver.setIsSyncable(account, SubstitutesContentProvider.authority, 1);
                ContentResolver.setSyncAutomatically(account, SubstitutesContentProvider.authority, true);
                ContentResolver.addPeriodicSync(account, SubstitutesContentProvider.authority, Bundle.EMPTY, 2 * 60 * 60)
            } else {
                ContentResolver.setIsSyncable(account, SubstitutesContentProvider.authority, 0)
                ContentResolver.setSyncAutomatically(account, SubstitutesContentProvider.authority, false);
                ContentResolver.removePeriodicSync(account,  SubstitutesContentProvider.authority, Bundle.EMPTY)
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

        private var gesaHu = GesaHu(context);

        companion object {
            const val extraSingleDay = "day";
            const val extraDate = "date";
            const val extraIgnorePast = "extraIgnorePast";
        }

        override fun onPerformSync(account: Account, extras: Bundle?, authority: String, provider: ContentProviderClient, syncResult: SyncResult?) {
            //android.os.Debug.waitForDebugger();

            if(Thread.interrupted()) {
                return;
            }
            val hasDate = extras?.containsKey(extraDate) ?: false
            val singleDay = extras?.getBoolean(extraSingleDay, false) ?: false
            val date = if(hasDate) localDateFromUnix(extras!!.getInt(extraDate)) else SchoolWeek.nextFromNow()

            if(hasDate && singleDay) {
                Log.d("SubstitutesSync", "Sync triggered for $date")
                loadSubstitutesForDay(provider, account, date)
            } else {
                Log.d("SubstitutesSync", "Sync triggered for week starting with $date")
                val ignorePast = (extras?.getBoolean(extraIgnorePast, false) ?: false) && date > LocalDate.now();

                clearOldSubstitutes(provider);

                val days = if (hasDate) 7 else 14;
                for (i in 0..days-1) {
                    if(Thread.interrupted()) {
                        return;
                    }

                    var day = date.withFieldAdded(DurationFieldType.days(), i)
                    if (day.dayOfWeek == DateTimeConstants.SATURDAY || date.dayOfWeek == DateTimeConstants.SUNDAY) {
                        //Saturday => Monday & Sunday => Tuesday
                        day = date.withFieldAdded(DurationFieldType.days(), 2);
                    }
                    if(ignorePast && day < LocalDate.now())
                        continue
                    Log.d("SubstitutesSync", "Synced $day")
                    val isSuccessful = loadSubstitutesForDay(provider,account, day)
                    if (!isSuccessful) {
                        return;
                    }
                }
            }
        }

        private fun clearOldSubstitutes(provider: ContentProviderClient) {
            val oldest = LocalDate.now().withFieldAdded(DurationFieldType.months(), -6);

            provider.delete(SubstitutesContract.uri, "date < ${oldest.unixTimeStamp}", null);
            provider.delete(AnnouncementsContract.uri, "date < ${oldest.unixTimeStamp}", null);
            provider.delete(SupervisionsContract.uri, "date < ${oldest.unixTimeStamp}", null);
        }

        private fun loadSubstitutesForDay(provider: ContentProviderClient, account: Account, date: LocalDate): Boolean {
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
            if (response != null && response.isSuccessful) {
                val substitutesList = response.body() ?: return false;

                provider.delete(SubstitutesContract.uri, "date = ${substitutesList.date.unixTimeStamp}", null);
                provider.delete(AnnouncementsContract.uri, "date = ${substitutesList.date.unixTimeStamp}", null);
                provider.delete(SupervisionsContract.uri, "date = ${substitutesList.date.unixTimeStamp}", null);

                val substituteInserts = mutableListOf<ContentValues>()
                for (substitute in substitutesList.substitutes) {
                    substituteInserts.add(SubstituteAdapter.toContentValues(substitute, substitutesList.date))
                }
                if(substitutesList.announcement.isNotEmpty() && substitutesList.announcement.trim() != "keine") {
                    Log.d("SubstitutesSync", "Inserted an announcement.")
                    provider.insert(AnnouncementsContract.uri, AnnouncementAdapter.toContentValues(substitutesList.announcement, substitutesList.date));
                }
                val supervisionInserts = mutableListOf<ContentValues>()
                for (supervision in substitutesList.supervisions) {
                    supervisionInserts.add(SupervisionAdapter.toContentValues(supervision, substitutesList.date))
                }

                val substituteCount = provider.bulkInsert(SubstitutesContract.uri, substituteInserts.toTypedArray())
                Log.d("SubstitutesSync", "Inserted $substituteCount substitutes.")

                val supervisionCount = provider.bulkInsert(SupervisionsContract.uri, supervisionInserts.toTypedArray())
                Log.d("SubstitutesSync", "Inserted $supervisionCount supervisions.")
                return true;
            } else if (response != null && response.code() == 403) {
                BoardsSyncService.setIsSyncEnabled(account, false)
                CalendarSyncService.setIsSyncEnabled(account, false)
                SubstitutesSyncService.setIsSyncEnabled(account, false)

                GesaHuAccountService.GesaHuAuthenticator.askForLogin(context)
                return false;
            }
            return false;
        }
    }
}