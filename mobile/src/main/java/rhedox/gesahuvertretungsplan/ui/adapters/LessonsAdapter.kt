package rhedox.gesahuvertretungsplan.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.ui.viewHolders.ErrorViewHolder
import rhedox.gesahuvertretungsplan.ui.viewHolders.LessonViewHolder
import tr.xip.errorview.ErrorView

/**
 * Created by robin on 19.01.2017.
 */
class LessonsAdapter : ListAdapter<Lesson>(true) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == ListAdapter.ItemTypeValues.view) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_lesson, parent, false)
            return LessonViewHolder(view)
        } else {
            return ErrorViewHolder(ErrorView(parent.context))
        }
    }
}