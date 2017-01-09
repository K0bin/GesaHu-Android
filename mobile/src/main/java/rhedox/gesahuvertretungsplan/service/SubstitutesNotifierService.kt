package rhedox.gesahuvertretungsplan.service

import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.WakefulBroadcastReceiver
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.SubstituteFormatter
import rhedox.gesahuvertretungsplan.model.SubstitutesList
import rhedox.gesahuvertretungsplan.model.database.SubstitutesRepository
import rhedox.gesahuvertretungsplan.presenter.SubstitutesPresenter
import rhedox.gesahuvertretungsplan.ui.activity.SubstitutesActivity
import rhedox.gesahuvertretungsplan.util.SubstituteShareUtils
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 26.10.2016.
 */
class SubstitutesNotifierService : IntentService("SubstitutesNotifier") {
    companion object {
        const val EXTRA_LESSON = "lesson";
        const val REQUEST_CODE_BASE = 64
        const val GROUP_KEY = "gesahuvpsubstitutes"
    }

    private var color: Int = 0;
    private var lesson: Int = -1;

    private var date: LocalDate? = null;
    private var intent: Intent? = null;

    private lateinit var repository: SubstitutesRepository;

    override fun onCreate() {
        super.onCreate()

        //Color is used for the notifications
        color = ContextCompat.getColor(applicationContext, R.color.colorDefaultAccent)
        repository = SubstitutesRepository(this)
        repository.substitutesCallback = { date: LocalDate, list: List<Substitute> -> onSubstitutesLoaded(date, list) }
    }

    override fun onHandleIntent(intent: Intent) {
        lesson = intent.getIntExtra(EXTRA_LESSON, -1)

        if (lesson != -1 && (DateTime.now().dayOfWeek == DateTimeConstants.SATURDAY || DateTime.now().dayOfWeek == DateTimeConstants.SUNDAY)) {
            if (lesson == 1)
                lesson = -1
            else
                return
        }
        date = SchoolWeek.nextFromNow()
        repository.loadSubstitutesForDay(date!!)
        this.intent = intent
    }

    fun onSubstitutesLoaded(date: LocalDate, substitutes: List<Substitute>) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        notificationManager.cancelAll()

        //Store titles for summary notification
        val titles = mutableListOf<String>()

        for (i in substitutes.indices) {
            if ((lesson == -1 || lesson == substitutes[i].lessonBegin) && substitutes[i].isRelevant) {

                //Text to display
                val notificationText = SubstituteFormatter.makeNotificationText(applicationContext, substitutes[i])
                val title = SubstituteFormatter.makeSubstituteKindText(applicationContext, substitutes[i].kind)
                val body = String.format(applicationContext.getString(R.string.notification_summary), title, substitutes[i].lessonText)
                titles.add(body)

                val builder = NotificationCompat.Builder(applicationContext)
                //Open app on click on notification
                val launchIntent = Intent(applicationContext, SubstitutesActivity::class.java)
                launchIntent.putExtra(SubstitutesActivity.Extra.date, date.unixTimeStamp)
                val launchPending = PendingIntent.getActivity(applicationContext, REQUEST_CODE_BASE + titles.size, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT)
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
                builder.setGroup(GROUP_KEY)

                //Only relevant for JELLY_BEAN and higher
                val pending = SubstituteShareUtils.makePendingShareIntent(applicationContext, LocalDate.now(), substitutes[i])
                val action = NotificationCompat.Action(R.drawable.ic_share, applicationContext.getString(R.string.share), pending)
                builder.addAction(action)

                //Only relevant for LOLLIPOP and higher
                builder.setCategory(NotificationCompat.CATEGORY_EVENT)
                builder.priority = NotificationCompat.PRIORITY_DEFAULT
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                builder.color = color

                notificationManager.notify(i, builder.build())
            }
        }

        //Notification group summary
        val summary = makeSummaryNotification(lesson, date, titles)
        if (summary != null)
            notificationManager.notify(titles.size + 13, summary)

        //TeslaUnread
        try {
            val cv = ContentValues()

            cv.put("tag", "rhedox.gesahuvertretungsplan/rhedox.gesahuvertretungsplan.ui.activity.SubstitutesActivity")

            cv.put("count", SubstitutesList.countRelevant(substitutes))

            applicationContext.contentResolver.insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), cv)

        } catch (ex: IllegalArgumentException) { /* TeslaUnread is not installed. */ }

        if(intent != null)
            WakefulBroadcastReceiver.completeWakefulIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.destroy()
        if(intent != null)
            WakefulBroadcastReceiver.completeWakefulIntent(intent)
    }

    private fun makeSummaryNotification(lesson: Int, date: LocalDate?, notificationLines: List<String>): Notification? {

        if (notificationLines.size <= 1)
            return null

        //This summary notification, denoted by setGroupSummary(true), is the only notification that appears on Marshmallow and lower devices and should (you guessed it) summarize all of the individual notifications.
        val builder = NotificationCompat.Builder(applicationContext)

        //Open app on click on notification
        val launchIntent = Intent(applicationContext, SubstitutesActivity::class.java)
        if (date != null) {
            launchIntent.putExtra(SubstitutesActivity.Extra.date, date.unixTimeStamp)
        }
        val launchPending = PendingIntent.getActivity(applicationContext, REQUEST_CODE_BASE + notificationLines.size + 13, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(launchPending)

        //Normal notification
        builder.setContentText(String.format("%s %s", Integer.toString(notificationLines.size), applicationContext.getString(R.string.lessons)))
        builder.setSmallIcon(R.drawable.ic_notification)
        val title: String
        val summary: String
        if (lesson == -1) {
            builder.setContentInfo("1-10")
            title = applicationContext.getString(R.string.notification_summary_day)
            summary = applicationContext.getString(R.string.notification_summary_day_hint)
        } else {
            builder.setContentInfo(Integer.toString(lesson))
            title = applicationContext.getString(R.string.notification_summary_lesson)
            summary = applicationContext.getString(R.string.notification_summary_lesson_hint)
        }
        builder.setContentTitle(title)

        //Inbox style expanded notification
        val inboxStyle = NotificationCompat.InboxStyle()
        for (i in 0..Math.min(5, notificationLines.size) - 1)
            inboxStyle.addLine(notificationLines[i])

        if (notificationLines.size > 5)
            inboxStyle.setSummaryText(String.format(applicationContext.getString(R.string.inbox_style), Integer.toString(notificationLines.size - 5)))
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
        builder.setGroup(GROUP_KEY)

        return builder.build()
    }
}
