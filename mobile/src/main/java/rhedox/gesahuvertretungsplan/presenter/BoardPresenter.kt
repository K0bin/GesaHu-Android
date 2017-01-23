package rhedox.gesahuvertretungsplan.presenter

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.mvp.BoardContract
import rhedox.gesahuvertretungsplan.mvp.NavDrawerContract
import rhedox.gesahuvertretungsplan.presenter.state.BoardState

/**
 * Created by robin on 18.01.2017.
 */
class BoardPresenter(kodein: Kodein, state: BoardContract.State) : NavDrawerPresenter(kodein), BoardContract.Presenter {
    private var view: BoardContract.View? = null
    val boardId = state.boardId;
    private val repository: BoardsRepository = kodein.instance()
    private var board: Board? = null

    init {
        repository.boardsCallback = { onBoardsLoaded(it) }
        repository.loadBoards()
    }

    fun onBoardsLoaded(boards: List<Board>) {
        val board = boards.find({ it.id == boardId })
        if (board != null) {
            this.board = board;
            view?.title = board.name;
        }
    }

    override fun attachView(view: NavDrawerContract.View) {
        super.attachView(view)

        this.view = view as BoardContract.View;
        this.view?.title = this.board?.name ?: "";
    }

    override fun detachView() {
        super.detachView()

        this.view = null;
    }

    override fun destroy() {
        super.destroy()
        repository.destroy()
    }

    override fun saveState(): BoardContract.State {
        return BoardState(boardId)
    }
}