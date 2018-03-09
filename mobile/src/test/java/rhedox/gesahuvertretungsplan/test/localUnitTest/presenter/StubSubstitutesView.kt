package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract

/**
 * Created by robin on 02.01.2017.
 */
class StubSubstitutesView : SubstitutesContract.View {

    override fun showList(position: Int, list: List<Substitute>) {listShown[position] = true}
    override fun showDatePicker(defaultDate: LocalDate) {}

    override var currentTab: Int = -1
    override var isFabVisible: Boolean = false
    override var isAppBarExpanded: Boolean = false
    override var tabTitles: Array<String> = arrayOf("","","","","")
    override var isRefreshing: Boolean = false
    override var isSwipeRefreshEnabled: Boolean = false
    override var isCabVisible: Boolean = false

    override fun setSelected(position: Int, listPosition: Int?) { selected[position] = listPosition }
    override fun showDialog(text: String) {}
    override fun openSubstitutesForDate(date: LocalDate) {}
    override fun share(text: String) {}

    var listShown = arrayOf(false, false, false, false, false)
    var selected = arrayOfNulls<Int>(5)
}