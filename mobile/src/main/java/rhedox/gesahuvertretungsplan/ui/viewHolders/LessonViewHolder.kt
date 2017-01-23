package rhedox.gesahuvertretungsplan.ui.viewHolders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import org.joda.time.format.DateTimeFormatterBuilder
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.Lesson

/**
 * Created by robin on 19.01.2017.
 */
class LessonViewHolder(view: View) : ModelViewHolder<Lesson>(view) {
    val date = view.findViewById(R.id.date) as TextView
    val topic = view.findViewById(R.id.topic) as TextView
    val homework = view.findViewById(R.id.homework) as TextView
    val homeworkDue = view.findViewById(R.id.homeworkDue) as TextView

    val formatter = DateTimeFormatterBuilder().appendDayOfMonth(2).appendLiteral('.').appendMonthOfYear(2).appendLiteral('.').appendYear(4,4).toFormatter()

    override fun bind(lesson: Lesson) {
        date.text = lesson.date.toString(formatter)
        topic.text = lesson.topic
        homework.text = lesson.homeWork ?: "";
        homeworkDue.text = lesson.homeWorkDue?.toString(formatter) ?: ""
    }
}