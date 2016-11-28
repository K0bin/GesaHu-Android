package rhedox.gesahuvertretungsplan.mvp

import android.graphics.Bitmap
import android.os.Bundle
import rhedox.gesahuvertretungsplan.model.Board

/**
 * Created by robin on 20.10.2016.
 */
interface BaseContract {
    interface Presenter {
        fun onNavigationDrawerItemClicked(drawerId: Int)
        fun attachView(view: View)
        fun detachView()
        fun saveState(bundle: Bundle)
        fun destroy()
    }

    interface View {
        fun setBoards(boards: List<Board>)
        fun setAvatar(avatar: Bitmap)
        fun openSettings()
        fun openAbout()
        var userName: String;
        var currentDrawerId: Int;
    }
}