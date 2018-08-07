package rhedox.gesahuvertretungsplan.ui.viewHolder

import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.backgroundColor
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.ui.widget.NumberCircle

/**
 * Created by robin on 24.01.2017.
 */
class MarksCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val card = view.findViewById<View>(R.id.marks_card)
    private val mark = view.findViewById<NumberCircle>(R.id.mark)

    fun bind(mark: String) {
        this.mark.outlineText = mark

        if (mark.isBlank()) {
            card.visibility = View.GONE
        } else {
            card.visibility = View.VISIBLE
        }
    }
}