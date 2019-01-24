package rhedox.gesahuvertretungsplan.ui.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.ui.view_holder.ErrorViewHolder
import rhedox.gesahuvertretungsplan.ui.view_holder.LessonViewHolder
import rhedox.gesahuvertretungsplan.ui.view_holder.LessonsCardViewHolder
import tr.xip.errorview.ErrorView

/**
 * Created by robin on 19.01.2017.
 */
class LessonsAdapter(context: Context, private val isTablet: Boolean = false) : ListAdapter<Lesson>(hasEmptyView = true, hasTopHeader = !isTablet) {
    var lessonsMissed = 0
        set(value) {
            if (field != value) {
                field = value
                if (lessonsTotal == 0 && lessonsMissed == 0 && lessonsMissedWithSickNote == 0 && isTablet) {
                    hasTopHeader = false
                } else {
                    hasTopHeader = true
                    notifyItemChanged(0)
                }
            }
        }
    var lessonsMissedWithSickNote = 0
        set(value) {
            if (field != value) {
                field = value
                if (lessonsTotal == 0 && lessonsMissed == 0 && lessonsMissedWithSickNote == 0 && isTablet) {
                    hasTopHeader = false
                } else {
                    hasTopHeader = true
                    notifyItemChanged(0)
                }
            }
        }
    var lessonsTotal = 0
        set(value) {
            if (field != value) {
                field = value
                if (lessonsTotal == 0 && lessonsMissed == 0 && lessonsMissedWithSickNote == 0 && isTablet) {
                    hasTopHeader = false
                } else {
                    hasTopHeader = true
                    notifyItemChanged(0)
                }
            }
        }

    @ColorInt private val errorTitleColor: Int
    @ColorInt private val errorMessageColor: Int

    init {
        val typedArray = context.theme.obtainStyledAttributes(intArrayOf(R.attr.textPrimary, R.attr.textSecondary))
        errorTitleColor = typedArray.getColor(0, 0)
        errorMessageColor = typedArray.getColor(1, 0)
        typedArray.recycle()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ItemTypeValues.topHeader) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_lesson_card, parent, false)
            return LessonsCardViewHolder(view)
        }

        return if(viewType == ListAdapter.ItemTypeValues.view) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_lesson, parent, false)
            LessonViewHolder(view)
        } else {
            ErrorViewHolder(ErrorView(parent.context))
        }
    }

    override fun bindTopHeader(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? LessonsCardViewHolder)?.bind(lessonsTotal, lessonsMissedWithSickNote, lessonsMissed)
    }

    override fun bindEmptyView(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ErrorViewHolder)?.bind(R.string.no_lessons, errorTitleColor, R.drawable.ic_rip, R.string.no_lessons_hint, errorMessageColor, false)
    }
}