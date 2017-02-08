package rhedox.gesahuvertretungsplan.ui.viewHolder

import android.graphics.Color
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import org.jetbrains.anko.backgroundColor
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.ui.widget.NumberCircle

/**
 * Created by robin on 24.01.2017.
 */
class MarksCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val card = view.findViewById(R.id.marks_card) as CardView
    private val mark = view.findViewById(R.id.mark) as NumberCircle
    private val colorGood = ContextCompat.getColor(view.context, R.color.mark_good)
    private val colorAverage = ContextCompat.getColor(view.context, R.color.mark_average)
    private val colorBad = ContextCompat.getColor(view.context, R.color.mark_bad)

    fun bind(mark: Int) {
        this.mark.outlineText = mark.toString()
        if (mark > 8) {
            this.mark.outlineColor = colorGood
            this.mark.ovalColor = colorGood
        } else if (mark > 4) {
            this.mark.outlineColor = colorAverage
            this.mark.ovalColor = colorAverage
        } else {
            this.mark.outlineColor = colorBad
            this.mark.ovalColor = colorBad
        }

        if (mark == 0) {
            card.visibility == View.GONE
            card.backgroundColor = Color.BLUE
        } else {
            card.visibility = View.VISIBLE
        }
    }
}