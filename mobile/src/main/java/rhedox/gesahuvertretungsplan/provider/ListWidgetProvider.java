package rhedox.gesahuvertretungsplan.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import org.joda.time.LocalDate;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;
import rhedox.gesahuvertretungsplan.util.appwidget.ListFactoryService;

/**
 * Created by Robin on 20.07.2015.
 */
public class ListWidgetProvider extends AppWidgetProvider {
    public static final int REQUEST_CODE = 1;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int i= 0; i < appWidgetIds.length; i++) {
            LocalDate date = SchoolWeek.next();

            Intent factoryServiceIntent = new Intent(context, ListFactoryService.class);
            factoryServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            factoryServiceIntent.putExtra(ListFactoryService.EXTRA_DATE, date.toDateTimeAtCurrentTime().getMillis());
            factoryServiceIntent.setData(Uri.parse(factoryServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean darkTheme = prefs.getBoolean(PreferenceFragment.PREF_WIDGET_DARK, false);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list);
            remoteViews.setInt(R.id.widget_frame, "setBackgroundColor", ContextCompat.getColor(context, darkTheme ? R.color.windowBackgroundDark : R.color.windowBackgroundLight));
            remoteViews.setRemoteAdapter(R.id.list, factoryServiceIntent);

            Intent onClickIntent = new Intent(context, MainActivity.class);
            onClickIntent.putExtra(MainActivity.EXTRA_DATE, date.toDateTimeAtCurrentTime().getMillis());
            PendingIntent onClickPending = PendingIntent.getActivity(context, REQUEST_CODE, onClickIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_toolbar_text, onClickPending);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
