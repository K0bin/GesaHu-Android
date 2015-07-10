package rhedox.gesahuvertretungsplan.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;

import java.util.List;

import rhedox.gesahuvertretungsplan.ui.MainActivity;
import rhedox.gesahuvertretungsplan.net.OnDownloadedListener;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.net.SubstitutesList;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.StudentInformation;

public class AlarmReceiver extends BroadcastReceiver implements OnDownloadedListener{
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm", "Alarm received!");
        SubstitutesList plan = new SubstitutesList();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String schoolClass = prefs.getString("pref_class", "a");
        String schoolYear = prefs.getString("pref_year", "5");

        plan.load(context, SchoolWeek.next(), new StudentInformation(schoolYear, schoolClass), this);

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
    public void onDownloaded(List<Substitute> substitutes, String annotation) {

        int count = 0;
        for (int i = 0; i < substitutes.size(); i++) {
            //NotificationManagerCompat notificationManager = (NotificationManagerCompat) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (substitutes.get(i).getIsImportant()) {
                String notificationText = "";
                if (!substitutes.get(i).getSubject().trim().equals("")) {
                    notificationText += substitutes.get(i).getSubject().trim();
                }
                if (!substitutes.get(i).getRoom().trim().equals("")) {
                    notificationText += "; Raum: " + substitutes.get(i).getRoom().trim();
                }
                if (!substitutes.get(i).getRegularTeacher().trim().equals("")) {
                    notificationText += System.getProperty("line.separator") + "Lehrer: " + substitutes.get(i).getRegularTeacher().trim() + "; ";
                }
                if (!substitutes.get(i).getReplacementTeacher().trim().equals("") && !substitutes.get(i).getReplacementTeacher().trim().equals(" ")) {
                    notificationText += "Vertretungslehrer: " + substitutes.get(i).getReplacementTeacher().trim();
                }
                if (!substitutes.get(i).getHint().trim().equals("")) {
                    notificationText += System.getProperty("line.separator") + "Hinweis: " + substitutes.get(i).getHint().trim();
                }


                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

                Intent launchIntent = new Intent(context.getApplicationContext(), MainActivity.class);
                PendingIntent launchPending = PendingIntent.getActivity(context, 1 + count * 2, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.bigText(notificationText);

                bigTextStyle.setBigContentTitle("Vertretung");
                bigTextStyle.setSummaryText("Du hast Vertretung in der " + substitutes.get(i).getLesson() + " Stunde");
                builder.setStyle(bigTextStyle);
                builder.setSmallIcon(R.drawable.icon_notification);
                builder.setContentTitle("Vertretung");
                builder.setContentText("Du hast Vertretung in der " + substitutes.get(i).getLesson() + " Stunde");
                builder.setContentInfo(substitutes.get(i).getLesson());
                builder.setContentIntent(launchPending);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    //share.putExtra(Intent.EXTRA_SUBJECT, "Vertretung");
                    share.putExtra(Intent.EXTRA_TEXT, "Wir haben " + notificationText);
                    share.setType("text/plain");
                    PendingIntent pending = PendingIntent.getActivity(context, 2 + count * 2, share, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.addAction(R.drawable.icon_share, "Teilen", pending);
                }
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder.setCategory(NotificationCompat.CATEGORY_EVENT);
                    builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                }

                notificationManager.notify(i, builder.build());
                count++;
            }
        }

    }

    @Override
    public void onDownloadFailed(int error) {

    }
}
