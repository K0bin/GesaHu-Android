package rhedox.gesahuvertretungsplan.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.RemoteViews;

import org.joda.time.LocalDate;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;
import rhedox.gesahuvertretungsplan.service.CounterWidgetService;

/**
 * Created by Robin on 20.07.2015.
 */
public class CounterWidgetProvider extends AppWidgetProvider {
    public static final int REQUEST_CODE = 3;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean darkTheme = prefs.getBoolean(PreferenceFragment.PREF_WIDGET_DARK, false);
        boolean amoled = prefs.getBoolean(PreferenceFragment.PREF_AMOLED, false);

        LocalDate date = SchoolWeek.next();

        for(int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = makeAppWidget(context, darkTheme, amoled, date, -1);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

        Intent serviceIntent = new Intent(context, CounterWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        serviceIntent.putExtra(CounterWidgetService.EXTRA_DATE, date.toDateTimeAtCurrentTime().getMillis());
        context.startService(serviceIntent);

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static RemoteViews makeAppWidget(Context context, boolean darkTheme, boolean amoled, LocalDate date, int substituteCount) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_counter);

        if(darkTheme) {
            @DrawableRes int backgroundResource = amoled ? R.drawable.widget_card_black : R.drawable.widget_card_dark;
            remoteViews.setInt(R.id.counter, "setBackgroundResource", backgroundResource);
            remoteViews.setTextColor(R.id.counter, 0xFFFFFFFF);
        }
        else {
            remoteViews.setInt(R.id.counter, "setBackgroundResource", R.drawable.widget_card);
            remoteViews.setTextColor(R.id.counter, 0xFF000000);
        }

        if(substituteCount != -1 && substituteCount != 0) {
            remoteViews.setViewVisibility(R.id.counter, View.VISIBLE);
            remoteViews.setTextViewText(R.id.counter, Integer.toString(substituteCount));
        } else
            remoteViews.setViewVisibility(R.id.counter, View.GONE);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_DATE, date.toDateTimeAtCurrentTime().getMillis());
        intent.putExtra(MainActivity.EXTRA_WIDGET, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_frame, pendingIntent);

        return remoteViews;
    }
}
