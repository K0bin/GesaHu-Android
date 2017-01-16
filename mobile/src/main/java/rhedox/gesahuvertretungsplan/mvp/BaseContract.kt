package rhedox.gesahuvertretungsplan.mvp

import android.graphics.Bitmap
import android.os.Bundle
import rhedox.gesahuvertretungsplan.model.api.json.BoardName

/**
 * Created by robin on 20.10.2016.
 */
interface BaseContract {
    interface Presenter {
        fun onNavigationDrawerItemClicked(drawerId: Int)
        fun attachView(view: View, isRecreated: Boolean)
        fun detachView()
        fun destroy()
    }

    interface View {
        fun setBoards(boards: List<String>)
        fun setAvatar(avatar: Bitmap)
        fun navigateToSettings()
        fun navigateToAbout()
        fun navigateToIntro()
        fun navigateToAuth()
        var userName: String;
        var currentDrawerId: Int;
    }
}