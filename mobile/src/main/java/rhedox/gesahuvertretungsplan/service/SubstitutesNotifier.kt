package rhedox.gesahuvertretungsplan.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.SubstituteFormatter
import rhedox.gesahuvertretungsplan.model.database.SubstitutesDatabaseRepository
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity
import rhedox.gesahuvertretungsplan.util.SubstituteShareUtils
import rhedox.gesahuvertretungsplan.util.countRelevant
import rhedox.gesahuvertretungsplan.util.notificationManager
import rhedox.gesahuvertretungsplan.util.unixTimeStamp
import javax.inject.Inject

/**
 * Created by robin on 26.10.2016.
 */
class SubstitutesNotifier(private val context: Context) {
    companion object {
        const val extraLesson = "lesson"
        const val requestCodeBase = 64
        const val groupKey = "gesahuvpsubstitutes"
        const val substitutesChannel = "substitutes"
    }

    //Color is used for the notifications
    private val color: Int = ContextCompat.getColor(context, R.color.colorDefaultAccent)
    private var lesson: Int = -1

    @Inject internal lateinit var formatter: SubstituteFormatter
    @Inject internal lateinit var repository: SubstitutesDatabaseRepository

    init {
        (context.applicationContext as App)
                .appComponent
                .plusSubstitutes()
                .inject(this)
    }

    fun load(lesson: Int? = null) {
        var selectedLesson = lesson ?: -1
        if (lesson != -1 && (DateTime.now().dayOfWeek == DateTimeConstants.SATURDAY || DateTime.now().dayOfWeek == DateTimeConstants.SUNDAY)) {
            if (lesson == 1)
                selectedLesson  = -1
            else
                return
        }
        this.lesson = selectedLesson
        val date: LocalDate = SchoolWeek.nextFromNow()
        val substitutes = repository.loadSubstitutesForDaySync(date, true)
        onSubstitutesLoaded(date, substitutes)
    }

    private fun onSubstitutesLoaded(date: LocalDate, substitutes: List<Substitute>) {
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
                bigTextStyle.setSummaryText(formatter.makeSubstituteKindText(substitutes[i].kind))
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
        for (i in 0 until Math.min(5, notificationLines.size))
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
