package rhedox.gesahuvertretungsplan.ui.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.model.database.Mark
import rhedox.gesahuvertretungsplan.ui.viewHolder.*
import tr.xip.errorview.ErrorView

/**
 * Created by robin on 19.01.2017.
 */
class MarksAdapter(context: Context) : ListAdapter<Mark>(hasEmptyView = true, hasTopHeader = true) {
    var mark = ""
        get() = field
        set(value) {
            field = value
            notifyItemChanged(0)
        }

    private val config: ErrorView.Config;

    init {
        val typedArray = context.theme.obtainStyledAttributes(intArrayOf(R.attr.textPrimary, R.attr.textSecondary))
        val errorTitleColor = typedArray.getColor(0, 0)
        val errorMessageColor = typedArray.getColor(1, 0)
        typedArray.recycle()

        config = ErrorView.Config.create()
                .title(context.getString(R.string.no_marks))
                .titleColor(errorTitleColor)
                .image(R.drawable.ic_rip)
                .subtitle(context.getString(R.string.no_marks_hint))
                .subtitleColor(errorMessageColor)
                .retryVisible(false)
                .build()
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
        (holder as? ErrorViewHolder)?.bind(config)
    }
}