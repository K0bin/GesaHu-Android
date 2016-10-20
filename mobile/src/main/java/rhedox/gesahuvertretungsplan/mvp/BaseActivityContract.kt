package rhedox.gesahuvertretungsplan.mvp

import rhedox.gesahuvertretungsplan.model.Board

/**
 * Created by robin on 20.10.2016.
 */
interface BaseActivityContract {
    interface Presenter {
    }

    interface View {
        fun setBoards(boards: List<Board>)
    }
}