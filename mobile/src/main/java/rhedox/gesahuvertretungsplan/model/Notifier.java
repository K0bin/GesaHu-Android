package rhedox.gesahuvertretungsplan.model;

import android.app.Notification;
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
import android.util.Log;

import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.util.SubstituteShareUtils;

/**
 * Created by Robin on 07.09.2015.
 */
public class Notifier {
	private Context context;
	private int color;
	private User user;

	@NonNull private GesaHuiApi gesahui;

	public static final int REQUEST_CODE_BASE = 64;

	public static final String GROUP_KEY = "gesahuvp";

	private int lesson = -1;

	public Notifier(Context context) {
		this.context = context.getApplicationContext(); //Prevent Activity leaking!
		user = new User(context);

		//Color is used for the notifications
		color = ContextCompat.getColor(context, R.color.colorDefaultAccent);

		gesahui = GesaHuiApi.Companion.create(context);
	}

	public Notifier(Context context, int lesson) {
		this(context);
		this.lesson = lesson;
	}

	public void load() {
		LocalDate date = SchoolWeek.next();
		Call<SubstitutesList> call = gesahui.substitutes(new QueryDate(date), user.getUsername());
		try {
			Response<SubstitutesList> response = call.execute();

			if(response == null || !response.isSuccessful() || response.body() == null)
				return;

			List<Substitute> substitutes = SubstitutesList.filterRelevant(response.body().getSubstitutes(), true);
			NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

			notificationManager.cancelAll();

			//Store titles for summary notification
			String[] titles = new String[99];

			int count = 0;
			for (int i = 0; i < substitutes.size(); i++) {
				if (lesson == -1 || lesson == substitutes.get(i).getLessonBegin()) {

					//Text to display
					String notificationText = SubstituteFormatter.makeNotificationText(context, substitutes.get(i));
					String title = SubstituteFormatter.makeSubstituteKindText(context, substitutes.get(i).getKind());
					String body = String.format(context.getString(R.string.notification_summary), title, substitutes.get(i).getLessonText());
					titles[count] = body;

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
					builder.setContentInfo(substitutes.get(i).getLessonText());
					builder.setGroup(Notifier.GROUP_KEY);

					//Only relevant for JELLY_BEAN and higher
					PendingIntent pending = SubstituteShareUtils.makePendingShareIntent(context, LocalDate.now(), substitutes.get(i));
					NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_share, context.getString(R.string.share), pending);
					builder.addAction(action);

					//Only relevant for LOLLIPOP and higher
					builder.setCategory(NotificationCompat.CATEGORY_EVENT);
					builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
					builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
					builder.setColor(color);

					notificationManager.notify(i, builder.build());
					count++;
				}
			}

			//Notification group summary
			Notification summary = makeSummaryNotification(lesson, count, response.body().getDate(), titles);
			if (summary != null)
				notificationManager.notify(count + 13, summary);

			//TeslaUnread
			try {
				ContentValues cv = new ContentValues();

				cv.put("tag", "rhedox.gesahuvertretungsplan/rhedox.gesahuvertretungsplan.ui.activity.MainActivity");

				cv.put("count", SubstitutesList.countRelevant(substitutes));

				context.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), cv);

			} catch (IllegalArgumentException ex) { /* TeslaUnread is not installed. */ }
		}
		catch (IOException e)  { }
		catch (IllegalArgumentException ie) { /* TeslaUnread is not installed. */ }
	}

	private Notification makeSummaryNotification(int lesson, int notificationCount, LocalDate date, String[] notificationLines) {

		if(notificationCount <= 1 || notificationLines == null || notificationLines.length == 0)
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
		builder.setGroup(Notifier.GROUP_KEY);

		return builder.build();
	}
}