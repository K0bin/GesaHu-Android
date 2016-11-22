package rhedox.gesahuvertretungsplan.service

import android.accounts.Account
import android.app.Service
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log.d
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.accountManager
import rhedox.gesahuvertretungsplan.model.api.GesaHuApi
import rhedox.gesahuvertretungsplan.model.database.BoardsContentProvider
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.model.database.tables.BoardAdapter
import rhedox.gesahuvertretungsplan.model.database.tables.BoardsContract

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
            if(Thread.interrupted()) {
                return;
            }

            val password = context.accountManager.getPassword(account) ?: "";
            val call = gesahu.boards(account.name, password)
            val response = call.execute()
            if(response != null && response.isSuccessful) {
                provider.delete(BoardsContract.uri, null, null);

                val boards = response.body()
                for(board in boards) {
                    provider.insert(BoardsContract.uri, BoardAdapter.toContentValues(board))
                }
            }

            if(Thread.interrupted()) {
                return;
            }
            val okHttp = OkHttpClient();
            val hasAvatar = loadImage(okHttp, "http://gesahui.de/home/schoolboard/userbilder/${account.name.toUpperCase()}.jpg")

            if(!hasAvatar && !Thread.interrupted()) {
                val future = context.accountManager.hasFeatures(account, arrayOf(GesaHuAccountService.GesaHuAuthenticator.Feature.originalUserpictur),  null, null);
                while(!future.isDone) {
                    //Block thread
                    if(Thread.interrupted())
                        return;
                }
                if(future.result) {
                    loadImage(okHttp, "http://gesahui.de/home/schoolboard/userbilder_original/${account.name.toUpperCase()}.jpg")
                }
            }
        }

        private fun loadImage(okHttp: OkHttpClient, url: String): Boolean {
            d("BoardsSyncService", "Downloading image: $url");
            val avatarRequest = Request.Builder()
                    .url(url)
                    .build()
            val avatarResponse = okHttp.newCall(avatarRequest).execute()
            if(avatarResponse != null && avatarResponse.isSuccessful) {
                val bytes = avatarResponse.body().bytes();

                if(bytes != null) {
                    val fos = context.openFileOutput(BoardsContract.avatarFileName, Context.MODE_PRIVATE)
                    fos.write(bytes)
                    fos.close()

                    return true;
                }
            }
            return false;
        }

    }
}