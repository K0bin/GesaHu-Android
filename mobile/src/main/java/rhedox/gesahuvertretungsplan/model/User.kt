package rhedox.gesahuvertretungsplan.model

import android.content.Context
import rhedox.gesahuvertretungsplan.App

/**
 * Created by robin on 11.10.2016.
 */
class User(context: Context) {
    val username: String;
    val password: String;

    init {
        val prefs = context.getSharedPreferences(App.PREFERENCES_LOGIN, Context.MODE_PRIVATE);
        username = prefs.getString(App.PREF_USERNAME, "");
        password = prefs.getString(App.PREF_PASSWORD, "");
    }
}
