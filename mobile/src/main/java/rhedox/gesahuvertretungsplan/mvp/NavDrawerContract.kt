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

    interface View {
        fun showBoards(boards: List<Board>)
        fun navigateToSettings()
        fun navigateToAbout()
        fun navigateToIntro()
        fun navigateToAuth()
        fun navigateToBoard(boardId: Long)
        var userName: String;
        var currentDrawerId: Int;
        var avatar: Bitmap?
    }
}