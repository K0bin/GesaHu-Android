package rhedox.gesahuvertretungsplan.service

import android.accounts.Account
import android.app.Service
import android.content.*
import android.net.Uri
import android.os.*
import android.util.Log
import android.util.Log.d
import com.crashlytics.android.Crashlytics
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.accountManager
import retrofit2.Response
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.api.BoardInfo
import rhedox.gesahuvertretungsplan.model.api.GesaHu
import rhedox.gesahuvertretungsplan.model.database.BoardsContentProvider
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.model.database.tables.*
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Created by robin on 30.10.2016.
 */
class BoardsSyncService : Service() {

    companion object {
        private var syncAdapter: SyncAdapter? = null;

        fun setIsSyncEnabled(account: Account, isEnabled: Boolean) {
            if(isEnabled) {
                ContentResolver.setIsSyncable(account, BoardsContentProvider.authority, 1);
                ContentResolver.setSyncAutomatically(account, BoardsContentProvider.authority, true);
                ContentResolver.addPeriodicSync(account, BoardsContentProvider.authority, Bundle.EMPTY, 24 * 60 * 60)
            } else {
                ContentResolver.setIsSyncable(account, BoardsContentProvider.authority, 0);
                ContentResolver.setSyncAutomatically(account, BoardsContentProvider.authority, false);
                ContentResolver.removePeriodicSync(account, BoardsContentProvider.authority, Bundle.EMPTY)
            }
        }

        fun getPhotoUrl(username: String): String = "https://www.gesahui.de/home/schoolboard/userbilder/${username.toUpperCase()}.jpg"
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

        private val gesahu = GesaHu(context);

        override fun onPerformSync(account: Account, extras: Bundle?, authority: String, provider: ContentProviderClient, syncResult: SyncResult?) {
            //android.os.Debug.waitForDebugger()

            if(Thread.interrupted()) {
                return;
            }

            val password = context.accountManager.getPassword(account) ?: "";
            val call = gesahu.boards(account.name, password)
            var response: Response<List<BoardInfo>>? = null
            try {
                response = call.execute()
            } catch (e: Exception) {
                if (e !is IOException && !BuildConfig.DEBUG) {
                    Crashlytics.logException(e)
                } else {
                    if (e.message != null) {
                        Log.e("BoardsSync", e.message)
                    }
                }
            }
            if(Thread.interrupted()) {
                return;
            }
            if(response != null && response.isSuccessful) {
                //Clear tables
                provider.delete(BoardsContract.uri, null, null);
                provider.delete(LessonsContract.uri, null, null);
                provider.delete(MarksContract.uri, null, null);

                //Insert new data
                val boards = response.body() ?: return
                for (board in boards) {
                    val uri = provider.insert(BoardsContract.uri, BoardsAdapter.toContentValues(board.board))
                    val id = uri.lastPathSegment.toLong()
                    for (lesson in board.lessons) {
                        provider.insert(LessonsContract.uri, LessonsAdapter.toContentValues(lesson, id))
                    }
                    for (mark in board.marks) {
                        provider.insert(MarksContract.uri, MarksAdapter.toContentValues(mark, id))
                    }
                }
            } else if (response != null && response.code() == 403) {
                BoardsSyncService.setIsSyncEnabled(account, false)
                CalendarSyncService.setIsSyncEnabled(account, false)
                SubstitutesSyncService.setIsSyncEnabled(account, false)

                GesaHuAccountService.GesaHuAuthenticator.askForLogin(context)
                return;
            }

            if(Thread.interrupted()) {
                return;
            }
            val okHttp = OkHttpClient();
            val wasAvatarSuccessful = loadImage(okHttp, BoardsSyncService.getPhotoUrl(account.name))
            if (!wasAvatarSuccessful && !Thread.interrupted()) {
                val future = context.accountManager.hasFeatures(account, arrayOf(GesaHuAccountService.GesaHuAuthenticator.Feature.originalUserpicture), null, null);
                while (!future.isDone) {
                    //Block thread
                    if (Thread.interrupted())
                        return;
                }
                if (future.result) {
                    loadImage(okHttp, "https://www.gesahui.de/home/schoolboard/userbilder_original/${account.name.toUpperCase()}.jpg")
                }
            }
        }

        private fun loadImage(okHttp: OkHttpClient, url: String): Boolean {
            d("BoardsSyncService", "Downloading image: $url");
            val avatarRequest = Request.Builder()
                    .url(url)
                    .build()
            val avatarCall = okHttp.newCall(avatarRequest)
            var avatarResponse: okhttp3.Response? = null;
            var bytes: ByteArray? = null
            try {
                avatarResponse = avatarCall.execute()
                bytes = avatarResponse.body()?.bytes();
            } catch (e: IOException) {}
            if(avatarResponse != null && avatarResponse.isSuccessful && bytes != null) {
                val fos = context.openFileOutput(BoardsContract.avatarFileName, Context.MODE_PRIVATE)
                fos.write(bytes)
                fos.close()

                avatarResponse.close()
                return true;
            }
            avatarResponse?.close()
            return false;
        }

    }
}