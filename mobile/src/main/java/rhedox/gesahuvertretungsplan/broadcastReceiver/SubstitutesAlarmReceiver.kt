package rhedox.gesahuvertretungsplan.broadcastReceiver

import android.Manifest
import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.IntDef
import android.support.annotation.LongDef
import android.support.annotation.RequiresPermission
import org.jetbrains.anko.alarmManager
import org.joda.time.DateTime
import org.joda.time.DurationFieldType
import rhedox.gesahuvertretungsplan.service.SubstitutesNotifierJobService

/**
 * Created by robin on 01.08.17.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class SubstitutesAlarmReceiver: BroadcastReceiver() {
    @Retention(AnnotationRetention.SOURCE)
    @LongDef(NotificationFrequencyValues.daily, NotificationFrequencyValues.perLesson, NotificationFrequencyValues.both, NotificationFrequencyValues.none, flag = true)
    annotation class NotificationFrequency

    object NotificationFrequencyValues {
        const val none = 0L
        const val daily = 1L
        const val perLesson = 2L
        const val both = daily or perLesson

    }

    companion object {
        const val extraLesson = "lesson";

        private val hours = intArrayOf(8, 9, 10, 11, 12, 13, 14, 15, 16)
        private val minutes = intArrayOf(45, 35, 30, 25, 25, 15, 45, 30, 30)

        const val requestCode = 0
        const val requestCodeBase = 14

        const val jobId = 0

        @JvmStatic
        @RequiresPermission(value = Manifest.permission.SET_ALARM)
        fun create(context: Context, firstHour: Int, firstMinute: Int, @NotificationFrequency frequency: Long) {
            if (frequency == NotificationFrequencyValues.none)
                return

            var time = DateTime.now()

            if (firstHour < time.hourOfDay || firstHour == time.hourOfDay && firstMinute < time.minuteOfHour)
                time = time.withFieldAdded(DurationFieldType.days(), 1)

            var alarm = DateTime(time.year, time.monthOfYear, time.dayOfMonth, firstHour, firstMinute, 0, 0)
            if (frequency and NotificationFrequencyValues.daily == NotificationFrequencyValues.daily)
                scheduleAlarm(context, alarm, requestCode, -1)
            else
                scheduleAlarm(context, alarm, requestCode, 1)


            if (frequency and NotificationFrequencyValues.perLesson == NotificationFrequencyValues.perLesson) {
                for (i in hours.indices) {
                    time = DateTime.now()

                    if (hours[i] < time.hourOfDay || hours[i] == time.hourOfDay && minutes[i] < time.minuteOfHour)
                        time = time.withFieldAdded(DurationFieldType.days(), 1)

                    alarm = DateTime(time.year, time.monthOfYear, time.dayOfMonth, hours[i], minutes[i], 0, 0)
                    scheduleAlarm(context, alarm, requestCodeBase + i, i + 2)
                }
            }
        }

        private fun scheduleAlarm(context: Context, time: DateTime, requestCode: Int, lesson: Int) {
            val millis = time.millis

            val manager = context.alarmManager

            val intent: Intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent = Intent(context, SubstitutesAlarmReceiver::class.java)
            } else {
                intent = Intent(context, SubstitutesAlarmReceiverLegacy::class.java)
            }
            intent.putExtra(extraLesson, lesson)
            val pending = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            manager.setInexactRepeating(AlarmManager.RTC, millis, AlarmManager.INTERVAL_DAY, pending)
        }

        @JvmStatic
        fun cancelDaily(context: Context) {
            val manager = context.alarmManager

            val intent = PendingIntent.getBroadcast(context, requestCode, Intent(), PendingIntent.FLAG_NO_CREATE)
            if (intent != null)
                manager.cancel(intent)
        }

        @JvmStatic
        fun cancelLesson(context: Context) {
            val manager = context.alarmManager

            for (i in hours.indices) {
                val intent = PendingIntent.getBroadcast(context, requestCodeBase + i, Intent(), PendingIntent.FLAG_NO_CREATE)
                if (intent != null)
                    manager.cancel(intent)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobInfo = JobInfo.Builder(jobId, ComponentName(context, SubstitutesNotifierJobService::class.java))
                .setOverrideDeadline(1000 * 60 * 5)
                .build()
        jobScheduler.schedule(jobInfo)
    }
}