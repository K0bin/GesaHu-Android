package rhedox.gesahuvertretungsplan.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import android.content.Intent
import com.google.android.apps.dashclock.api.DashClockExtension
import com.google.android.apps.dashclock.api.ExtensionData
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.database.SubstitutesDatabaseRepository
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity
import rhedox.gesahuvertretungsplan.util.filterRelevant

/**
 * Created by robin on 11.12.2016.
 */
class SubstitutesDashClockExtension : DashClockExtension() {
    lateinit var repo: SubstitutesDatabaseRepository
    var date = LocalDate()
    private var liveData: LiveData<List<Substitute>>? = null

    private val observer = Observer<List<Substitute>> {
        if (it?.isNotEmpty() == true) {
            onSubstitutesLoaded(it.first().date, it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        liveData?.removeObserver(observer)
    }

    override fun onUpdateData(reason: Int) {
        date = SchoolWeek.nextFromNow()
        liveData = repo.loadSubstitutesForDay(date)
        liveData!!.observeForever(observer)
    }

    private fun onSubstitutesLoaded(date: LocalDate, list: List<Substitute>) {
        if(date != this.date)
            return

        val important = list.filterRelevant(true)
        val count = important.size

        var body = ""
        for (substitute in important) {
            var title = ""

            when (substitute.kind) {
                Substitute.KindValues.substitute -> title = getString(R.string.substitute)

                Substitute.KindValues.roomChange -> title = getString(R.string.roomchange)

                Substitute.KindValues.dropped -> title = getString(R.string.dropped)

                Substitute.KindValues.test -> title = getString(R.string.test)
            }

            if ("" != body)
                body += System.getProperty("line.separator")

            body += String.format(getString(R.string.notification_summary), title, substitute.lessonText)
        }

        if (count > 0) {
            publishUpdate(ExtensionData()
                    .visible(true)
                    .icon(R.drawable.ic_notification)
                    .status(Integer.toString(count))
                    .expandedTitle(getString(R.string.app_name))
                    .expandedBody(body)
                    .clickIntent(Intent(this, MainActivity::class.java))
                    )
        } else
            publishUpdate(ExtensionData()
                    .visible(false))
    }
}
