package rhedox.gesahuvertretungsplan.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.ui.viewHolders.LessonViewHolder

/**
 * Created by robin on 19.01.2017.
 */
class LessonsAdapter : RecyclerView.Adapter<LessonViewHolder>() {
    var lessons = listOf<Lesson>()
    get() = field
    set(value) {
        //Get count before replacing layoutManager
        val count = itemCount

        field = value

        //Notify recyclerview about changes
        if (count != value.size) {
            if (count > value.size)
                notifyItemRangeRemoved(Math.max(value.size - 1, 0), count - value.size)
            else
                notifyItemRangeInserted(Math.max(count - 1, 0), value.size - count)
        }

        notifyItemRangeChanged(0, Math.min(value.size, count))
    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bindLesson(lessons[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_lesson, parent, false)
        return LessonViewHolder(view)
    }
}