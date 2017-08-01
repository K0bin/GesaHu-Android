package rhedox.gesahuvertretungsplan.mvp

import android.graphics.Bitmap
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.presenter.state.NavDrawerState

/**
 * Created by robin on 20.10.2016.
 */
interface NavDrawerContract {
    interface Presenter {
        fun onNavigationDrawerItemClicked(drawerId: Int)
        fun attachView(view: View)
        fun detachView()
        fun destroy()
        fun saveState(): NavDrawerState
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
        fun navigateToBoard(boardId: Long)
        var userName: String;
        var currentDrawerId: Int;
        var avatar: Bitmap?
    }
}