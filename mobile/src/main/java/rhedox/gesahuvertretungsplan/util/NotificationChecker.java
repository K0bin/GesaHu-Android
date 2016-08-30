package rhedox.gesahuvertretungsplan.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.joda.time.LocalDate;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.ShortNameResolver;
import rhedox.gesahuvertretungsplan.model.Student;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.model.GesahuiApi;
import rhedox.gesahuvertretungsplan.model.SubstitutesListConverterFactory;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;

/**
 * Created by Robin on 07.09.2015.
 */
public class NotificationChecker implements Callback<SubstitutesList> {
	private Context context;
	private int color;
	private boolean isLoading = false;
	private boolean useSummaryNotification;

	@NonNull private GesahuiApi gesahui;

	public static final int REQUEST_CODE_BASE = 64;

	public static final String GROUP_KEY = "gesahuvp";

	private int lesson = -1;

	public NotificationChecker(Context context) {
		this.context = context.getApplicationContext(); //Prevent Activity leaking!

		//Load student for matching lessons
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String schoolClass = prefs.getString("pref_class", "a");
		String schoolYear = prefs.getString("pref_year", "5");
		boolean specialMode = prefs.getBoolean(PreferenceFragment.PREF_SPECIAL_MODE, false);
		useSummaryNotification = prefs.getBoolean(PreferenceFragment.PREF_NOTIFICATION_SUMMARY, false);

		Student student = new Student(schoolYear, schoolClass);

		//Color is used for the notifications
		color = ContextCompat.getColor(context, R.color.colorDefaultAccent);

		//Init retrofit for pulling the data
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://gesahui.de")
				.addConverterFactory(new SubstitutesListConverterFactory(new ShortNameResolver(context, specialMode), student))
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

		Log.d("Notification", "ReceivedBroadcast");

		if(response == null || response.body() == null)
			return;

		List<Substitute> substitutes = SubstitutesList.filterImportant(response.body().getSubstitutes(), true);

		if(substitutes == null)
			return;

		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

		notificationManager.cancelAll();

		//Store titles for summary notification
		String[] titles = new String[99];

		int count = 0;
		for (int i = 0; i < substitutes.size(); i++) {
			if (lesson == -1 || lesson == substitutes.get(i).getStartingLesson()) {

				//Text to display
				String notificationText = SubstituteFormatter.makeNotificationText(context, substitutes.get(i));
				String title = SubstituteFormatter.makeSubstituteKindText(context, substitutes.get(i).getKind());
				String body = String.format(context.getString(R.string.notification_summary), title, substitutes.get(i).getLesson());
				titles[count] = body;

				if(lesson != -1 || !useSummaryNotification) {
					NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

					//Open app on click on notification
					Intent launchIntent = new Intent(context.getApplicationContext(), MainActivity.class);
					if (response.body().getDate() != null)
						launchIntent.putExtra(MainActivity.EXTRA_DATE, response.body().getDate().toDateTimeAtCurrentTime().getMillis());
					PendingIntent launchPending = PendingIntent.getActivity(context, REQUEST_CODE_BASE + count, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					builder.setContentIntent(launchPending);

					//Expanded style
					NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
					bigTextStyle.bigText(notificationText);
					bigTextStyle.setBigContentTitle(title);
					bigTextStyle.setSummaryText(body);
					builder.setStyle(bigTextStyle);

					//Normal notification
					builder.setSmallIcon(R.drawable.ic_notification);
					builder.setContentTitle(title);
					builder.setContentText(body);
					builder.setContentInfo(substitutes.get(i).getLesson());
					builder.setGroup(NotificationChecker.GROUP_KEY);

					//Only relevant for JELLY_BEAN and higher
					PendingIntent pending = SubstituteShareHelper.makePendingShareIntent(context, LocalDate.now(), substitutes.get(i));
					NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_share, context.getString(R.string.share), pending);
					builder.addAction(action);

					//Only relevant for LOLLIPOP and higher
					builder.setCategory(NotificationCompat.CATEGORY_EVENT);
					builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
					builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
					builder.setColor(color);

					notificationManager.notify(i, builder.build());
				}
				count++;
			}
		}

		//Notification group summary
		Notification summary = makeSummaryNotification(lesson, count, response.body().getDate(), titles);
		if(summary != null)
			notificationManager.notify(count+13, summary);

		//TeslaUnread
		try {
			ContentValues cv = new ContentValues();

			cv.put("tag", "rhedox.gesahuvertretungsplan/rhedox.gesahuvertretungsplan.ui.activity.MainActivity");

			cv.put("count", SubstitutesList.countImportant(substitutes));

			context.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), cv);

		} catch (IllegalArgumentException ex) { /* TeslaUnread is not installed. */  }
	}

	private Notification makeSummaryNotification(int lesson, int notificationCount, LocalDate date, String[] notificationLines) {

		if(notificationCount <= 0 || notificationLines == null || notificationLines.length == 0)
			return null;

		if((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) && (lesson != -1 || !useSummaryNotification))
			return null;

		//This summary notification, denoted by setGroupSummary(true), is the only notification that appears on Marshmallow and lower devices and should (you guessed it) summarize all of the individual notifications.
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

		//Open app on click on notification
		Intent launchIntent = new Intent(context.getApplicationContext(), MainActivity.class);
		if(date != null)
			launchIntent.putExtra(MainActivity.EXTRA_DATE, date.toDateTimeAtCurrentTime().getMillis());
		PendingIntent launchPending = PendingIntent.getActivity(context, REQUEST_CODE_BASE + notificationCount + 13, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(launchPending);

		//Normal notification
		builder.setContentText(String.format("%s %s", Integer.toString(notificationCount), context.getString(R.string.lessons)));
		builder.setSmallIcon(R.drawable.ic_notification);
		String title;
		String summary;
		if (lesson == -1) {
			builder.setContentInfo("1-10");
			title = context.getString(R.string.notification_summary_day);
			summary = context.getString(R.string.notification_summary_day_hint);
		} else {
			builder.setContentInfo(Integer.toString(lesson));
			title = context.getString(R.string.notification_summary_lesson);
			summary = context.getString(R.string.notification_summary_lesson_hint);
		}
		builder.setContentTitle(title);

		//Inbox style expanded notification
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		for(int i = 0; i < Math.min(5, notificationCount); i++)
			inboxStyle.addLine(notificationLines[i]);

		if(notificationCount > 5)
			inboxStyle.setSummaryText(String.format(context.getString(R.string.inbox_style),Integer.toString(notificationCount-5)));
		else
			inboxStyle.setSummaryText(summary);

		inboxStyle.setBigContentTitle(title);
		builder.setStyle(inboxStyle);


		//Only relevant for LOLLIPOP and higher
		builder.setCategory(NotificationCompat.CATEGORY_EVENT);
		builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
		builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
		builder.setColor(color);

		//N + Wear summary
		builder.setGroupSummary(true);
		builder.setGroup(NotificationChecker.GROUP_KEY);

		return builder.build();
	}

	@Override
	public void onFailure(Call<SubstitutesList> call, Throwable t) {
		isLoading = false;
	}
}