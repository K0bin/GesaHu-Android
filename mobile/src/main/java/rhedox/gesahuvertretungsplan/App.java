package rhedox.gesahuvertretungsplan;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Robin on 29.06.2015.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
