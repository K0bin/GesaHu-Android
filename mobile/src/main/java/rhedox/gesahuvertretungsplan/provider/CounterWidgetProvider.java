package rhedox.gesahuvertretungsplan.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import org.joda.time.LocalDate;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.ui.fragment.SettingsFragment;
import rhedox.gesahuvertretungsplan.util.widget.CounterWidgetService;

/**
 * Created by Robin on 20.07.2015.
 */
public class CounterWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean darkTheme = prefs.getBoolean(SettingsFragment.PREF_WIDGET_DARK, false);

        LocalDate date = SchoolWeek.next();
        //TODO: Remove following line
        date = new LocalDate(2015, 7, 24);

        for(int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = makeAppWidget(context, darkTheme, date, -1);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

        Intent serviceIntent = new Intent(context, CounterWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        serviceIntent.putExtra(CounterWidgetService.EXTRA_DATE, date.toDateTimeAtCurrentTime().getMillis());
        context.startService(serviceIntent);

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static RemoteViews makeAppWidget(Context context, boolean darkTheme, LocalDate date, int substituteCount) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_counter);

        if(darkTheme) {
            remoteViews.setInt(R.id.counter, "setBackgroundResource", R.drawable.widget_card_dark);
            remoteViews.setTextColor(R.id.counter, 0xFFFFFFFF);
        }
        else {
            remoteViews.setInt(R.id.counter, "setBackgroundResource", R.drawable.widget_card);
            remoteViews.setTextColor(R.id.counter, 0xFF000000);
        }

        if(substituteCount != -1) {
            remoteViews.setViewVisibility(R.id.counter, View.VISIBLE);
            remoteViews.setTextViewText(R.id.counter, Integer.toString(substituteCount));
        } else
            remoteViews.setViewVisibility(R.id.counter, View.GONE);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_DATE, date.toDateTimeAtCurrentTime().getMillis());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 7, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_frame, pendingIntent);

        return remoteViews;
    }
}
