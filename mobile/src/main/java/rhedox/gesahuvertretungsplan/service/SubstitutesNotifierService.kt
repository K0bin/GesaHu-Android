package rhedox.gesahuvertretungsplan.service

import android.app.*
import android.content.ContentValues
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
@Suppress("DEPRECATION")
class SubstitutesNotifierService : IntentService("SubstitutesNotifier") {
    companion object {
        const val extraLesson = "lesson";
        const val requestCodeBase = 64
        const val groupKey = "gesahuvpsubstitutes"
        const val substitutesChannel = "substitutes"
    }

    private lateinit var notifier: SubstitutesNotifier;
    private var lastIntent: Intent? = null

    override fun onCreate() {
        super.onCreate()

        notifier = SubstitutesNotifier(applicationContext)
    }

    override fun onHandleIntent(intent: Intent) {
        lastIntent = intent;
        val lesson = intent.getIntExtra(extraLesson, -1)
        notifier.load(lesson)
        WakefulBroadcastReceiver.completeWakefulIntent(lastIntent)
        lastIntent = null;
    }

    override fun onDestroy() {
        super.onDestroy()
        if(lastIntent != null)
            WakefulBroadcastReceiver.completeWakefulIntent(lastIntent)
    }
}
