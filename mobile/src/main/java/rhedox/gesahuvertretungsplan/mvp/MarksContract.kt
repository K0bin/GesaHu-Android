package rhedox.gesahuvertretungsplan.mvp

import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.model.database.Mark


/**
 * Created by robin on 18.01.2017.
 */
interface MarksContract {
    interface State {
        val boardId: Long;
    }

    interface Presenter {
        fun saveState(): State
        fun attachView(view: View)
        fun detachView()
        fun destroy()
    }

    interface View {
        fun showList(list: List<Mark>)
        var mark: Int
    }
}
