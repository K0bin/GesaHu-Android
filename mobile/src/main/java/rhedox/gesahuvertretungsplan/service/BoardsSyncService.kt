package rhedox.gesahuvertretungsplan.service

import android.accounts.Account
import android.app.Service
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.util.Log.d
import com.crashlytics.android.Crashlytics
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.model.api.BoardInfo
import rhedox.gesahuvertretungsplan.model.api.GesaHu
import rhedox.gesahuvertretungsplan.model.database.StubBoardsContentProvider
import rhedox.gesahuvertretungsplan.model.database.dao.BoardsDao
import rhedox.gesahuvertretungsplan.model.database.dao.LessonsDao
import rhedox.gesahuvertretungsplan.model.database.dao.MarksDao
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.model.database.entity.Mark
import rhedox.gesahuvertretungsplan.util.accountManager
import java.io.IOException
import javax.inject.Inject

/**
 * Created by robin on 30.10.2016.
 */
class BoardsSyncService : Service() {

    companion object {
        private var syncAdapter: SyncAdapter? = null;

        fun setIsSyncEnabled(account: Account, isEnabled: Boolean) {
            if(isEnabled) {
                ContentResolver.setIsSyncable(account, StubBoardsContentProvider.authority, 1);
                ContentResolver.setSyncAutomatically(account, StubBoardsContentProvider.authority, true);
                ContentResolver.addPeriodicSync(account, StubBoardsContentProvider.authority, Bundle.EMPTY, 24 * 60 * 60)
            } else {
                ContentResolver.setIsSyncable(account, StubBoardsContentProvider.authority, 0);
                ContentResolver.setSyncAutomatically(account, StubBoardsContentProvider.authority, false);
                ContentResolver.removePeriodicSync(account, StubBoardsContentProvider.authority, Bundle.EMPTY)
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

        @Inject internal lateinit var boardsDao: BoardsDao
        @Inject internal lateinit var lessonsDao: LessonsDao
        @Inject internal lateinit var marksDao: MarksDao

        init {
            (context.applicationContext as App)
                    .appComponent
                    .plusBoards()
                    .inject(this)
        }

        override fun onPerformSync(account: Account, extras: Bundle?, authority: String, provider: ContentProviderClient, syncResult: SyncResult?) {
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
                val boards = mutableListOf<Board>()
                val lessons = mutableListOf<Lesson>()
                val marks = mutableListOf<Mark>()

                //Insert new data
                val boardsInfo = response.body() ?: return
                for (board in boardsInfo) {
                    boards.add(board.board)
                    lessons.addAll(board.lessons)
                    marks.addAll(board.marks)
                }

                boardsDao.insertAndClear(*boards.toTypedArray())
                lessonsDao.insertAndClear(*lessons.toTypedArray())
                marksDao.insertAndClear(*marks.toTypedArray())

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
                val fos = context.openFileOutput(Board.avatarFileName, Context.MODE_PRIVATE)
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