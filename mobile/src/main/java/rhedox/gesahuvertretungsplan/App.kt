package rhedox.gesahuvertretungsplan

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.StrictMode
import android.provider.CalendarContract
import android.util.Log
import com.facebook.stetho.InspectorModulesProvider
import com.facebook.stetho.Stetho
import com.facebook.stetho.inspector.database.ContentProviderDatabaseDriver
import com.facebook.stetho.inspector.database.ContentProviderSchema
import com.facebook.stetho.inspector.protocol.ChromeDevtoolsDomain
import com.google.firebase.FirebaseApp
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import net.danlew.android.joda.JodaTimeAndroid
import rhedox.gesahuvertretungsplan.dependencyInjection.AppComponent
import rhedox.gesahuvertretungsplan.dependencyInjection.DaggerAppComponent
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment
import javax.inject.Inject


@Suppress("MemberVisibilityCanBePrivate")
/**
 * Created by Robin on 29.06.2015.
 */
class App : Application() {

    public var refWatcher: RefWatcher? = null
        private set;

    public lateinit var appComponent: AppComponent
        private set;

    @Inject lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
                .application(this)
                .build()
        appComponent.inject(this)

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        FirebaseApp.initializeApp(this)

        //Disable Firebase crash reporting
        if (BuildConfig.DEBUG) {
            Thread.setDefaultUncaughtExceptionHandler { _, paramThrowable ->
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

        initWithDependencies();
    }

    private fun initWithDependencies() {
        PreferenceFragment.applyDarkTheme(prefs)
    }

    companion object {
        @JvmStatic
        fun getRefWatcher(context: Context): RefWatcher? {
            return if (BuildConfig.DEBUG)
                (context.applicationContext as App).refWatcher
            else
                null
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
