package rhedox.gesahuvertretungsplan.mvp

import android.graphics.Bitmap
import rhedox.gesahuvertretungsplan.model.Board

/**
 * Created by robin on 20.10.2016.
 */
interface NavDrawerContract {
    interface Presenter {
        fun onNavigationDrawerItemClicked(drawerId: Int)
        fun attachView(view: View)
        fun detachView()
        fun destroy()
    }

    object DrawerIds {
        const val substitutes = 0;
        const val settings = 1;
        const val about = 2;
        const val board = 13;
    }

    interface View {
        fun showBoards(boards: List<Board>)
        fun navigateToSettings()
        fun navigateToAbout()
        fun navigateToIntro()
        fun navigateToAuth()
        fun navigateToSubstitutes()
        fun navigateToBoard(boardId: Long)
        var userName: String;
        var currentDrawerId: Int;
        var avatar: Bitmap?
    }
}