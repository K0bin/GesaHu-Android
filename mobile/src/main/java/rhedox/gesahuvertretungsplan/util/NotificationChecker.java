package rhedox.gesahuvertretungsplan.util;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import org.joda.time.LocalDate;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.ShortNameResolver;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.net.GesahuiApi;
import rhedox.gesahuvertretungsplan.net.SubstitutesListConverterFactory;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;

/**
 * Created by Robin on 07.09.2015.
 */
public class NotificationChecker implements Callback<SubstitutesList> {
    private Context context;
    private int color;
    private boolean isLoading = false;

    @NonNull private GesahuiApi gesahui;

    public static final int REQUEST_CODE_BASE = 64;
    private int lesson = -1;

    public NotificationChecker(Context context) {
        this.context = context.getApplicationContext(); //Prevent Activity leaking!

        //Load stundent information for matching lessons
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String schoolClass = prefs.getString("pref_class", "a");
        String schoolYear = prefs.getString("pref_year", "5");
        boolean specialMode = prefs.getBoolean(PreferenceFragment.PREF_SPECIAL_MODE, false);

        StudentInformation information = new StudentInformation(schoolYear, schoolClass);

        //Color is used for the notifications
        color = prefs.getInt(PreferenceFragment.PREF_COLOR, ContextCompat.getColor(context, R.color.colorDefaultAccent));

        //Init retrofit for pulling the data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gesahui.de")
                .addConverterFactory(new SubstitutesListConverterFactory(new ShortNameResolver(context, specialMode), information))
                //.client(client)
                .build();

        gesahui = retrofit.create(GesahuiApi.class);
    }

    public NotificationChecker(Context context, int lesson) {
        this(context);
        this.lesson = lesson;
    }

    public void load() {
        if(!isLoading) {

            LocalDate date = SchoolWeek.next();
            Call<SubstitutesList> call = gesahui.getSubstitutesList(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
            call.enqueue(this);

            isLoading = true;
        }
    }

    @Override
    public void onResponse(Call<SubstitutesList> call, Response<SubstitutesList> response) {
        isLoading = false;

        if(response == null || response.body() == null)
            return;

        List<Substitute> substitutes = response.body().getSubstitutes();

        if(substitutes == null)
            return;

        int count = 0;
        for (int i = 0; i < substitutes.size(); i++) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (substitutes.get(i).getIsImportant() && (lesson == -1 || lesson == substitutes.get(i).getStartingLesson())) {
                String notificationText = SubstituteShareHelper.makeShareText(null, substitutes.get(i), context);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

                Intent launchIntent = new Intent(context.getApplicationContext(), MainActivity.class);
                PendingIntent launchPending = PendingIntent.getActivity(context, REQUEST_CODE_BASE + count, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.bigText(notificationText);

                bigTextStyle.setBigContentTitle(context.getString(R.string.substitute));
                bigTextStyle.setSummaryText(context.getString(R.string.notification_text) + " " + substitutes.get(i).getLesson() + " " + context.getString(R.string.hour) + ".");
                builder.setStyle(bigTextStyle);
                builder.setSmallIcon(R.drawable.ic_notification);
                builder.setContentTitle(context.getString(R.string.substitute));
                builder.setContentText(context.getString(R.string.notification_text) + " " + substitutes.get(i).getLesson() + " " + context.getString(R.string.hour) + ".");
                builder.setContentInfo(substitutes.get(i).getLesson());
                builder.setContentIntent(launchPending);

                //Only relevant for JELLY_BEAN and higher
                PendingIntent pending = SubstituteShareHelper.makePendingShareIntent(LocalDate.now(), substitutes.get(i), context);
                NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_share, context.getString(R.string.share), pending);
                builder.addAction(action);


                //Only relevant for LOLLIPOP and higher
                builder.setCategory(NotificationCompat.CATEGORY_EVENT);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                builder.setColor(color);

                notificationManager.notify(i, builder.build());
                count++;
            }
        }

        //TeslaUnread
        try {
            ContentValues cv = new ContentValues();

            cv.put("tag", "com.yourpackagename/com.youractivityname");

            cv.put("count", SubstitutesList.countImportant(substitutes));

            context.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), cv);

        } catch (IllegalArgumentException ex) { /* TeslaUnread is not installed. */  }
    }

    @Override
    public void onFailure(Call<SubstitutesList> call, Throwable t) {
        isLoading = false;
    }
}
