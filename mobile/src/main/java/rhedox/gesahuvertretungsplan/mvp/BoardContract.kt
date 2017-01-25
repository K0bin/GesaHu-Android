package rhedox.gesahuvertretungsplan.mvp

import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.database.Lesson

/**
 * Created by robin on 18.01.2017.
 */
interface BoardContract {
    interface State {
        val boardId: Long;
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun saveState(): State
        fun destroy();
    }

    interface View {
        var title: String;
    }
}
