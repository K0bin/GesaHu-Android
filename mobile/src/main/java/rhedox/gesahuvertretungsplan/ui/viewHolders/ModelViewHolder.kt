package rhedox.gesahuvertretungsplan.ui.viewHolders

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by robin on 23.01.2017.
 */
abstract class ModelViewHolder<T>(view: View): RecyclerView.ViewHolder(view) {
    abstract fun bind(model: T);
}