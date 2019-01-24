package rhedox.gesahuvertretungsplan.service

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.database.SubstitutesDatabaseRepository
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment
import javax.inject.Inject

/**
 * Created by robin on 10.12.2016.
 */
class SubstitutesWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ViewsFactory(applicationContext)
    }

    class ViewsFactory(private val context: Context) : RemoteViewsFactory {
        @Inject internal lateinit var repository: SubstitutesDatabaseRepository
        private var list: List<Substitute> = listOf()
        private var darkTheme = false
        private val date = SchoolWeek.nextFromNow()

        override fun onCreate() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            darkTheme = prefs.getBoolean(PreferenceFragment.PREF_WIDGET_DARK, false)

            (context.applicationContext as App).appComponent.plusSubstitutes().inject(this)
        }

        override fun onDataSetChanged() {
            list = repository.loadSubstitutesForDaySync(date, true)
        }

        override fun getViewAt(position: Int): RemoteViews? {
            if (position == 0 && list.isEmpty()) {
                val remoteViews = RemoteViews(context.packageName, R.layout.view_empty)
                if (darkTheme) {
                    remoteViews.setTextColor(R.id.subject, 0xFFFFFFFF.toInt())
                    remoteViews.setTextColor(R.id.hint, 0xFFFFFFFF.toInt())
                } else {
                    remoteViews.setTextColor(R.id.subject, 0xFF000000.toInt())
                    remoteViews.setTextColor(R.id.hint, 0xFF000000.toInt())
                }
                return remoteViews
            }

            if (list.size <= position)
                return null

            val substitute = list[position]
            val remoteViews = RemoteViews(context.packageName, R.layout.appwidget_view_substitute)
            remoteViews.setTextViewText(R.id.lesson, substitute.lessonText)
            remoteViews.setTextViewText(R.id.subject, substitute.subject)
            remoteViews.setTextViewText(R.id.teacher, substitute.teacher)
            remoteViews.setTextViewText(R.id.substituteTeacher, substitute.substitute)
            remoteViews.setTextViewText(R.id.hint, substitute.hint)
            remoteViews.setTextViewText(R.id.room, substitute.room)

            remoteViews.setTextColor(R.id.lesson, 0xFFFFFFFF.toInt())

            if (darkTheme) {
                remoteViews.setTextColor(R.id.subject, 0xFFFFFFFF.toInt())
                remoteViews.setTextColor(R.id.teacher, 0xFFFFFFFF.toInt())
                remoteViews.setTextColor(R.id.substituteTeacher, 0xFFFFFFFF.toInt())
                remoteViews.setTextColor(R.id.hint, 0xFFFFFFFF.toInt())
                remoteViews.setTextColor(R.id.room, 0xFFFFFFFF.toInt())
            } else {
                remoteViews.setTextColor(R.id.subject, 0xFF000000.toInt())
                remoteViews.setTextColor(R.id.teacher, 0xFF000000.toInt())
                remoteViews.setTextColor(R.id.substituteTeacher, 0xFF000000.toInt())
                remoteViews.setTextColor(R.id.hint, 0xFF000000.toInt())
                remoteViews.setTextColor(R.id.room, 0xFF000000.toInt())
            }

            return remoteViews
        }

        override fun getCount(): Int { return if(list.isNotEmpty()) list.size else 1; }
        override fun getViewTypeCount(): Int { return 1; }
        override fun getItemId(position: Int): Long { return if(list.isEmpty()) -1L else position.toLong(); }
        override fun getLoadingView(): RemoteViews? { return null; }
        override fun hasStableIds(): Boolean { return true; }
        override fun onDestroy() { }

    }
}