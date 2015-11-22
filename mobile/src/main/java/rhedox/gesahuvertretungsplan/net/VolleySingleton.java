package rhedox.gesahuvertretungsplan.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by Robin on 12.07.2015.
 */
public class VolleySingleton {
    private static VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private Context context;

    private VolleySingleton(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue == null) {
            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(30, TimeUnit.SECONDS);
            client.setReadTimeout(30, TimeUnit.SECONDS);
            client.setWriteTimeout(30, TimeUnit.SECONDS);
            requestQueue = Volley.newRequestQueue(context, new OkHttpStack(client));
        }

        return requestQueue;
    }

    public static VolleySingleton getInstance(@NonNull Context context) {
        if(VolleySingleton.volleySingleton == null)
            VolleySingleton.volleySingleton = new VolleySingleton(context);

        return VolleySingleton.volleySingleton;
    }

    // Check network connection
    public static boolean isNetworkConnected(@NonNull Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
