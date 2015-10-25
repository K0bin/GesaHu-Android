package rhedox.gesahuvertretungsplan;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by Robin on 29.06.2015.
 */

@ReportsCrashes(
        formUri = "https://collector.tracepot.com/84f365ea"
)
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        ACRA.init(this);
    }
}

//https://code.google.com/p/android/issues/detail?id=184887
//https://code.google.com/p/android/issues/detail?id=77712
//https://code.google.com/p/android/issues/detail?id=183783