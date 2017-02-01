package rhedox.gesahuvertretungsplan.ui.adapter

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
class MarksAdapter : ListAdapter<Mark>(hasEmptyView = true, hasTopHeader = false) {
    var mark = 0
        get() = field
        set(value) {
            field = value
            cardVH?.bind(mark)
            hasTopHeader = mark != 0
        }

    var cardVH: MarksCardViewHolder? = null

    private var colorGood = 0;
    private var colorAverage = 0;
    private var colorBad = 0;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ItemTypeValues.topHeader) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_mark_card, parent, false)
            cardVH = MarksCardViewHolder(view, colorGood, colorAverage, colorBad)
            return cardVH!!;
        }

        if(viewType == ListAdapter.ItemTypeValues.view) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_mark, parent, false)
            return MarkViewHolder(view)
        } else {
            return ErrorViewHolder(ErrorView(parent.context))
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        colorBad = ContextCompat.getColor(recyclerView.context, R.color.mark_bad)
        colorAverage = ContextCompat.getColor(recyclerView.context, R.color.mark_average)
        colorGood = ContextCompat.getColor(recyclerView.context, R.color.mark_good)
    }

    override fun bindTopHeader(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ItemTypeValues.topHeader) {
            (holder as MarksCardViewHolder).bind(mark)
        }
    }
}