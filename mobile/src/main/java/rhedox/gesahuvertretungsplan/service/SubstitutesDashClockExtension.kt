package rhedox.gesahuvertretungsplan.service

import android.content.Intent
import com.google.android.apps.dashclock.api.DashClockExtension
import com.google.android.apps.dashclock.api.ExtensionData
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.SubstitutesList
import rhedox.gesahuvertretungsplan.model.database.SubstitutesRepository
import rhedox.gesahuvertretungsplan.ui.activity.SubstitutesActivity

/**
 * Created by robin on 11.12.2016.
 */
class SubstitutesDashClockExtension : DashClockExtension() {
    lateinit var repo: SubstitutesRepository;
    var date = LocalDate()

    override fun onCreate() {
        super.onCreate()

        repo = SubstitutesRepository(applicationContext)
        repo.substitutesCallback = { date: LocalDate, list: List<Substitute> -> onSubstitutesLoaded(date, list) }
    }

    override fun onDestroy() {
        super.onDestroy()

        repo.destroy()
    }

    override fun onUpdateData(reason: Int) {
        date = SchoolWeek.nextFromNow()
        repo.loadSubstitutesForDay(date)
    }

    fun onSubstitutesLoaded(date: LocalDate, list: List<Substitute>) {
        if(date != this.date)
            return;

        val important = SubstitutesList.filterRelevant(list, true)
        val count = important.size

        var body = ""
        for (substitute in important) {
            var title = ""

            when (substitute.kind.toInt()) {
                Substitute.KIND_SUBSTITUTE.toInt() -> title = getString(R.string.substitute)

                Substitute.KIND_ROOM_CHANGE.toInt() -> title = getString(R.string.roomchange)

                Substitute.KIND_DROPPED.toInt() -> title = getString(R.string.dropped)

                Substitute.KIND_TEST.toInt() -> title = getString(R.string.test)
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
                    .clickIntent(Intent(this, SubstitutesActivity::class.java)))
        } else
            publishUpdate(ExtensionData()
                    .visible(false))
    }
}
