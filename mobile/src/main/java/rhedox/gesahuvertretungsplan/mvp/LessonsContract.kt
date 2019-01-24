package rhedox.gesahuvertretungsplan.mvp

import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.presenter.state.LessonsState


/**
 * Created by robin on 18.01.2017.
 */
interface LessonsContract {
    interface Presenter {
        fun saveState(): LessonsState
        fun attachView(view: View)
        fun detachView()
        fun destroy()
    }

    interface View {
        fun showList(list: List<Lesson>)
        var lessonsTotal: Int
        var lessonsMissed: Int
        var lessonsMissedWithSickNote: Int
    }
}
