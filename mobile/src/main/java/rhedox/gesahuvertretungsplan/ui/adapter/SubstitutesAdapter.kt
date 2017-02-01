package rhedox.gesahuvertretungsplan.ui.adapter

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.Dimension
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.ui.anko.SubstituteView
import rhedox.gesahuvertretungsplan.ui.viewHolder.ErrorViewHolder
import rhedox.gesahuvertretungsplan.ui.viewHolder.SubstituteViewHolder
import tr.xip.errorview.ErrorView

/**
 * Created by robin on 23.01.2017.
 */
class SubstitutesAdapter(private val presenter: SubstitutesContract.Presenter, context: Context) : ListAdapter<Substitute>(hasEmptyView = true), SelectableAdapter {
    private var recyclerView: RecyclerView? = null
    private var selected: Int? = null

    //Colors
    @ColorInt private val textColor: Int
    @ColorInt private val textColorRelevant: Int
    @ColorInt private val circleColor: Int
    @ColorInt private val circleColorRelevant: Int

    @Dimension private val selectedElevation: Float = context.resources.getDimension(R.dimen.touch_raise)
    private val ankoComponent: SubstituteView = SubstituteView(context)

    init {
        val typedArray = context.theme.obtainStyledAttributes(intArrayOf(R.attr.circleColor, R.attr.circleImportantColor, R.attr.circleTextColor, R.attr.circleImportantTextColor))
        textColor = typedArray.getColor(2, 0)
        textColorRelevant = typedArray.getColor(3, 0)
        circleColorRelevant = typedArray.getColor(1, 0)
        circleColor = typedArray.getColor(0, 0)
        typedArray.recycle()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ItemTypeValues.view -> {
                val view = ankoComponent.createView(parent)
                return SubstituteViewHolder(view, presenter, textColor, textColorRelevant, circleColor, circleColorRelevant, selectedElevation)
            }

            else -> return ErrorViewHolder(ErrorView(parent.context))
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)

        this.recyclerView = null
    }

    override fun setSelected(position: Int) {
        if (position != -1) {
            setViewHolderSelected(selected, false)
            setViewHolderSelected(position, true)
        } else if (selected != null) {
            setViewHolderSelected(selected, false)
        }
        selected = position
    }

    private fun setViewHolderSelected(position: Int?, isSelected: Boolean) {
        if (recyclerView == null || position == null)
            return

        val viewHolder = recyclerView!!.findViewHolderForAdapterPosition(position)
        if (viewHolder is SubstituteViewHolder) {
            viewHolder.setSelected(isSelected, true)
        }
    }
}