package rhedox.gesahuvertretungsplan

import android.accounts.AccountManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.StrictMode
import android.preference.PreferenceManager
import android.provider.CalendarContract
import com.facebook.stetho.InspectorModulesProvider

import com.facebook.stetho.Stetho
import com.facebook.stetho.inspector.database.ContentProviderDatabaseDriver
import com.facebook.stetho.inspector.database.ContentProviderSchema
import com.facebook.stetho.inspector.protocol.ChromeDevtoolsDomain
import com.github.salomonbrys.kodein.*
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

import net.danlew.android.joda.JodaTimeAndroid
import org.jetbrains.anko.accountManager
import org.jetbrains.anko.connectivityManager
import rhedox.gesahuvertretungsplan.model.*
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.SubstitutesRepository
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment
import rhedox.gesahuvertretungsplan.util.PermissionManager
import android.util.Log
import com.google.firebase.FirebaseApp

/**
 * Created by Robin on 29.06.2015.
 */
class App : Application(), KodeinAware {
    override val kodein by Kodein.lazy {
        bind<SharedPreferences>() with instance(PreferenceManager.getDefaultSharedPreferences(applicationContext))
        bind<BoardsRepository>() with provider { BoardsRepository(applicationContext) }
        bind<SyncObserver>() with provider { SyncObserver() }
        bind<AvatarLoader>() with provider { AvatarLoader(applicationContext) }
        bind<PermissionManager>() with instance(PermissionManager(applicationContext))
        bind<ConnectivityManager>() with instance(applicationContext.connectivityManager)
        bind<AccountManager>() with instance(applicationContext.accountManager)

        //Substitute
        bind<SubstitutesRepository>() with provider { SubstitutesRepository(applicationContext) }
        bind<SubstituteFormatter>() with singleton { SubstituteFormatter(applicationContext) }
    }

    private var refWatcher: RefWatcher? = null

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        FirebaseApp.initializeApp(this)

        //Disable Firebase crash reporting
        if (BuildConfig.DEBUG) {
            Thread.setDefaultUncaughtExceptionHandler { paramThread, paramThrowable ->
                Log.wtf("Alert", paramThrowable.message, paramThrowable)
                System.exit(2) //Prevents the service/app from freezing
            }
        }

        JodaTimeAndroid.init(this)

        //Debug
        if (BuildConfig.DEBUG) {
            val policy = StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            StrictMode.setVmPolicy(policy)

            val threadPolicy = StrictMode.ThreadPolicy.Builder()
                    //.detectAll()
                    .detectDiskWrites()
                    .detectCustomSlowCalls()
                    .detectNetwork()
                    .penaltyLog()
                    .build()
            StrictMode.setThreadPolicy(threadPolicy)
            Stetho.initialize(Stetho.newInitializerBuilder(this)
                    .enableWebKitInspector(ExtInspectorModulesProvider(this))
                    .build())

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

        val pref: SharedPreferences = kodein.instance()
        PreferenceFragment.applyDarkTheme(pref)
    }

    companion object {
        @JvmStatic
        fun getRefWatcher(context: Context): RefWatcher? {
            if (BuildConfig.DEBUG)
                return (context.applicationContext as App).refWatcher
            else
                return null
        }

        fun checkNightMode(context: Context): Boolean {
            val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return currentNightMode == Configuration.UI_MODE_NIGHT_YES
        }
    }

    private class ExtInspectorModulesProvider internal constructor(private val context: Context) : InspectorModulesProvider {

        override fun get(): Iterable<ChromeDevtoolsDomain> {
            return Stetho.DefaultInspectorModulesBuilder(context)
                    .provideDatabaseDriver(createContentProviderDatabaseDriver(context))
                    .finish()
        }

        private fun createContentProviderDatabaseDriver(context: Context): ContentProviderDatabaseDriver {
            val calendarsSchema = ContentProviderSchema.Builder()
                    .table(ContentProviderSchema.Table.Builder()
                            .uri(CalendarContract.Calendars.CONTENT_URI)
                            .projection(arrayOf(CalendarContract.Calendars._ID, CalendarContract.Calendars.NAME, CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.Calendars.OWNER_ACCOUNT, CalendarContract.Calendars.CALENDAR_COLOR, CalendarContract.Calendars._SYNC_ID, CalendarContract.Calendars.CALENDAR_TIME_ZONE, CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.SYNC_EVENTS, CalendarContract.Calendars.IS_PRIMARY))
                            .build())
                    .build()

            // sample events content provider we want to support
            val eventsSchema = ContentProviderSchema.Builder()
                    .table(ContentProviderSchema.Table.Builder()
                            .uri(CalendarContract.Events.CONTENT_URI)
                            .projection(arrayOf(CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.ACCOUNT_NAME, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.CALENDAR_ID))
                            .build())
                    .build()
            return ContentProviderDatabaseDriver(context, calendarsSchema, eventsSchema)
        }
    }
}
