package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import android.graphics.Bitmap
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.api.json.BoardName
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract

/**
 * Created by robin on 02.01.2017.
 */
class StubSubstitutesView : SubstitutesContract.View {

    override fun showList(position: Int, list: List<Substitute>) {listShown[position] = true}
    override fun showDatePicker(defaultDate: LocalDate) {}

    override var currentTab: Int = -1
    override var isFabVisible: Boolean = false
    override var isBackButtonVisible: Boolean = false
    override var isAppBarExpanded: Boolean = false
    override var tabTitles: Array<String> = arrayOf("","","","","")
    override var isRefreshing: Boolean = false
    override var isSwipeRefreshEnabled: Boolean = false
    override var isCabVisible: Boolean = false

    override fun setSelected(position: Int, listPosition: Int?) { selected[position] = listPosition }
    override fun showDialog(text: String) {}
    override fun openSubstitutesForDate(date: LocalDate) {}
    override fun share(text: String) {}
    override fun goBack() {}
    override fun finish() {}
    override fun setBoards(boards: List<Board>) {}
    override fun setAvatar(avatar: Bitmap) {}
    override fun navigateToIntro() {}
    override fun navigateToSettings() {}
    override fun navigateToAbout() {}
    override fun navigateToAuth() {}
    override fun navigateToBoard(boardId: Long) {}

    override var userName: String = ""
    override var currentDrawerId: Int = -1

    var listShown = arrayOf<Boolean>(false, false, false, false, false)
    var selected = arrayOfNulls<Int>(5)
}