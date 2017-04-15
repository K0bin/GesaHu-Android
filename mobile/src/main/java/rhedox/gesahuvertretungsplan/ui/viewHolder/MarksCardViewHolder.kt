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
    private val card = view.findViewById(R.id.marks_card)
    private val mark = view.findViewById(R.id.mark) as NumberCircle

    fun bind(mark: String) {
        this.mark.outlineText = mark

        if (mark.isNullOrBlank()) {
            card.visibility = View.GONE
        } else {
            card.visibility = View.VISIBLE
        }
    }
}