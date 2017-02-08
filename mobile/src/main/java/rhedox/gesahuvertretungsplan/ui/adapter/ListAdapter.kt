package rhedox.gesahuvertretungsplan.ui.adapter

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import rhedox.gesahuvertretungsplan.ui.viewHolder.ModelViewHolder

/**
 * Created by robin on 23.01.2017.
 */
abstract class ListAdapter<T>(private val hasEmptyView: Boolean = false, hasTopHeader: Boolean = false) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    open var list = listOf<T>()
    get() = field
    set(value) {
        val diffResult = DiffUtil.calculateDiff(ListDiffCallback(field, value, hasEmptyView, hasTopHeader))
        diffResult.dispatchUpdatesTo(this)
        field = value
    }

    var hasTopHeader = hasTopHeader
    get() = field
    set(value) {
        if (field && !value) {
            notifyItemRemoved(0)
        } else if (!field && value) {
            notifyItemInserted(0)
        }
        field = value;
    }

    object ItemTypeValues {
        const val view = 0;
        const val emptyView = 1;
        const val topHeader = 2;
    }

    override fun getItemCount(): Int {
        if (list.isNotEmpty() || !hasEmptyView) {
            return if (!hasTopHeader) list.size else list.size + 1
        } else {
            return if (!hasTopHeader) 1 else 2;
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasTopHeader && position == 0) ItemTypeValues.topHeader else if (list.isEmpty() && hasEmptyView) ItemTypeValues.emptyView else ItemTypeValues.view
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ItemTypeValues.view) {
            (holder as ModelViewHolder<T>).bind(list[if (!hasTopHeader) position else position - 1])
        } else if (getItemViewType(position) == ItemTypeValues.topHeader) {
            bindTopHeader(holder, 0)
        } else if (getItemViewType(position) == ItemTypeValues.emptyView) {
            bindEmptyView(holder, position)
        }
    }

    open fun bindTopHeader(holder: RecyclerView.ViewHolder, position: Int) {}
    open fun bindEmptyView(holder: RecyclerView.ViewHolder, position: Int) {}
}