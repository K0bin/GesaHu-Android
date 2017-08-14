package rhedox.gesahuvertretungsplan.broadcastReceiver

import android.content.Context
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import rhedox.gesahuvertretungsplan.service.SubstitutesNotifierService

/**
 * Created by robin on 26.10.2016.
 */
class SubstitutesAlarmReceiverLegacy : WakefulBroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val service = Intent(context, SubstitutesNotifierService::class.java)
        service.putExtra(SubstitutesNotifierService.extraLesson, intent.getIntExtra(SubstitutesAlarmReceiver.extraLesson, -1))
        startWakefulService(context, service)
    }
}
