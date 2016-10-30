package rhedox.gesahuvertretungsplan;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.facebook.stetho.Stetho;
import com.frogermcs.androiddevmetrics.AndroidDevMetrics;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Robin on 29.06.2015.
 */
public class App extends Application {
	public static final String PREFERENCES_LOGIN = "rhedox.gesahuvertretungsplan.login";
	public static final String ACCOUNT_TYPE = "rhedox.gesahuvertretungsplan.gesaHuAccount";

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
					.penaltyDialog()
					.build();
			//StrictMode.setThreadPolicy(threadPolicy);

			AndroidDevMetrics.initWith(this);
			Stetho.initializeWithDefaults(this);

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
}
