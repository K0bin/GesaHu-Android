package rhedox.gesahuvertretungsplan.presenter

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import rhedox.gesahuvertretungsplan.model.Board
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
    private var board: Board? = null
    private var marks: List<Mark> = listOf()

    init {
        repository.boardsCallback = { boards ->
            val board = boards.find { it.id == boardId }
            if (board != null) {
                onBoardLoaded(board)
            }
        }
        repository.marksCallback = { boardId, marks -> if (boardId == this.boardId) onMarksLoaded(marks) }
        repository.loadBoards()
        repository.loadMarks(boardId)
    }

    fun onBoardLoaded(board: Board) {
        view?.mark = board.mark ?: 0
        this.board = board;
    }

    fun onMarksLoaded(marks: List<Mark>) {
        view?.showList(marks)
        this.marks = marks;
    }

    override fun attachView(view: MarksContract.View) {
        this.view = view;

        view.mark = board?.mark ?: 0
        view.showList(marks)
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