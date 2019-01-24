package rhedox.gesahuvertretungsplan.ui.adapter

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.ui.view_holder.ErrorViewHolder
import rhedox.gesahuvertretungsplan.ui.view_holder.SubstituteViewHolder
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
    @ColorInt val errorTitleColor: Int
    @ColorInt val errorMessageColor: Int

    @Dimension private val selectedElevation: Float = context.resources.getDimension(R.dimen.touch_raise)

    init {
        val typedArray = context.theme.obtainStyledAttributes(intArrayOf(R.attr.circleColor, R.attr.circleImportantColor, R.attr.circleTextColor, R.attr.circleImportantTextColor, R.attr.textPrimary, R.attr.textSecondary))
        textColor = typedArray.getColor(2, 0)
        textColorRelevant = typedArray.getColor(3, 0)
        circleColorRelevant = typedArray.getColor(1, 0)
        circleColor = typedArray.getColor(0, 0)
        errorTitleColor = typedArray.getColor(4, 0)
        errorMessageColor = typedArray.getColor(5, 0)
        typedArray.recycle()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemTypeValues.view -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.view_substitute, parent, false)
                SubstituteViewHolder(view, presenter, textColor, textColorRelevant, circleColor, circleColorRelevant, selectedElevation)
            }

            else -> ErrorViewHolder(ErrorView(parent.context))
        }
    }

    override fun bindEmptyView(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ErrorViewHolder)?.bind(R.string.no_substitutes, errorTitleColor, R.drawable.no_substitutes, R.string.no_substitutes_hint, errorMessageColor, false)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (holder is SubstituteViewHolder) {
            holder.setSelected(position == selected, false)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
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