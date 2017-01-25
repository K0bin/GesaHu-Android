package rhedox.gesahuvertretungsplan.mvp

import android.os.Parcelable
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.Substitute

/**
 * Created by robin on 20.10.2016.
 */
interface SubstitutesContract {
    interface State {
        val date: LocalDate?
        val canGoUp: Boolean?
        val selected: Int?
    }

    interface Presenter {
        fun onDatePickerIconClicked();
        fun onDatePicked(date: LocalDate)
        fun onActivePageChanged(position: Int)
        fun onFabClicked()
        fun onPageAttached(position: Int)
        fun onShareButtonClicked()
        fun onListItemClicked(listEntry: Int)
        fun onRefresh()
        fun onCabClosed()
        fun onBackPressed()
        fun attachView(view: View)
        fun detachView()
        fun destroy()
        fun saveState(): State
    }

    interface View {
        fun showList(position: Int, list: List<Substitute>)
        fun showDatePicker(defaultDate: LocalDate)
        var currentTab: Int
        var isFabVisible: Boolean
        var isBackButtonVisible: Boolean
        var isAppBarExpanded: Boolean
        var tabTitles: Array<String>
        var isRefreshing: Boolean
        var isSwipeRefreshEnabled: Boolean
        var isCabVisible: Boolean
        fun setSelected(position: Int, listPosition: Int?)
        fun showDialog(text: String)
        fun openSubstitutesForDate(date: LocalDate)
        fun share(text: String)
        fun goBack()
        fun finish()
    }
}