package rhedox.gesahuvertretungsplan.mvp

import rhedox.gesahuvertretungsplan.model.database.entity.Mark
import rhedox.gesahuvertretungsplan.presenter.state.MarksState


/**
 * Created by robin on 18.01.2017.
 */
interface MarksContract {
    interface Presenter {
        fun saveState(): MarksState
        fun attachView(view: View)
        fun detachView()
        fun destroy()
    }

    interface View {
        fun showList(list: List<Mark>)
        var mark: String
    }
}
