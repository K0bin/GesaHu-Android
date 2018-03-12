package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import android.accounts.Account
import android.graphics.Bitmap
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.mvp.MainContract

/**
 * Created by robin on 26.01.2017.
 */
class StubNavDrawerView: MainContract.View {
    var boards: List<Board> = listOf()
    var currentView: Int = ViewValues.substitutes;

    object ViewValues {
        const val substitutes = 0;
        const val settings = 1;
        const val about = 2;
        const val intro = 3;
        const val board = 4;
        const val auth = 5;
        const val supervisions = 6;
    }

    override fun showBoards(boards: List<Board>) { this.boards = boards }
    override fun navigateToSettings() { currentView = ViewValues.settings }
    override fun navigateToAbout() { currentView = ViewValues.about }
    override fun navigateToIntro() { currentView = ViewValues.intro }
    override fun navigateToAuth() { currentView = ViewValues.auth }
    override fun navigateToSubstitutes(date: LocalDate?) { currentView = ViewValues.substitutes}
    override fun navigateToBoard(boardName: String) { currentView = ViewValues.board }
    override fun navigateToSupervisions(date: LocalDate?) { currentView = ViewValues.supervisions }
    override fun updateCalendarSync(account: Account) {}

    override var userName: String = ""
    override var currentDrawerId: Int = -1
    override var avatar: Bitmap? = null
}