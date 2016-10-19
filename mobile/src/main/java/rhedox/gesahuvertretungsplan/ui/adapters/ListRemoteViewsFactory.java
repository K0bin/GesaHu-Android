package rhedox.gesahuvertretungsplan.ui.adapters;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.GesaHuApi;
import rhedox.gesahuvertretungsplan.model.QueryDate;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.broadcastReceiver.ListWidgetProvider;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;
import rhedox.gesahuvertretungsplan.service.ListFactoryService;

/**
 * Created by Robin on 22.07.2015.
 */
public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory, Callback<SubstitutesList> {
    private List<Substitute> substitutes;
    private SubstitutesList list;

    private Context context;
    private int appWidgetId;
    private LocalDate date;

    private boolean darkTheme;

    public ListRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        date = new DateTime(intent.getLongExtra(ListFactoryService.EXTRA_DATE, 0l)).toLocalDate();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        darkTheme = prefs.getBoolean(PreferenceFragment.PREF_WIDGET_DARK, false);
    }

    @Override
    public void onCreate() {
        GesaHuApi gesahui = GesaHuApi.Companion.create(context);
        Call<SubstitutesList> call = gesahui.substitutes(new QueryDate(date));
        call.enqueue(this);
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(substitutes == null || list == null)
            return 0;

        if(!list.hasSubstitutes())
            return 1;
        else
            return substitutes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (list == null)
            return null;

        if (position == 0 && !list.hasSubstitutes()) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_view_empty);
            if (darkTheme) {
                remoteViews.setTextColor(R.id.subject, 0xFFFFFFFF);
                remoteViews.setTextColor(R.id.hint, 0xFFFFFFFF);
            } else {
                remoteViews.setTextColor(R.id.subject, 0xFF000000);
                remoteViews.setTextColor(R.id.hint, 0xFF000000);
            }
            return remoteViews;
        }

        if (substitutes == null || substitutes.size() <= position)
            return null;

        Substitute substitute = substitutes.get(position);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_view_substitute);
        remoteViews.setTextViewText(R.id.lesson, substitute.getLessonText());
        remoteViews.setTextViewText(R.id.subject, substitute.getSubject());
        remoteViews.setTextViewText(R.id.teacher, substitute.getTeacher());
        remoteViews.setTextViewText(R.id.substituteTeacher, substitute.getSubstitute());
        remoteViews.setTextViewText(R.id.hint, substitute.getHint());
        remoteViews.setTextViewText(R.id.room, substitute.getRoom());

        remoteViews.setTextColor(R.id.lesson, 0xFFFFFFFF);

        if (darkTheme) {
            remoteViews.setTextColor(R.id.subject, 0xFFFFFFFF);
            remoteViews.setTextColor(R.id.teacher, 0xFFFFFFFF);
            remoteViews.setTextColor(R.id.substituteTeacher, 0xFFFFFFFF);
            remoteViews.setTextColor(R.id.hint, 0xFFFFFFFF);
            remoteViews.setTextColor(R.id.room, 0xFFFFFFFF);
        } else {
            remoteViews.setTextColor(R.id.subject, 0xFF000000);
            remoteViews.setTextColor(R.id.teacher, 0xFF000000);
            remoteViews.setTextColor(R.id.substituteTeacher, 0xFF000000);
            remoteViews.setTextColor(R.id.hint, 0xFF000000);
            remoteViews.setTextColor(R.id.room, 0xFF000000);
        }

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onResponse(Call<SubstitutesList> call, Response<SubstitutesList> response) {
        if(response == null || !response.isSuccessful())
            return;

        this.list = response.body();

        if(list != null)
            this.substitutes = SubstitutesList.filterRelevant(list.getSubstitutes(), true);
        else
            this.substitutes = null;

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        if (appWidgetId == -1) {
            int[] ids = widgetManager.getAppWidgetIds(new ComponentName(context, ListWidgetProvider.class));

            widgetManager.notifyAppWidgetViewDataChanged(ids, R.id.list);
        } else {
            widgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list);
        }
    }

    @Override
    public void onFailure(Call<SubstitutesList> call, Throwable t) {

    }
}
