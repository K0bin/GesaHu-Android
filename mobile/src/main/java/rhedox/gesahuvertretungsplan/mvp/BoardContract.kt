package rhedox.gesahuvertretungsplan.mvp

import rhedox.gesahuvertretungsplan.presenter.state.BoardState

/**
 * Created by robin on 18.01.2017.
 */
interface BoardContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun saveState(): BoardState
        fun destroy()
    }

    interface View {
        var title: String
    }
}
