package rhedox.gesahuvertretungsplan.ui.adapter

import androidx.recyclerview.widget.DiffUtil

/**
 * Created by robin on 09.03.2018.
 */
class ListDiffCallback<T>(private val oldList: List<T>, private val newList: List<T>): DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = false
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int  = newList.size
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldList[oldItemPosition] == newList[newItemPosition]
}