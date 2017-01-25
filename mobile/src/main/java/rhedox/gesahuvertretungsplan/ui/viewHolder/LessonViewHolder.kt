package rhedox.gesahuvertretungsplan.ui.viewHolder

import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.joda.time.format.DateTimeFormatter
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
    val statusIcon = view.findViewById(R.id.status_icon) as ImageView

    val absentDrawable: Drawable = ContextCompat.getDrawable(view.context, R.drawable.ic_absent)
    val presentDrawable: Drawable = ContextCompat.getDrawable(view.context, R.drawable.ic_present)
    val sickNote: Drawable = ContextCompat.getDrawable(view.context, R.drawable.ic_sick_note)

    val formatter: DateTimeFormatter = DateTimeFormatterBuilder().appendDayOfMonth(2).appendLiteral('.').appendMonthOfYear(2).appendLiteral('.').appendYear(4,4).toFormatter()

    override fun bind(lesson: Lesson) {
        date.text = lesson.date.toString(formatter)
        topic.text = lesson.topic
        homework.text = lesson.homeWork ?: "";
        homeworkDue.text = lesson.homeWorkDue?.toString(formatter) ?: ""
        statusIcon.setImageDrawable(if (lesson.status == Lesson.StatusValues.present) presentDrawable else if(lesson.status == Lesson.StatusValues.absentWithSickNote) sickNote else absentDrawable)
    }
}