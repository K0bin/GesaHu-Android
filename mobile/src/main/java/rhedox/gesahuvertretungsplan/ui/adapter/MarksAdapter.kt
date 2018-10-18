package rhedox.gesahuvertretungsplan.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.entity.Mark
import rhedox.gesahuvertretungsplan.ui.viewHolder.ErrorViewHolder
import rhedox.gesahuvertretungsplan.ui.viewHolder.MarkViewHolder
import rhedox.gesahuvertretungsplan.ui.viewHolder.MarksCardViewHolder
import tr.xip.errorview.ErrorView

/**
 * Created by robin on 19.01.2017.
 */
class MarksAdapter(context: Context, private val isTablet: Boolean = false) : ListAdapter<Mark>(hasEmptyView = true, hasTopHeader = !isTablet) {
    var mark = ""
        set(value) {
            if (field != value) {
                field = value
                if (field.isBlank() && isTablet) {
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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_mark_card, parent, false)
            return MarksCardViewHolder(view)
        }

        if(viewType == ListAdapter.ItemTypeValues.view) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_mark, parent, false)
            return MarkViewHolder(view)
        } else {
            return ErrorViewHolder(ErrorView(parent.context))
        }
    }

    override fun bindTopHeader(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? MarksCardViewHolder)?.bind(mark)
    }

    override fun bindEmptyView(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ErrorViewHolder)?.bind(R.string.no_marks, errorTitleColor, R.drawable.ic_rip, R.string.no_marks_hint, errorMessageColor, false)
    }
}