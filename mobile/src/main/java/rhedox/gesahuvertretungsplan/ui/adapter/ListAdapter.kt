package rhedox.gesahuvertretungsplan.ui.adapter

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import rhedox.gesahuvertretungsplan.ui.viewHolder.ModelViewHolder

/**
 * Created by robin on 23.01.2017.
 */
abstract class ListAdapter<T>(private val hasEmptyView: Boolean = false, hasTopHeader: Boolean = false) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isDiffing = false;
    open var list = listOf<T>()
    set(value) {
        doAsync {
            isDiffing = true;
            val diffResult = DiffUtil.calculateDiff(ListDiffCallback(field, value, hasEmptyView, hasTopHeader), false)
            uiThread {
                diffResult.dispatchUpdatesTo(this@ListAdapter)
                field = value
                isDiffing = false;
            }
        }
    }

    var hasTopHeader = hasTopHeader
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
        return if (list.isNotEmpty() || !hasEmptyView) {
            if (!hasTopHeader) list.size else list.size + 1
        } else {
            if (!hasTopHeader) 1 else 2;
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasTopHeader && position == 0) ItemTypeValues.topHeader else if (list.isEmpty() && hasEmptyView) ItemTypeValues.emptyView else ItemTypeValues.view
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            getItemViewType(position) == ItemTypeValues.view -> (holder as? ModelViewHolder<T>)?.bind(list[if (!hasTopHeader) position else position - 1])
            getItemViewType(position) == ItemTypeValues.topHeader -> bindTopHeader(holder, 0)
            getItemViewType(position) == ItemTypeValues.emptyView -> bindEmptyView(holder, position)
        }
    }

    open fun bindTopHeader(holder: RecyclerView.ViewHolder, position: Int) {}
    open fun bindEmptyView(holder: RecyclerView.ViewHolder, position: Int) {}
}