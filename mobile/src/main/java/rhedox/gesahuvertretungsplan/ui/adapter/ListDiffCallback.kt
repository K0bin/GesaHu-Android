package rhedox.gesahuvertretungsplan.ui.adapter

import android.support.v7.util.DiffUtil

/**
 * Created by robin on 30.01.2017.
 */
class ListDiffCallback<T>(private val oldList: List<T>, private val newList: List<T>, private val hasEmptyView: Boolean = false, private val hasTopHeader: Boolean = false) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldList.isEmpty() || newList.isEmpty() && (oldItemPosition == 0 || newItemPosition == 0)) {
            return oldList.isEmpty() && newList.isEmpty() && oldItemPosition == 0 && newItemPosition == 0 && hasEmptyView
        }
        if (newItemPosition == 0 || oldItemPosition == 0 && hasTopHeader) {
            return newItemPosition == 0 && oldItemPosition == 0 && hasTopHeader
        }

        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun getOldListSize(): Int {
        if (oldList.isNotEmpty()) {
            return if (!hasTopHeader) oldList.size else oldList.size + 1
        } else {
            if (hasEmptyView) {
                return if (hasTopHeader) 2 else 1;
            } else {
                return 0;
            }
        }
    }

    override fun getNewListSize(): Int {
        if (newList.isNotEmpty()) {
            return if (!hasTopHeader) newList.size else newList.size + 1
        } else {
            if (hasEmptyView) {
                return if (hasTopHeader) 2 else 1;
            } else {
                return 0;
            }
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (hasEmptyView) {
            if (oldItemPosition == 0 && oldList.isEmpty()) {
                return newItemPosition == 0 && newList.isEmpty()
            } else if (newItemPosition == 0 && newList.isEmpty())
                return false;
        }

        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}