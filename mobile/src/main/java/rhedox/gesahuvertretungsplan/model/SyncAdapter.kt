package rhedox.gesahuvertretungsplan.model

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
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

/**
 * Created by robin on 11.10.2016.
 */

class SyncAdapter(context: Context, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context, autoInitialize, true) {

    private var gesaHu: GesaHuApi;

    init {
        gesaHu = GesaHuApi.create(context)
    }

    override fun onPerformSync(account: Account, extras: Bundle?, authority: String, provider: ContentProviderClient, syncResult: SyncResult?) {
        clearOldSubstitutes(provider);

        val username = account.name ?: "";

        //val today = SchoolWeek.next()
        val today = LocalDate(2016,10,10);
        for(i in 0..6) {
            var date = today.withFieldAdded(DurationFieldType.days(), i)
            if(date.dayOfWeek == DateTimeConstants.SATURDAY || date.dayOfWeek == DateTimeConstants.SUNDAY) {
                //Saturday => Monday & Sunday => Tuesday
                date = date.withFieldAdded(DurationFieldType.days(), 2);
            }

            loadSubstitutesForDay(provider, date, username)
        }
    }

    fun clearOldSubstitutes(provider: ContentProviderClient) {
        val oldest = LocalDate.now().withFieldAdded(DurationFieldType.months(), -1);

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

                for (substitute in substitutesList.substitutes) {
                    provider.insert(substitutesUri, SubstituteAdapter.toContentValues(substitute, substitutesList.date));
                    provider.insert(announcementsUri, AnnouncementAdapter.toContentValues(substitutesList.announcement, substitutesList.date));
                }
            }
        } catch (e: IOException) {
            Log.e("GesaHu Substitutes Sync", e.message)
        }
    }
}