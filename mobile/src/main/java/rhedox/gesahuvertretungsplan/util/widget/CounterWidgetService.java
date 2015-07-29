package rhedox.gesahuvertretungsplan.util.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import com.android.volley.Response;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Date;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.net.SubstituteRequest;
import rhedox.gesahuvertretungsplan.net.VolleySingleton;
import rhedox.gesahuvertretungsplan.provider.CounterWidgetProvider;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.ui.activity.SingleDayActivity;
import rhedox.gesahuvertretungsplan.ui.fragment.SettingsFragment;

/**
 * Created by Robin on 23.07.2015.
 */
public class CounterWidgetService extends Service implements Response.Listener<SubstitutesList>{
    private boolean darkTheme;

    private int[] allWidgetIds;
    private LocalDate date;

    public static final String EXTRA_DATE = "extra_date";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        darkTheme = prefs.getBoolean(SettingsFragment.PREF_WIDGET_DARK, false);
        StudentInformation studentInformation = new StudentInformation(prefs.getString(SettingsFragment.PREF_YEAR, "5"), prefs.getString(SettingsFragment.PREF_CLASS, "a"));

        date = new DateTime(intent.getLongExtra(EXTRA_DATE, 0l)).toLocalDate();

        VolleySingleton.getInstance(getApplicationContext()).getRequestQueue().add(new SubstituteRequest(getApplicationContext(), date, studentInformation, this, null));

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onResponse(SubstitutesList response) {
        if(response == null)
            return;

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

        for(int appWidgetId : allWidgetIds) {
            RemoteViews remoteViews = CounterWidgetProvider.makeAppWidget(getApplicationContext(), darkTheme, date, SubstitutesList.countImportant(response.getSubstitutes()));

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}

