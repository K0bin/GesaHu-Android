package rhedox.gesahuvertretungsplan.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.SortedList;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.joda.time.LocalDate;

import java.util.Date;
import java.util.List;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.net.SubstituteJSoupRequest;
import rhedox.gesahuvertretungsplan.net.SubstituteRequest;
import rhedox.gesahuvertretungsplan.net.VolleySingleton;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.ui.fragment.SettingsFragment;

/**
 * Created by Robin on 07.09.2015.
 */
public class NotificationChecker implements Response.Listener<SubstitutesList>, Response.ErrorListener {
    private Context context;
    private int color;
    private boolean isLoading = false;
    private StudentInformation information;

    public NotificationChecker(Context context) {
        this.context = context.getApplicationContext(); //Prevent Activity leaking!

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String schoolClass = prefs.getString("pref_class", "a");
        String schoolYear = prefs.getString("pref_year", "5");

        information = new StudentInformation(schoolYear, schoolClass);

        color = prefs.getInt(SettingsFragment.PREF_COLOR, ContextCompat.getColor(context, R.color.colorDefaultAccent));
    }

    public void load() {
        if(!isLoading) {
            SubstituteJSoupRequest request = new SubstituteJSoupRequest(context, SchoolWeek.next(), information, this, null);
            request.setRetryPolicy(new DefaultRetryPolicy(30000,5,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(context).getRequestQueue().add(request);
            isLoading = true;
        }
    }

    @Override
    public void onResponse(SubstitutesList response) {
        isLoading = false;

        if(response == null || response.getSubstitutes() == null)
            return;

        List<Substitute> substitutes = response.getSubstitutes();

        int count = 0;
        for (int i = 0; i < substitutes.size(); i++) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (substitutes.get(i).getIsImportant()) {
                String notificationText = SubstituteShareHelper.makeShareText(null, substitutes.get(i), context);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

                Intent launchIntent = new Intent(context.getApplicationContext(), MainActivity.class);
                PendingIntent launchPending = PendingIntent.getActivity(context, 1 + count * 2, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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

        } catch (IllegalArgumentException ex) { /* TeslaUnread is not installed. */ }
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        isLoading = false;
    }
}
