package rhedox.gesahuvertretungsplan.service

import android.accounts.Account
import android.app.Service
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import com.pawegio.kandroid.accountManager
import rhedox.gesahuvertretungsplan.model.api.GesaHuApi
import rhedox.gesahuvertretungsplan.model.database.BoardsContentProvider
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.model.database.tables.BoardAdapter

/**
 * Created by robin on 30.10.2016.
 */
class BoardsSyncService : Service() {

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

    class SyncAdapter(context: Context, autoInitialize: Boolean): AbstractThreadedSyncAdapter(context, autoInitialize, false) {

        private val gesahu = GesaHuApi.create(context);

        override fun onPerformSync(account: Account, extras: Bundle?, authority: String, provider: ContentProviderClient, syncResult: SyncResult?) {
            val password = context.accountManager?.getPassword(account) ?: "";
            val call = gesahu.boards(account.name, password)
            val response = call.execute()
            if(response != null && response.isSuccessful) {
                val uri = Uri.Builder()
                        .scheme("content")
                        .authority(BoardsContentProvider.authority)
                        .build()

                provider.delete(uri, null, null);

                val boards = response.body()
                for(board in boards) {
                    provider.insert(uri, BoardAdapter.toContentValues(board))
                }
            }
        }

    }
}