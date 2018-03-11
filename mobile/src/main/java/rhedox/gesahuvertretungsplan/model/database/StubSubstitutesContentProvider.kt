package rhedox.gesahuvertretungsplan.model.database

import android.accounts.Account
import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.SyncRequest
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.os.bundleOf
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.service.SubstitutesSyncService
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 08.03.2018.
 */
class StubSubstitutesContentProvider: ContentProvider() {
    companion object {
        const val authority = "rhedox.gesahuvertretungsplan.substitutes.stub"

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

    override fun insert(uri: Uri?, values: ContentValues?): Uri? = null
    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? = null
    override fun onCreate(): Boolean = true
    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun getType(uri: Uri?): String? = null
}