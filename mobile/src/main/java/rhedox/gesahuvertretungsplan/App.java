package rhedox.gesahuvertretungsplan;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTimeZone;

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
    }

    public static RefWatcher getRefWatcher(Context context) {
        App application = (App) context.getApplicationContext();
        return application.refWatcher;
    }
}

//https://code.google.com/p/android/issues/detail?id=184887
//https://code.google.com/p/android/issues/detail?id=77712
//https://code.google.com/p/android/issues/detail?id=183783