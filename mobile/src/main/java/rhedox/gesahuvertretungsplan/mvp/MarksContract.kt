package rhedox.gesahuvertretungsplan.mvp


/**
 * Created by robin on 18.01.2017.
 */
interface MarksContract {
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
    }
}
