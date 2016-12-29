package rhedox.gesahuvertretungsplan;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.StrictMode;
import android.provider.CalendarContract;

import com.facebook.stetho.InspectorModulesProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.inspector.database.ContentProviderDatabaseDriver;
import com.facebook.stetho.inspector.database.ContentProviderSchema;
import com.facebook.stetho.inspector.protocol.ChromeDevtoolsDomain;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Robin on 29.06.2015.
 */
public class App extends Application {
	public static final String PREFERENCES_LOGIN = "rhedox.gesahuvertretungsplan.login";

	private RefWatcher refWatcher;

	public static final boolean ANALYTICS = true;

	@Override
	public void onCreate() {
		super.onCreate();

		JodaTimeAndroid.init(this);

		//Debug
		if(BuildConfig.DEBUG) {
			StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
					.detectAll()
					.penaltyLog()
					.build();
			StrictMode.setVmPolicy(policy);

			StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder()
					.detectAll()
					.penaltyLog()
					/*.penaltyDialog()*/
					.build();
			StrictMode.setThreadPolicy(threadPolicy);
			Stetho.initialize(Stetho.newInitializerBuilder(this)
					.enableWebKitInspector(new ExtInspectorModulesProvider(this))
					.build());

			refWatcher = LeakCanary.install(this);
		} else {
			StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
					.detectAll()
					.penaltyLog()
					.build();
			StrictMode.setVmPolicy(policy);

			StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder()
					.detectAll()
					.penaltyLog()
					.build();
			StrictMode.setThreadPolicy(threadPolicy);
		}
	}

	public static RefWatcher getRefWatcher(Context context) {
		if(BuildConfig.DEBUG)
			return ((App)context.getApplicationContext()).refWatcher;
		else
			return null;
	}

	private static class ExtInspectorModulesProvider implements InspectorModulesProvider {

		private Context mContext;

		ExtInspectorModulesProvider(Context context) {
			mContext = context;
		}

		@Override
		public Iterable<ChromeDevtoolsDomain> get() {
			return new Stetho.DefaultInspectorModulesBuilder(mContext)
					.provideDatabaseDriver(createContentProviderDatabaseDriver(mContext))
					.finish();
		}

		private ContentProviderDatabaseDriver createContentProviderDatabaseDriver(Context context) {
			ContentProviderSchema calendarsSchema = new ContentProviderSchema.Builder()
					.table(new ContentProviderSchema.Table.Builder()
							.uri(CalendarContract.Calendars.CONTENT_URI)
							.projection(new String[] {
									CalendarContract.Calendars._ID,
									CalendarContract.Calendars.NAME,
									CalendarContract.Calendars.ACCOUNT_NAME,
									CalendarContract.Calendars.ACCOUNT_TYPE,
									CalendarContract.Calendars.OWNER_ACCOUNT,
									CalendarContract.Calendars.CALENDAR_COLOR,
									CalendarContract.Calendars._SYNC_ID,
									CalendarContract.Calendars.CALENDAR_TIME_ZONE,
									CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
									CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
									CalendarContract.Calendars.SYNC_EVENTS,
									CalendarContract.Calendars.IS_PRIMARY,
							})
							.build())
					.build();

			// sample events content provider we want to support
			ContentProviderSchema eventsSchema = new ContentProviderSchema.Builder()
					.table(new ContentProviderSchema.Table.Builder()
							.uri(CalendarContract.Events.CONTENT_URI)
							.projection(new String[]{
									CalendarContract.Events._ID,
									CalendarContract.Events.TITLE,
									CalendarContract.Events.DESCRIPTION,
									CalendarContract.Events.ACCOUNT_NAME,
									CalendarContract.Events.DTSTART,
									CalendarContract.Events.DTEND,
									CalendarContract.Events.CALENDAR_ID,
							})
							.build())
					.build();
			return new ContentProviderDatabaseDriver(context, calendarsSchema, eventsSchema);
		}
	}

}
