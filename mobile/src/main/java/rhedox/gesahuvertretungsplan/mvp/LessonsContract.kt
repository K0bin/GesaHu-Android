package rhedox.gesahuvertretungsplan.mvp

import rhedox.gesahuvertretungsplan.model.database.Lesson


/**
 * Created by robin on 18.01.2017.
 */
interface LessonsContract {
    interface State {
        val boardId: Long;
    }

    interface Presenter {
        fun saveState(): State
        fun attachView(view: View, isRecreated: Boolean)
        fun detachView()
        fun destroy()
    }

    interface View {
        fun showList(list: List<Lesson>)
    }
}
