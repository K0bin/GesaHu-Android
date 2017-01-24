package rhedox.gesahuvertretungsplan.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.ui.viewHolders.ErrorViewHolder
import rhedox.gesahuvertretungsplan.ui.viewHolders.LessonViewHolder
import rhedox.gesahuvertretungsplan.ui.viewHolders.LessonsCardViewHolder
import rhedox.gesahuvertretungsplan.ui.viewHolders.ModelViewHolder
import tr.xip.errorview.ErrorView

/**
 * Created by robin on 19.01.2017.
 */
class LessonsAdapter : ListAdapter<Lesson>(true) {
    var lessonsMissed = 0
        get() = field
        set(value) {
            field = value
            cardVH?.bind(lessonsTotal, lessonsMissedWithSickNote, lessonsMissed)
        }
    var lessonsMissedWithSickNote = 0
        get() = field
        set(value) {
            field = value
            cardVH?.bind(lessonsTotal, lessonsMissedWithSickNote, lessonsMissed)
        }
    var lessonsTotal = 0
        get() = field
        set(value) {
            field = value
            cardVH?.bind(lessonsTotal, lessonsMissedWithSickNote, lessonsMissed)
        }

    var cardVH: LessonsCardViewHolder? = null

    object ItemTypeValues {
        const val card = 3;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ItemTypeValues.card) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_lesson_card, parent, false)
            cardVH = LessonsCardViewHolder(view)
            return cardVH!!;
        }

        if(viewType == ListAdapter.ItemTypeValues.view) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_lesson, parent, false)
            return LessonViewHolder(view)
        } else {
            return ErrorViewHolder(ErrorView(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ItemTypeValues.card) {
            (holder as LessonsCardViewHolder).bind(lessonsTotal, lessonsMissedWithSickNote, lessonsMissed)
        } else if (getItemViewType(position) == ListAdapter.ItemTypeValues.view) {
            if (getItemViewType(position) == ListAdapter.ItemTypeValues.view) {
                (holder as ModelViewHolder<Lesson>).bind(list[position - 1])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return ItemTypeValues.card;
        }
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }
}