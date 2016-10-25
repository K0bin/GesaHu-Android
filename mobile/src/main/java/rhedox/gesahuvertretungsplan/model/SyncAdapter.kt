package rhedox.gesahuvertretungsplan.model

import android.accounts.Account
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.pawegio.kandroid.accountManager
import org.joda.time.*
import retrofit2.Call
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.model.api.GesaHuApi
import rhedox.gesahuvertretungsplan.model.api.toQueryDate
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.model.database.tables.AnnouncementAdapter
import rhedox.gesahuvertretungsplan.model.database.tables.SubstituteAdapter
import java.io.IOError
import java.io.IOException
import java.sql.ClientInfoStatus

/**
 * Created by robin on 11.10.2016.
 */

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

        val date = if(extras != null && extras.containsKey(extraDate)) DateTime(extras.getLong(extraDate)).toLocalDate() else SchoolWeek.nextFromNow()

        if(extras?.getBoolean(extraSingleDay, false) ?: false) {
            Log.d("SyncAdapter", "Sync triggered for $date")
            loadSubstitutesForDay(provider, date, username)
        } else {
            Log.d("SyncAdapter", "Sync triggered")
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

                loadSubstitutesForDay(provider, day, username)
            }
        }
    }

    fun clearOldSubstitutes(provider: ContentProviderClient) {
        val oldest = LocalDate.now().withFieldAdded(DurationFieldType.months(), -6);

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

        provider.delete(substitutesUri, "date < ${oldest.toDateTime(LocalTime(0)).millis}", null);
        provider.delete(announcementsUri, "date < ${oldest.toDateTime(LocalTime(0)).millis}", null);
    }

    fun loadSubstitutesForDay(provider:ContentProviderClient, date: LocalDate, username: String) {
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
                provider.delete(substitutesUri, "date = ${substitutesList.date.toDateTime(LocalTime(0)).millis}", null);
                provider.delete(announcementsUri, "date = ${substitutesList.date.toDateTime(LocalTime(0)).millis}", null);

                val substituteInserts = mutableListOf<ContentValues>()
                for (substitute in substitutesList.substitutes) {
                    substituteInserts.add(SubstituteAdapter.toContentValues(substitute, substitutesList.date))
                }
                if(substitutesList.announcement.trim().length != 0 && substitutesList.announcement.trim() != "keine")
                    provider.insert(announcementsUri, AnnouncementAdapter.toContentValues(substitutesList.announcement, substitutesList.date));

                provider.bulkInsert(substitutesUri, substituteInserts.toTypedArray())
            }
        } catch (e: IOException) {
            Log.e("GesaHu Substitutes Sync", e.message)
        }
    }
}