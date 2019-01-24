package rhedox.gesahuvertretungsplan.presenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import rhedox.gesahuvertretungsplan.dependency_injection.BoardsComponent
import rhedox.gesahuvertretungsplan.model.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.model.database.entity.Mark
import rhedox.gesahuvertretungsplan.mvp.MarksContract
import rhedox.gesahuvertretungsplan.presenter.state.MarksState
import javax.inject.Inject

/**
 * Created by robin on 18.01.2017.
 */
class MarksPresenter(component: BoardsComponent, state: MarksState): MarksContract.Presenter {
    private var view: MarksContract.View? = null
    @Inject internal lateinit var repository: BoardsRepository
    private val boardName = state.boardName
    private val board: LiveData<Board>
    private var marks: LiveData<List<Mark>>

    private val boardObserver = Observer<Board?> {
        it ?: return@Observer
        onBoardLoaded(it)
    }
    private val marksObserver = Observer<List<Mark>?> {
        if (it?.isNotEmpty() != true) return@Observer
        onMarksLoaded(it)
    }

    init {
        component.inject(this)

        board = repository.loadBoard(boardName)
        marks = repository.loadMarks(boardName)
        board.observeForever(boardObserver)
        marks.observeForever(marksObserver)
    }

    private fun onBoardLoaded(board: Board) {
        view?.mark = board.mark ?: ""
    }

    private fun onMarksLoaded(marks: List<Mark>) {
        view?.showList(marks)
    }

    override fun attachView(view: MarksContract.View) {
        this.view = view

        view.mark = board.value?.mark ?: ""
        view.showList(marks.value ?: listOf())
    }

    override fun detachView() {
        this.view = null
    }

    override fun destroy() {
        board.removeObserver(boardObserver)
        marks.removeObserver(marksObserver)
    }

    override fun saveState(): MarksState {
        return MarksState(boardName)
    }
}