package rhedox.gesahuvertretungsplan.broadcastReceiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.presenter.SubstitutesPresenter;
import rhedox.gesahuvertretungsplan.service.SubstitutesWidgetService;
import rhedox.gesahuvertretungsplan.ui.activity.SubstitutesActivity;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;
import rhedox.gesahuvertretungsplan.util.JodaTimeUtils;

/**
 * Created by Robin on 20.07.2015.
 */
public class SubstitutesWidgetProvider extends AppWidgetProvider {
    public static final int REQUEST_CODE = 1;
	public static final String ACTION_NOTIFY = "notify";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(ACTION_NOTIFY.equals(intent.getAction())) {
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			int[] ids = manager.getAppWidgetIds(new ComponentName(context, SubstitutesWidgetProvider.class));
			if(ids != null && ids.length > 0)
				manager.notifyAppWidgetViewDataChanged(ids, R.id.list);
		}

		super.onReceive(context, intent);
	}

	@Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	    super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int i= 0; i < appWidgetIds.length; i++) {
            Intent factoryServiceIntent = new Intent(context, SubstitutesWidgetService.class);
            factoryServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            factoryServiceIntent.setData(Uri.parse(factoryServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_list);

	        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	        boolean darkTheme = prefs.getBoolean(PreferenceFragment.PREF_WIDGET_DARK, false);
	        boolean amoled = prefs.getBoolean(PreferenceFragment.PREF_AMOLED, false);
            @ColorInt int color;
            if(!darkTheme)
                color = ContextCompat.getColor(context, R.color.widgetBackgroundLight);
            else if(amoled)
                color = 0xFF000000;
            else
                color = ContextCompat.getColor(context, R.color.widgetBackgroundDark);

            remoteViews.setInt(R.id.widget_frame, "setBackgroundColor", color);
            remoteViews.setRemoteAdapter(R.id.list, factoryServiceIntent);

            Intent onClickIntent = new Intent(context, SubstitutesActivity.class);
            onClickIntent.putExtra(SubstitutesActivity.Extra.date, JodaTimeUtils.getUnixTimeStamp(SchoolWeek.nextFromNow()));
            PendingIntent onClickPending = PendingIntent.getActivity(context, REQUEST_CODE, onClickIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_toolbar_text, onClickPending);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
    }

	public static Intent getRefreshBroadcastIntent(Context context) {
		return new Intent(ACTION_NOTIFY)
				.setComponent(new ComponentName(context, SubstitutesWidgetProvider.class));
	}
}
