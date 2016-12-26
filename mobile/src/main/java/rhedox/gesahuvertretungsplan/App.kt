package rhedox.gesahuvertretungsplan

import android.accounts.AccountManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.StrictMode
import android.preference.PreferenceManager

import com.facebook.stetho.Stetho
import com.github.salomonbrys.kodein.*
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

import net.danlew.android.joda.JodaTimeAndroid
import org.jetbrains.anko.accountManager
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.SyncObserver
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.SubstitutesRepository

/**
 * Created by Robin on 29.06.2015.
 */
class App : Application(), KodeinAware {
    override val kodein by Kodein.lazy {
        bind<SharedPreferences>() with instance ( PreferenceManager.getDefaultSharedPreferences(applicationContext) )
        bind<BoardsRepository>() with provider { BoardsRepository(applicationContext) }
        bind<SubstitutesRepository>() with provider { SubstitutesRepository(applicationContext) }
        bind<SyncObserver>() with provider { SyncObserver() }
        bind<AccountManager>() with instance (applicationContext.accountManager)
        bind<AvatarLoader>() with provider { AvatarLoader(applicationContext) }
    }

    private var refWatcher: RefWatcher? = null

    override fun onCreate() {
        super.onCreate()

        JodaTimeAndroid.init(this)

        //Debug
        if (BuildConfig.DEBUG) {
            val policy = StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            StrictMode.setVmPolicy(policy)

            val threadPolicy = StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDialog()
                    .build()
            StrictMode.setThreadPolicy(threadPolicy)
            Stetho.initializeWithDefaults(this)

            refWatcher = LeakCanary.install(this)
        } else {
            val policy = StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            StrictMode.setVmPolicy(policy)

            val threadPolicy = StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            StrictMode.setThreadPolicy(threadPolicy)
        }
    }

    companion object {
        @JvmStatic
        val PREFERENCES_LOGIN = "rhedox.gesahuvertretungsplan.login"

        @JvmStatic
        val ANALYTICS = true

        @JvmStatic
        fun getRefWatcher(context: Context): RefWatcher? {
            if (BuildConfig.DEBUG)
                return (context.applicationContext as App).refWatcher
            else
                return null
        }
    }
}
