package rhedox.gesahuvertretungsplan;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.frogermcs.androiddevmetrics.AndroidDevMetrics;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;

/**
 * Created by Robin on 29.06.2015.
 */
public class App extends Application {
	private RefWatcher refWatcher;

	public static final boolean ANALYTICS_ENABLED = true;

	@Override
	public void onCreate() {
		super.onCreate();

		//Debug
		if(BuildConfig.DEBUG) {
			StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
					.detectAll()
					.penaltyLog()
					.setClassInstanceLimit(MainActivity.class, 12)
					.penaltyLog()
					.build();
			StrictMode.setVmPolicy(policy);

			AndroidDevMetrics.initWith(this);

			refWatcher = LeakCanary.install(this);
		}
	}

	public static RefWatcher getRefWatcher(Context context) {
		if(BuildConfig.DEBUG)
			return ((App)context.getApplicationContext()).refWatcher;
		else
			return null;
	}
}
