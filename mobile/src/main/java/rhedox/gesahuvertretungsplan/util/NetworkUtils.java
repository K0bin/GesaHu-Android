package rhedox.gesahuvertretungsplan.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Robin on 18.02.2016.
 */
public class NetworkUtils {

    private NetworkUtils() {}

    // Check network connection
    public static boolean isNetworkConnected(@NonNull Context context) {
        //using an activity context results in a memory leak
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
