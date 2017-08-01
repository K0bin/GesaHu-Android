package rhedox.gesahuvertretungsplan.service

import android.annotation.TargetApi
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import rhedox.gesahuvertretungsplan.broadcastReceiver.SubstitutesAlarmReceiver

/**
 * Created by robin on 01.08.17.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class SubstitutesNotifierJobService: JobService() {
    override fun onStopJob(job: JobParameters?): Boolean {
        return true
    }

    override fun onStartJob(job: JobParameters?): Boolean {
        job ?: return false;

        val lesson = job.extras.getInt(SubstitutesAlarmReceiver.extraLesson, -1);
        val notifier = SubstitutesNotifier(applicationContext)
        doAsync {
            notifier.load(lesson)
            uiThread {
                jobFinished(job, false)
            }
        }
        return true;
    }

}