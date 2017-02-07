package rhedox.gesahuvertretungsplan.ui.viewHolder

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.ui.widget.NumberCircle

/**
 * Created by robin on 24.01.2017.
 */
class LessonsCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val card = view.findViewById(R.id.lessons_card) as CardView
    private val lessonsTotal = view.findViewById(R.id.lessons_total) as NumberCircle
    private val lessonsMissedWithSickNote = view.findViewById(R.id.lessons_missed_with_sick_note) as NumberCircle
    private val lessonsMissed = view.findViewById(R.id.lessons_missed) as NumberCircle

    fun bind(lessonsTotal: Int, lessonsMissedWithSickNote: Int, lessonsMissed: Int) {
        this.lessonsTotal.outlineText = lessonsTotal.toString()
        this.lessonsMissedWithSickNote.outlineText = lessonsMissedWithSickNote.toString()
        this.lessonsMissed.outlineText = lessonsMissed.toString()

        if (lessonsTotal == 0 && lessonsMissedWithSickNote == 0 && lessonsMissed == 0) {
            card.visibility = View.GONE
        } else {
            card.visibility = View.VISIBLE
        }
    }
}