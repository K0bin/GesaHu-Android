package rhedox.gesahuvertretungsplan.mvp

import android.graphics.Bitmap
import rhedox.gesahuvertretungsplan.model.Board

/**
 * Created by robin on 20.10.2016.
 */
interface BaseContract {
    interface Presenter {
        fun onNavigationDrawerItemClicked(drawerId: Int)
    }

    interface View {
        fun setBoards(boards: List<Board>)
        fun setAvatar(avatar: Bitmap)
        var userName: String;
        var currentDrawerId: Int;
    }
}