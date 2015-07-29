package rhedox.gesahuvertretungsplan.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
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
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;

import java.util.List;

import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.net.SubstituteRequest;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.net.VolleySingleton;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;

public class AlarmReceiver extends BroadcastReceiver implements Response.Listener<SubstitutesList>, Response.ErrorListener {
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm", "Alarm received!");
        //SubstitutesList plan = new SubstitutesList();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String schoolClass = prefs.getString("pref_class", "a");
        String schoolYear = prefs.getString("pref_year", "5");

        StudentInformation information = new StudentInformation(schoolYear, schoolClass);

        VolleySingleton.getInstance(context).getRequestQueue().add(new SubstituteRequest(context, SchoolWeek.next(), information, this, null));

        this.context = context;
    }

    public static void create(Context context, int hour, int minute) {
        DateTime time = DateTime.now();

        if(hour < time.getHourOfDay() || (hour == time.getHourOfDay() && minute < time.getMinuteOfHour()))
            time = time.withFieldAdded(DurationFieldType.days(), 1);

        DateTime alarm = new DateTime(time.getYear(), time.getMonthOfYear(), time.getDayOfMonth(), hour, minute, 0, 0);
        long millis = alarm.getMillis();

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(manager != null) {
            manager.setInexactRepeating(AlarmManager.RTC, millis, AlarmManager.INTERVAL_DAY, pending);
        }
    }

    public static void cancel(Context context) {
        PendingIntent intent = PendingIntent.getBroadcast(context, 0 , new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(manager!=null) {
            manager.cancel(intent);
        }
    }

    @Override
    public void onResponse(SubstitutesList response) {
        if(response == null || response.getSubstitutes() == null)
            return;

        List<Substitute> substitutes = response.getSubstitutes();

        int count = 0;
        for (int i = 0; i < substitutes.size(); i++) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (substitutes.get(i).getIsImportant()) {
                String notificationText = "";

                if (!TextUtils.isEmpty(substitutes.get(i).getSubject())) {
                    notificationText += substitutes.get(i).getSubject().trim();
                }
                if (!TextUtils.isEmpty(substitutes.get(i).getRoom())) {
                    notificationText += "; "+context.getString(R.string.room)+": " + substitutes.get(i).getRoom().trim();
                }
                if (!TextUtils.isEmpty(substitutes.get(i).getRegularTeacher())) {
                    notificationText += System.getProperty("line.separator") + context.getString(R.string.teacher) + ": " + substitutes.get(i).getRegularTeacher().trim() + "; ";
                }
                if (!TextUtils.isEmpty(substitutes.get(i).getSubstituteTeacher()) && !TextUtils.isEmpty(substitutes.get(i).getSubstituteTeacher())) {
                    notificationText += context.getString(R.string.substitute_teacher)+": " + substitutes.get(i).getSubstituteTeacher().trim();
                }
                if (!TextUtils.isEmpty(substitutes.get(i).getHint())) {
                    notificationText += System.getProperty("line.separator") + context.getString(R.string.hint)+": " + substitutes.get(i).getHint().trim();
                }


                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

                Intent launchIntent = new Intent(context.getApplicationContext(), MainActivity.class);
                PendingIntent launchPending = PendingIntent.getActivity(context, 1 + count * 2, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.bigText(notificationText);

                bigTextStyle.setBigContentTitle(context.getString(R.string.substitute));
                bigTextStyle.setSummaryText(context.getString(R.string.notification_text) + " " + substitutes.get(i).getLesson() + " " + context.getString(R.string.hour) + ".");
                builder.setStyle(bigTextStyle);
                builder.setSmallIcon(R.drawable.icon_notification);
                builder.setContentTitle(context.getString(R.string.substitute));
                builder.setContentText(context.getString(R.string.notification_text) + " " + substitutes.get(i).getLesson() + " " + context.getString(R.string.hour) + ".");
                builder.setContentInfo(substitutes.get(i).getLesson());
                builder.setContentIntent(launchPending);

                //Only relevant for JELLY_BEAN and higher
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text) + notificationText);
                share.setType("text/plain");
                PendingIntent pending = PendingIntent.getActivity(context, count, share, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.addAction(R.drawable.icon_share, context.getString(R.string.share), pending);


                //Only relevant for LOLLIPOP and higher
                builder.setCategory(NotificationCompat.CATEGORY_EVENT);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

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
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo jobInfo  = new JobInfo.Builder(1, new ComponentName(context.getPackageName(), NotificationJob.class.getName()))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build();

            scheduler.schedule(jobInfo);
        }
    }
}
