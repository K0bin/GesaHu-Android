package rhedox.gesahuvertretungsplan.service

import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.WakefulBroadcastReceiver
import org.jetbrains.anko.notificationManager
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.SubstituteFormatter
import rhedox.gesahuvertretungsplan.model.database.SubstitutesRepository
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity
import rhedox.gesahuvertretungsplan.util.SubstituteShareUtils
import rhedox.gesahuvertretungsplan.util.countRelevant
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 26.10.2016.
 */
class SubstitutesNotifier(private val context: Context) {
    companion object {
        const val extraLesson = "lesson";
        const val requestCodeBase = 64
        const val groupKey = "gesahuvpsubstitutes"
        const val substitutesChannel = "substitutes"
    }

    //Color is used for the notifications
    private val color: Int = ContextCompat.getColor(context, R.color.colorDefaultAccent);
    private var lesson: Int = -1;

    private val formatter: SubstituteFormatter = SubstituteFormatter(context);

    fun load(lesson: Int? = null) {
        var _lesson = lesson
        if (lesson != -1 && lesson != -1 && (DateTime.now().dayOfWeek == DateTimeConstants.SATURDAY || DateTime.now().dayOfWeek == DateTimeConstants.SUNDAY)) {
            if (lesson == 1)
                _lesson = -1
            else
                return
        }
        val date: LocalDate = SchoolWeek.nextFromNow()
        val substitutes = SubstitutesRepository.loadSubstitutesForDaySync(context, date, true)
        onSubstitutesLoaded(date, substitutes)
    }

    fun onSubstitutesLoaded(date: LocalDate, substitutes: List<Substitute>) {
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        val notificationManager = context.notificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(substitutesChannel) == null) {
                val channel = NotificationChannel(substitutesChannel, context.getString(R.string.notification_channel_substitutes), NotificationManager.IMPORTANCE_DEFAULT)
                channel.description = context.getString(R.string.notification_channel_substitutes_description)
                notificationManager.createNotificationChannel(channel)
            }
        }

        notificationManagerCompat.cancelAll()

        //Store titles for summary notification
        val titles = mutableListOf<String>()

        for (i in substitutes.indices) {
            if ((lesson == -1 || lesson == substitutes[i].lessonBegin) && substitutes[i].isRelevant) {

                //Text to display
                val notificationText = formatter.makeNotificationText(substitutes[i])
                val title = formatter.makeSubstituteKindText(substitutes[i].kind)
                val body = String.format(context.getString(R.string.notification_summary), title, substitutes[i].lessonText)
                titles.add(body)

                val builder = NotificationCompat.Builder(context, substitutesChannel)
                //Open app on click on notification
                val launchIntent = Intent(context, MainActivity::class.java)
                launchIntent.putExtra(MainActivity.Extra.date, date.unixTimeStamp)
                val launchPending = PendingIntent.getActivity(context, requestCodeBase + titles.size, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                builder.setContentIntent(launchPending)

                //Expanded style
                val bigTextStyle = NotificationCompat.BigTextStyle()
                bigTextStyle.bigText(notificationText)
                bigTextStyle.setBigContentTitle(title)
                bigTextStyle.setSummaryText(body)
                builder.setStyle(bigTextStyle)

                //Normal notification
                builder.setSmallIcon(R.drawable.ic_notification)
                builder.setContentTitle(title)
                builder.setContentText(body)
                builder.setContentInfo(substitutes[i].lessonText)
                builder.setGroup(groupKey)
                builder.setChannelId(substitutesChannel)

                //Only relevant for JELLY_BEAN and higher
                val pending = SubstituteShareUtils.makePendingShareIntent(context, LocalDate.now(), substitutes[i])
                val action = NotificationCompat.Action(R.drawable.ic_share, context.getString(R.string.share), pending)
                builder.addAction(action)

                //Only relevant for LOLLIPOP and higher
                builder.setCategory(NotificationCompat.CATEGORY_EVENT)
                builder.priority = NotificationCompat.PRIORITY_DEFAULT
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                builder.color = color

                notificationManagerCompat.notify(i, builder.build())
            }
        }

        //Notification group summary
        val summary = makeSummaryNotification(lesson, date, titles)
        if (summary != null)
            notificationManagerCompat.notify(titles.size + 13, summary)

        //TeslaUnread
        try {
            val cv = ContentValues()

            cv.put("tag", "rhedox.gesahuvertretungsplan/rhedox.gesahuvertretungsplan.ui.activity.MainActivity")

            cv.put("count", substitutes.countRelevant())

            context.contentResolver.insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), cv)

        } catch (ex: IllegalArgumentException) { /* TeslaUnread is not installed. */ }
    }

    private fun makeSummaryNotification(lesson: Int, date: LocalDate?, notificationLines: List<String>): Notification? {

        if (notificationLines.size <= 1)
            return null

        //This summary notification, denoted by setGroupSummary(true), is the only notification that appears on Marshmallow and lower devices and should (you guessed it) summarize all of the individual notifications.
        val builder = NotificationCompat.Builder(context, substitutesChannel)

        //Open app on click on notification
        val launchIntent = Intent(context, MainActivity::class.java)
        if (date != null) {
            launchIntent.putExtra(MainActivity.Extra.date, date.unixTimeStamp)
        }
        val launchPending = PendingIntent.getActivity(context, requestCodeBase + notificationLines.size + 13, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(launchPending)

        //Normal notification
        builder.setContentText(String.format("%s %s", Integer.toString(notificationLines.size), context.getString(R.string.lessons)))
        builder.setSmallIcon(R.drawable.ic_notification)
        val title: String
        val summary: String
        if (lesson == -1) {
            builder.setContentInfo("1-10")
            title = context.getString(R.string.notification_summary_day)
            summary = context.getString(R.string.notification_summary_day_hint)
        } else {
            builder.setContentInfo(Integer.toString(lesson))
            title = context.getString(R.string.notification_summary_lesson)
            summary = context.getString(R.string.notification_summary_lesson_hint)
        }
        builder.setContentTitle(title)

        //Inbox style expanded notification
        val inboxStyle = NotificationCompat.InboxStyle()
        for (i in 0..Math.min(5, notificationLines.size) - 1)
            inboxStyle.addLine(notificationLines[i])

        if (notificationLines.size > 5)
            inboxStyle.setSummaryText(String.format(context.getString(R.string.inbox_style), Integer.toString(notificationLines.size - 5)))
        else
            inboxStyle.setSummaryText(summary)

        inboxStyle.setBigContentTitle(title)
        builder.setStyle(inboxStyle)


        //Only relevant for LOLLIPOP and higher
        builder.setCategory(NotificationCompat.CATEGORY_EVENT)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        builder.color = color

        //N + Wear summary
        builder.setGroupSummary(true)
        builder.setGroup(groupKey)

        //O
        builder.setChannelId(substitutesChannel)

        return builder.build()
    }
}