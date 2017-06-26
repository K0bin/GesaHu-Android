package rhedox.gesahuvertretungsplan.broadcastReceiver

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.annotation.IntDef
import android.support.annotation.RequiresPermission
import android.support.v4.content.WakefulBroadcastReceiver
import org.jetbrains.anko.alarmManager
import org.joda.time.DateTime
import org.joda.time.DurationFieldType
import rhedox.gesahuvertretungsplan.service.SubstitutesNotifierService

/**
 * Created by robin on 26.10.2016.
 */
class SubstitutesAlarmReceiver : WakefulBroadcastReceiver() {

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(DAILY, PER_LESSON, BOTH, flag = true)
    annotation class NotificationFrequency

    companion object {
        const val EXTRA_LESSON = "lesson";

        private val hours = intArrayOf(8, 9, 10, 11, 12, 13, 14, 15, 16)
        private val minutes = intArrayOf(45, 35, 30, 25, 25, 15, 45, 30, 30)

        const val NONE = 0L
        const val DAILY = 1L
        const val PER_LESSON = 2L
        const val BOTH = DAILY or PER_LESSON
        const val REQUEST_CODE = 0
        const val REQUEST_CODE_BASE = 14

        @JvmStatic
        @RequiresPermission(Manifest.permission.SET_ALARM)
        fun create(context: Context, firstHour: Int, firstMinute: Int, @NotificationFrequency frequency: Long) {
            if (frequency == NONE)
                return

            var time = DateTime.now()

            if (firstHour < time.hourOfDay || firstHour == time.hourOfDay && firstMinute < time.minuteOfHour)
                time = time.withFieldAdded(DurationFieldType.days(), 1)

            var alarm = DateTime(time.year, time.monthOfYear, time.dayOfMonth, firstHour, firstMinute, 0, 0)
            if (frequency and DAILY == DAILY)
                scheduleAlarm(context, alarm, REQUEST_CODE, -1)
            else
                scheduleAlarm(context, alarm, REQUEST_CODE, 1)


            if (frequency and PER_LESSON == PER_LESSON) {
                for (i in hours.indices) {
                    time = DateTime.now()

                    if (hours[i] < time.hourOfDay || hours[i] == time.hourOfDay && minutes[i] < time.minuteOfHour)
                        time = time.withFieldAdded(DurationFieldType.days(), 1)

                    alarm = DateTime(time.year, time.monthOfYear, time.dayOfMonth, hours[i], minutes[i], 0, 0)
                    scheduleAlarm(context, alarm, REQUEST_CODE_BASE + i, i + 2)
                }
            }
        }

        private fun scheduleAlarm(context: Context, time: DateTime, requestCode: Int, lesson: Int) {
            val millis = time.millis

            val manager = context.alarmManager

            val intent = Intent(context, SubstitutesAlarmReceiver::class.java)
            intent.putExtra(EXTRA_LESSON, lesson)
            val pending = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            manager.setInexactRepeating(AlarmManager.RTC, millis, AlarmManager.INTERVAL_DAY, pending)
        }

        @JvmStatic
        fun cancelDaily(context: Context) {
            val manager = context.alarmManager

            val intent = PendingIntent.getBroadcast(context, REQUEST_CODE, Intent(), PendingIntent.FLAG_NO_CREATE)
            if (intent != null)
                manager.cancel(intent)
        }

        @JvmStatic
        fun cancelLesson(context: Context) {
            val manager = context.alarmManager

            for (i in hours.indices) {
                val intent = PendingIntent.getBroadcast(context, REQUEST_CODE_BASE + i, Intent(), PendingIntent.FLAG_NO_CREATE)
                if (intent != null)
                    manager.cancel(intent)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val service = Intent(context, SubstitutesNotifierService::class.java)
        service.putExtra(SubstitutesNotifierService.extraLesson, intent.getIntExtra(EXTRA_LESSON, -1))
        startWakefulService(context, service)
    }
}
