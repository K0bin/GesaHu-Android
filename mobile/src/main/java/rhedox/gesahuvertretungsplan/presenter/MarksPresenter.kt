package rhedox.gesahuvertretungsplan.presenter

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.Mark
import rhedox.gesahuvertretungsplan.mvp.MarksContract
import rhedox.gesahuvertretungsplan.presenter.state.MarksState

/**
 * Created by robin on 18.01.2017.
 */
class MarksPresenter(kodein: Kodein, state: MarksContract.State): MarksContract.Presenter {
    private var view: MarksContract.View? = null
    private val repository: BoardsRepository = kodein.instance();
    private val boardId = state.boardId;

    init {
        repository.marksCallback = { boardId, marks -> if (boardId == this.boardId) onMarksLoaded(marks) }
        repository.loadMarks(boardId)
    }

    fun onMarksLoaded(marks: List<Mark>) {

    }

    override fun attachView(view: MarksContract.View, isRecreated: Boolean) {
        this.view = view;
    }

    override fun detachView() {
        this.view = null;
    }

    override fun destroy() {
        repository.destroy()
    }

    override fun saveState(): MarksContract.State {
        return MarksState(boardId)
    }
}