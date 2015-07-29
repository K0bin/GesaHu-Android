package rhedox.gesahuvertretungsplan.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import org.joda.time.LocalDate;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.ui.activity.SingleDayActivity;
import rhedox.gesahuvertretungsplan.ui.fragment.SettingsFragment;
import rhedox.gesahuvertretungsplan.util.widget.ListFactoryService;

/**
 * Created by Robin on 20.07.2015.
 */
public class ListWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int i= 0; i < appWidgetIds.length; i++) {
            LocalDate date = SchoolWeek.next();
            //TODO: Remove following line
            date = new LocalDate(2015, 7, 24);

            Intent factoryServiceIntent = new Intent(context, ListFactoryService.class);
            factoryServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            factoryServiceIntent.putExtra(ListFactoryService.EXTRA_DATE, date.toDateTimeAtCurrentTime().getMillis());
            factoryServiceIntent.setData(Uri.parse(factoryServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean darkTheme = prefs.getBoolean(SettingsFragment.PREF_WIDGET_DARK, false);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list);
            remoteViews.setInt(R.id.widget_frame, "setBackgroundColor", context.getResources().getColor(darkTheme ? R.color.widgetBackgroundDark : R.color.widgetBackgroundLight));
            remoteViews.setRemoteAdapter(R.id.list, factoryServiceIntent);

            Intent onClickIntent = new Intent(context, SingleDayActivity.class);
            onClickIntent.putExtra(SingleDayActivity.EXTRA_DATE, date.toDateTimeAtCurrentTime().getMillis());
            PendingIntent onClickPending = PendingIntent.getActivity(context, 7, onClickIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_toolbar_text, onClickPending);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
