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
import retrofit2.Retrofit;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.ShortNameResolver;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.net.GesahuiApi;
import rhedox.gesahuvertretungsplan.net.SubstitutesListConverterFactory;
import rhedox.gesahuvertretungsplan.provider.ListWidgetProvider;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;
import rhedox.gesahuvertretungsplan.util.appwidget.ListFactoryService;

/**
 * Created by Robin on 22.07.2015.
 */
public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory, Callback<SubstitutesList> {
    private List<Substitute> substitutes;

    private Context context;
    private int appWidgetId;
    private LocalDate date;
    private StudentInformation studentInformation;

    private boolean darkTheme;
    private boolean specialMode;

    public ListRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        date = new DateTime(intent.getLongExtra(ListFactoryService.EXTRA_DATE, 0l)).toLocalDate();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        darkTheme = prefs.getBoolean(PreferenceFragment.PREF_WIDGET_DARK, false);
        specialMode = prefs.getBoolean(PreferenceFragment.PREF_SPECIAL_MODE, false);
        studentInformation = new StudentInformation(prefs.getString(PreferenceFragment.PREF_YEAR, "5"), prefs.getString(PreferenceFragment.PREF_CLASS, "a"));
    }

    @Override
    public void onCreate() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gesahui.de")
                .addConverterFactory(new SubstitutesListConverterFactory(new ShortNameResolver(context, specialMode), studentInformation))
                .build();

        GesahuiApi gesahui = retrofit.create(GesahuiApi.class);
        Call<SubstitutesList> call = gesahui.getSubstitutesList(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
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
        return substitutes != null ? substitutes.size() : 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if(substitutes == null || substitutes.size() <= position)
            return null;
        else {
            Substitute substitute = substitutes.get(position);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_view_substitute);
            remoteViews.setTextViewText(R.id.lesson, substitute.getLesson());
            remoteViews.setTextViewText(R.id.subject, substitute.getSubject());
            remoteViews.setTextViewText(R.id.teacher, substitute.getTeacher());
            remoteViews.setTextViewText(R.id.substituteTeacher, substitute.getSubstituteTeacher());
            remoteViews.setTextViewText(R.id.hint, substitute.getHint());
            remoteViews.setTextViewText(R.id.room, substitute.getRoom());

            remoteViews.setTextColor(R.id.lesson, 0xFFFFFFFF);

            if(darkTheme) {
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

        if(response.body() != null)
            this.substitutes = SubstitutesList.filterImportant(response.body().getSubstitutes());
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
