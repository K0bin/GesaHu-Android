package rhedox.gesahuvertretungsplan.mvp

import android.graphics.Bitmap
import rhedox.gesahuvertretungsplan.model.Board

/**
 * Created by robin on 20.10.2016.
 */
interface NavDrawerContract {
    interface Presenter {
        fun onNavigationDrawerItemClicked(drawerId: Int)
        fun attachView(view: View, isRecreated: Boolean)
        fun detachView()
        fun destroy()
    }

    interface View {
        fun setBoards(boards: List<Board>)
        fun setAvatar(avatar: Bitmap)
        fun navigateToSettings()
        fun navigateToAbout()
        fun navigateToIntro()
        fun navigateToAuth()
        fun navigateToBoard(boardId: Long)
        var userName: String;
        var currentDrawerId: Int;
    }
}