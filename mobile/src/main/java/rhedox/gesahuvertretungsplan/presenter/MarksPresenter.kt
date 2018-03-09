package rhedox.gesahuvertretungsplan.presenter

import android.arch.lifecycle.Observer
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.model.database.entity.Mark
import rhedox.gesahuvertretungsplan.mvp.MarksContract
import rhedox.gesahuvertretungsplan.presenter.state.MarksState

/**
 * Created by robin on 18.01.2017.
 */
class MarksPresenter(kodein: Kodein, state: MarksState): MarksContract.Presenter {
    private var view: MarksContract.View? = null
    private val repository: BoardsRepository = kodein.instance();
    private val boardName = state.boardName;
    private val board = repository.loadBoard(boardName)
    private var marks = repository.loadMarks(boardName)

    private val boardObserver = Observer<Board?> {
        it ?: return@Observer
        onBoardLoaded(it)
    }
    private val marksObserver = Observer<List<Mark>?> {
        if (it?.isNotEmpty() != true) return@Observer
        onMarksLoaded(it)
    }

    init {
        board.observeForever(boardObserver)
        marks.observeForever(marksObserver)
    }

    fun onBoardLoaded(board: Board) {
        view?.mark = board.mark ?: ""
    }

    fun onMarksLoaded(marks: List<Mark>) {
        view?.showList(marks)
    }

    override fun attachView(view: MarksContract.View) {
        this.view = view;

        view.mark = board.value?.mark ?: ""
        view.showList(marks.value ?: listOf())
    }

    override fun detachView() {
        this.view = null;
    }

    override fun destroy() {
        board.removeObserver(boardObserver)
        marks.removeObserver(marksObserver)
    }

    override fun saveState(): MarksState {
        return MarksState(boardName)
    }
}