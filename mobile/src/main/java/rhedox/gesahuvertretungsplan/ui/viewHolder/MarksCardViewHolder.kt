package rhedox.gesahuvertretungsplan.ui.viewHolder

import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.ui.widget.NumberCircle

/**
 * Created by robin on 24.01.2017.
 */
class MarksCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val mark = view.findViewById(R.id.mark) as NumberCircle
    private val colorGood = ContextCompat.getColor(view.context, R.color.mark_good)
    private val colorAverage = ContextCompat.getColor(view.context, R.color.mark_average)
    private val colorBad = ContextCompat.getColor(view.context, R.color.mark_bad)

    fun bind(mark: Int) {
        this.mark.outlineText = mark.toString()
        if (mark > 8) {
            this.mark.outlineColor = colorGood
        } else if (mark > 4) {
            this.mark.outlineColor = colorAverage
        } else {
            this.mark.outlineColor = colorBad
        }
    }
}