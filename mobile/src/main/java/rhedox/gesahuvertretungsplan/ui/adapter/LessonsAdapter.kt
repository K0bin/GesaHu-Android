package rhedox.gesahuvertretungsplan.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.ui.viewHolder.ErrorViewHolder
import rhedox.gesahuvertretungsplan.ui.viewHolder.LessonViewHolder
import rhedox.gesahuvertretungsplan.ui.viewHolder.LessonsCardViewHolder
import rhedox.gesahuvertretungsplan.ui.viewHolder.ModelViewHolder
import tr.xip.errorview.ErrorView

/**
 * Created by robin on 19.01.2017.
 */
class LessonsAdapter : ListAdapter<Lesson>(hasEmptyView = true, hasTopHeader = true) {
    var lessonsMissed = 0
        get() = field
        set(value) {
            field = value
            notifyItemChanged(0)
        }
    var lessonsMissedWithSickNote = 0
        get() = field
        set(value) {
            field = value
            notifyItemChanged(0)
        }
    var lessonsTotal = 0
        get() = field
        set(value) {
            field = value
            notifyItemChanged(0)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ItemTypeValues.topHeader) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_lesson_card, parent, false)
            return LessonsCardViewHolder(view)
        }

        if(viewType == ListAdapter.ItemTypeValues.view) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_lesson, parent, false)
            return LessonViewHolder(view)
        } else {
            return ErrorViewHolder(ErrorView(parent.context))
        }
    }

    override fun bindTopHeader(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LessonsCardViewHolder) {
            holder.bind(lessonsTotal, lessonsMissedWithSickNote, lessonsMissed)
        }
    }
}