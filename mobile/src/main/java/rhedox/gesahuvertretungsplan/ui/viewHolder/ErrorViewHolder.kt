package rhedox.gesahuvertretungsplan.ui.viewHolder

import android.view.ViewGroup
import androidx.annotation.*

import androidx.recyclerview.widget.RecyclerView
import rhedox.gesahuvertretungsplan.R
import tr.xip.errorview.ErrorView

/**
 * Created by Robin on 07.05.2016.
 */
class ErrorViewHolder(private val view: ErrorView) : RecyclerView.ViewHolder(view) {

    init {
        val context = view.context

        //Set width & margin
        val params = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val margin = context.resources.getDimension(R.dimen.errorView_margin_top)
        params.setMargins(0, margin.toInt(), 0, margin.toInt())
        view.layoutParams = params
    }

    fun bind(@StringRes title: Int, @ColorInt titleColor: Int, @DrawableRes imageRes: Int, @StringRes subtitle: Int, @ColorInt subtitleColor: Int, retryVisible: Boolean) {
        view.setTitle(title)
        view.setTitleColor(titleColor)
        view.setImage(imageRes)
        view.setSubtitle(subtitle)
        view.setSubtitleColor(subtitleColor)
        view.setRetryVisible(retryVisible)
    }
}
