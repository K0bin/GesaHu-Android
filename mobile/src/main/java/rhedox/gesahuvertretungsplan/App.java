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

import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;

/**
 * Created by Robin on 29.06.2015.
 */

public class App extends Application {
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        refWatcher = LeakCanary.install(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String darkThemeString = prefs.getString(PreferenceFragment.PREF_DARK_TYPE, "default");

        if("always".equals(darkThemeString))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else if("auto".equals(darkThemeString))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        else if("never".equals(darkThemeString))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        //Debug
        if(BuildConfig.DEBUG) {
            StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyDeath()
                    .build();
            StrictMode.setVmPolicy(policy);
        }
    }

    public static RefWatcher getRefWatcher(Context context) {
        App application = (App) context.getApplicationContext();
        return application.refWatcher;
    }
}

//https://code.google.com/p/android/issues/detail?id=77712
//https://code.google.com/p/android/issues/detail?id=183783
