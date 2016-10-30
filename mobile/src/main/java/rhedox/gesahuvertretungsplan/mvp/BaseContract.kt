package rhedox.gesahuvertretungsplan.mvp

import rhedox.gesahuvertretungsplan.model.Board

/**
 * Created by robin on 20.10.2016.
 */
interface BaseContract {
    interface Presenter {
        fun onNavigationDrawerItemClicked(position: Int)
    }

    interface View {
        fun setBoards(boards: List<Board>)
        var userName: String;
    }
}