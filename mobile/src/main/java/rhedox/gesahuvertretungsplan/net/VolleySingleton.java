package rhedox.gesahuvertretungsplan.net;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.OkHttpClient;

/**
 * Created by Robin on 12.07.2015.
 */
public class VolleySingleton {
    private static VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private Context context;

    private VolleySingleton(Context context) {
        this.context = context.getApplicationContext();
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(context, new OkHttpStack(new OkHttpClient()));

        return requestQueue;
    }

    public static VolleySingleton getInstance(Context context) {
        if(VolleySingleton.volleySingleton == null)
            VolleySingleton.volleySingleton = new VolleySingleton(context);

        return VolleySingleton.volleySingleton;
    }
}
