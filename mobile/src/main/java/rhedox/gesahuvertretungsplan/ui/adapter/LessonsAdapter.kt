package rhedox.gesahuvertretungsplan.ui.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.ui.viewHolder.ErrorViewHolder
import rhedox.gesahuvertretungsplan.ui.viewHolder.LessonViewHolder
import rhedox.gesahuvertretungsplan.ui.viewHolder.LessonsCardViewHolder
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

    private val config: ErrorView.Config;

    init {
        val typedArray = context.theme.obtainStyledAttributes(intArrayOf(R.attr.textPrimary, R.attr.textSecondary))
        val errorTitleColor = typedArray.getColor(0, 0)
        val errorMessageColor = typedArray.getColor(1, 0)
        typedArray.recycle()

        config = ErrorView.Config.create()
                .title(context.getString(R.string.no_lessons))
                .titleColor(errorTitleColor)
                .image(R.drawable.ic_rip)
                .subtitle(context.getString(R.string.no_lessons_hint))
                .subtitleColor(errorMessageColor)
                .retryVisible(false)
                .build()
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
        (holder as? ErrorViewHolder)?.bind(config)
    }
}