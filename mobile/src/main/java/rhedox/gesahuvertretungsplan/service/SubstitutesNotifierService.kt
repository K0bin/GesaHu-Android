package rhedox.gesahuvertretungsplan.service

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver

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
