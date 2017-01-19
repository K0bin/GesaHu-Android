package rhedox.gesahuvertretungsplan.mvp

import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.Substitute

/**
 * Created by robin on 18.01.2017.
 */
interface BoardContract {
    interface State {
        val boardId: Long;
    }

    interface Presenter : NavDrawerContract.Presenter {
        fun saveState(): State
    }

    interface View : NavDrawerContract.View {
    }
}
