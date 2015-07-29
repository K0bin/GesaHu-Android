package rhedox.gesahuvertretungsplan.util;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.List;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.net.SubstituteRequest;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.net.VolleySingleton;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;

/**
 * Created by Robin on 23.07.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NotificationJob extends JobService implements Response.Listener<SubstitutesList>, Response.ErrorListener{
    private SubstituteRequest request;
    private JobParameters parameters;

    @Override
    public boolean onStartJob(JobParameters params) {
        this.parameters = params;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String schoolClass = prefs.getString("pref_class", "a");
        String schoolYear = prefs.getString("pref_year", "5");

        StudentInformation information = new StudentInformation(schoolYear, schoolClass);

        request = new SubstituteRequest(getApplicationContext(), SchoolWeek.next(), information, this, null);
        VolleySingleton.getInstance(getApplicationContext()).getRequestQueue().add(request);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if(request!=null)
            request.cancel();

        return true;
    }
    
    @Override
    public void onResponse(SubstitutesList response) {
        if(response == null || response.getSubstitutes() == null)
            return;

        List<Substitute> substitutes = response.getSubstitutes();

        int count = 0;
        for (int i = 0; i < substitutes.size(); i++) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            if (substitutes.get(i).getIsImportant()) {
                String notificationText = "";
                if (!TextUtils.isEmpty(substitutes.get(i).getSubject())) {
                    notificationText += substitutes.get(i).getSubject().trim();
                }
                if (!TextUtils.isEmpty(substitutes.get(i).getRoom())) {
                    notificationText += "; "+getApplicationContext().getString(R.string.room)+": " + substitutes.get(i).getRoom().trim();
                }
                if (!TextUtils.isEmpty(substitutes.get(i).getRegularTeacher())) {
                    notificationText += System.getProperty("line.separator") + getApplicationContext().getString(R.string.teacher) + ": " + substitutes.get(i).getRegularTeacher().trim() + "; ";
                }
                if (!TextUtils.isEmpty(substitutes.get(i).getSubstituteTeacher()) && !TextUtils.isEmpty(substitutes.get(i).getSubstituteTeacher())) {
                    notificationText += getApplicationContext().getString(R.string.substitute_teacher)+": " + substitutes.get(i).getSubstituteTeacher().trim();
                }
                if (!TextUtils.isEmpty(substitutes.get(i).getHint())) {
                    notificationText += System.getProperty("line.separator") + getApplicationContext().getString(R.string.hint)+": " + substitutes.get(i).getHint().trim();
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                Intent launchIntent = new Intent(getApplicationContext().getApplicationContext(), MainActivity.class);
                PendingIntent launchPending = PendingIntent.getActivity(getApplicationContext(), 1 + count * 2, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.bigText(notificationText);

                bigTextStyle.setBigContentTitle(getApplicationContext().getString(R.string.substitute));
                bigTextStyle.setSummaryText(getApplicationContext().getString(R.string.notification_text) + " " + substitutes.get(i).getLesson() + " " + getApplicationContext().getString(R.string.hour)+".");
                builder.setStyle(bigTextStyle);
                builder.setSmallIcon(R.drawable.icon_notification);
                builder.setContentTitle(getApplicationContext().getString(R.string.substitute));
                builder.setContentText(getApplicationContext().getString(R.string.notification_text) + " " + substitutes.get(i).getLesson() + " " + getApplicationContext().getString(R.string.hour)+".");
                builder.setContentInfo(substitutes.get(i).getLesson());
                builder.setContentIntent(launchPending);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_TEXT, getApplicationContext().getString(R.string.share_text) + notificationText);
                    share.setType("text/plain");
                    PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 2 + count * 2, share, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.addAction(R.drawable.icon_share, getApplicationContext().getString(R.string.share), pending);
                }
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder.setCategory(NotificationCompat.CATEGORY_EVENT);
                    builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                }

                notificationManager.notify(i, builder.build());
                count++;
            }
        }

        jobFinished(parameters, false);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        jobFinished(parameters, true);
    }
}
