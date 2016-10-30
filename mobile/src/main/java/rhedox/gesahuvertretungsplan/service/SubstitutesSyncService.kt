package rhedox.gesahuvertretungsplan.service

import android.accounts.Account
import android.app.Service
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import org.joda.time.DateTimeConstants
import org.joda.time.DurationFieldType
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.api.GesaHuApi
import rhedox.gesahuvertretungsplan.model.api.toQueryDate
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.model.database.tables.AnnouncementAdapter
import rhedox.gesahuvertretungsplan.model.database.tables.SubstituteAdapter
import rhedox.gesahuvertretungsplan.model.localDateFromUnix
import rhedox.gesahuvertretungsplan.model.unixTimeStamp
import java.io.IOException

/**
 * Created by robin on 18.10.2016.
 */
class SubstitutesSyncService : Service() {

    companion object {
        private var syncAdapter: SyncAdapter? = null;
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

        private var gesaHu: GesaHuApi;

        init {
            gesaHu = GesaHuApi.create(context)
        }

        companion object {
            const val extraSingleDay = "day";
            const val extraDate = "date";
            const val extraIgnorePast = "extraIgnorePast";
        }

        override fun onPerformSync(account: Account, extras: Bundle?, authority: String, provider: ContentProviderClient, syncResult: SyncResult?) {
            val username = account.name ?: "";

            val date = if(extras != null && extras.containsKey(extraDate)) localDateFromUnix(extras.getInt(extraDate)) else SchoolWeek.nextFromNow()

            if(extras?.getBoolean(extraSingleDay, false) ?: false) {
                Log.d("SubstitutesSync", "Sync triggered for $date")
                loadSubstitutesForDay(provider, date, username)
            } else {
                Log.d("SubstitutesSync", "Sync triggered for week starting with $date")
                val ignorePast = (extras?.getBoolean(extraIgnorePast, false) ?: false) && date > LocalDate.now();

                clearOldSubstitutes(provider);

                for (i in 0..7) {
                    var day = date.withFieldAdded(DurationFieldType.days(), i)
                    if (day.dayOfWeek == DateTimeConstants.SATURDAY || date.dayOfWeek == DateTimeConstants.SUNDAY) {
                        //Saturday => Monday & Sunday => Tuesday
                        day = date.withFieldAdded(DurationFieldType.days(), 2);
                    }
                    if(ignorePast && day < LocalDate.now())
                        continue
                    Log.d("SubstitutesSync", "Synced $day")
                }
            }
        }

        fun clearOldSubstitutes(provider: ContentProviderClient) {
            val oldest = LocalDate.now().withFieldAdded(DurationFieldType.months(), -6);

            val substitutesUri = Uri.Builder()
                    .scheme("content")
                    .authority(SubstitutesContentProvider.authority)
                    .path(SubstitutesContentProvider.substitutesPath)
                    .build()

            val announcementsUri = Uri.Builder()
                    .scheme("content")
                    .authority(SubstitutesContentProvider.authority)
                    .path(SubstitutesContentProvider.announcementsPath)
                    .build()

            provider.delete(substitutesUri, "date < ${oldest.unixTimeStamp}", null);
            provider.delete(announcementsUri, "date < ${oldest.unixTimeStamp}", null);
        }

        fun loadSubstitutesForDay(provider: ContentProviderClient, date: LocalDate, username: String): Boolean {
            val call = gesaHu.substitutes(date.toQueryDate(), username)

            val substitutesUri = Uri.Builder()
                    .scheme("content")
                    .authority(SubstitutesContentProvider.authority)
                    .path(SubstitutesContentProvider.substitutesPath)
                    .build();

            val announcementsUri = Uri.Builder()
                    .scheme("content")
                    .authority(SubstitutesContentProvider.authority)
                    .path(SubstitutesContentProvider.announcementsPath)
                    .build()


            try {
                val response = call.execute();
                if (response != null && response.isSuccessful) {
                    val substitutesList = response.body();
                    provider.delete(substitutesUri, "date = ${substitutesList.date.unixTimeStamp}", null);
                    provider.delete(announcementsUri, "date = ${substitutesList.date.unixTimeStamp}", null);

                    val substituteInserts = mutableListOf<ContentValues>()
                    for (substitute in substitutesList.substitutes) {
                        substituteInserts.add(SubstituteAdapter.toContentValues(substitute, substitutesList.date))
                    }
                    if(substitutesList.announcement.trim().length != 0 && substitutesList.announcement.trim() != "keine") {
                        Log.d("SubstitutesSync", "Inserted an announcement.")
                        provider.insert(announcementsUri, AnnouncementAdapter.toContentValues(substitutesList.announcement, substitutesList.date));
                    }

                    val count = provider.bulkInsert(substitutesUri, substituteInserts.toTypedArray())
                    Log.d("SubstitutesSync", "Inserted $count substitutes.")
                    return true;
                }
            } catch (e: IOException) {
                Log.e("GesaHu Substitutes Sync", e.message)
            }
            return false;
        }
    }
}