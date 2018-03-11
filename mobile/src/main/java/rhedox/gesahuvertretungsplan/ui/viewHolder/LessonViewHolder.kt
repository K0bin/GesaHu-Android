package rhedox.gesahuvertretungsplan.ui.viewHolder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormatterBuilder
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson

/**
 * Created by robin on 19.01.2017.
 */
class LessonViewHolder(view: View) : ModelViewHolder<Lesson>(view) {
    val date = view.findViewById<TextView>(R.id.date)
    val topic = view.findViewById<TextView>(R.id.topic)
    val homework = view.findViewById<TextView>(R.id.homework)
    val homeworkDue = view.findViewById<TextView>(R.id.homeworkDue)
    val statusIcon = view.findViewById<ImageView>(R.id.status_icon)
    val homeworkIcon = view.findViewById<ImageView>(R.id.homeworkIcon)

    val formatter: DateTimeFormatter = DateTimeFormatterBuilder().appendDayOfMonth(2).appendLiteral('.').appendMonthOfYear(2).appendLiteral('.').appendYear(4,4).toFormatter()

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun bind(lesson: Lesson) {
        date.text = lesson.date.toString(formatter)
        topic.text = lesson.topic
        homework.text = lesson.homework ?: "";
        homeworkDue.text = lesson.homeworkDue?.toString(formatter) ?: ""
        homework.visibility = if (lesson.homework.isNullOrBlank()) View.GONE else View.VISIBLE
        homeworkDue.visibility = if (lesson.homeworkDue == null) View.GONE else View.VISIBLE
        homeworkIcon.visibility = if (lesson.homework.isNullOrBlank()) View.GONE else View.VISIBLE

        statusIcon.setImageResource(if (lesson.status == Lesson.StatusValues.present) R.drawable.ic_present else if(lesson.status == Lesson.StatusValues.absentWithSickNote) R.drawable.ic_sick_note else R.drawable.ic_absent)
        statusIcon.contentDescription = statusIcon.context.getString(if (lesson.status == Lesson.StatusValues.present) R.string.description_present else if(lesson.status == Lesson.StatusValues.absentWithSickNote) R.string.description_sickNote else R.string.description_absent);
    }
}