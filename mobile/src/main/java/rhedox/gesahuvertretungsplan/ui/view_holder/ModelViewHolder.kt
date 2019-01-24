package rhedox.gesahuvertretungsplan.ui.view_holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by robin on 23.01.2017.
 */
abstract class ModelViewHolder<T>(view: View): RecyclerView.ViewHolder(view) {
    abstract fun bind(model: T)
}