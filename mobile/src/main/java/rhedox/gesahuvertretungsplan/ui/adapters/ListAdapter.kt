package rhedox.gesahuvertretungsplan.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import rhedox.gesahuvertretungsplan.ui.viewHolders.ModelViewHolder

/**
 * Created by robin on 23.01.2017.
 */
abstract class ListAdapter<T>(private val hasEmptyView: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var list = listOf<T>()
    get() = field
    set(value) {
        val count = itemCount
        field = value

        //Notify recyclerview about changes
        notifyItemRangeRemoved(0, count)
        notifyItemRangeInserted(0, itemCount)
    }

    object ItemTypeValues {
        const val view = 0;
        const val emptyView = 1;
    }

    override fun getItemCount(): Int {
        return if (list.isNotEmpty() || !hasEmptyView) list.size else 1;
    }

    override fun getItemViewType(position: Int): Int {
        return if (list.isEmpty() && hasEmptyView) ItemTypeValues.emptyView else ItemTypeValues.view
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ItemTypeValues.view) {
            (holder as ModelViewHolder<T>).bind(list[position])
        }
    }
}