package rhedox.gesahuvertretungsplan.mvp

import android.accounts.Account
import android.graphics.Bitmap
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.presenter.state.MainState

/**
 * Created by robin on 20.10.2016.
 */
interface MainContract {
    interface Presenter {
        fun onNavigationDrawerItemClicked(drawerId: Int)
        fun attachView(view: View)
        fun detachView()
        fun destroy()
        fun onCalendarPermissionResult(isGranted: Boolean)
        fun saveState(): MainState
    }

    object DrawerIds {
        const val substitutes = 0;
        const val supervisions = 1;
        const val settings = 2;
        const val about = 3;
        const val board = 13;
    }

    interface View {
        fun showBoards(boards: List<Board>)
        fun navigateToSettings()
        fun navigateToAbout()
        fun navigateToIntro()
        fun navigateToAuth()
        fun navigateToSubstitutes(date: LocalDate? = null)
        fun navigateToSupervisions(date: LocalDate? = null)
        fun navigateToBoard(boardName: String)
        fun updateCalendarSync(account: Account)
        var userName: String;
        var currentDrawerId: Int;
        var avatar: Bitmap?
    }
}