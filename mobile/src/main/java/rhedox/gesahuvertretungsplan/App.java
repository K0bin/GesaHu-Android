package rhedox.gesahuvertretungsplan;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import net.danlew.android.joda.JodaTimeAndroid;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;

/**
 * Created by Robin on 29.06.2015.
 */

@ReportsCrashes(
        formUri = "https://collector.tracepot.com/84f365ea"
)
public class App extends Application {
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        refWatcher = LeakCanary.install(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceFragment.applyDarkTheme(prefs);

        //Debug
        if(BuildConfig.DEBUG) {
            StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .setClassInstanceLimit(MainActivity.class, 12)
                    .penaltyDeath()
                    .build();
            StrictMode.setVmPolicy(policy);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        if(!BuildConfig.DEBUG)
            ACRA.init(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        App application = (App) context.getApplicationContext();
        return application.refWatcher;
    }
}

//https://code.google.com/p/android/issues/detail?id=77712
//https://code.google.com/p/android/issues/detail?id=183783
