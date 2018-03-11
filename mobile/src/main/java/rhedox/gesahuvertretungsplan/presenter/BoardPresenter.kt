package rhedox.gesahuvertretungsplan.presenter

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import rhedox.gesahuvertretungsplan.dependencyInjection.BoardsComponent
import rhedox.gesahuvertretungsplan.model.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.mvp.BoardContract
import rhedox.gesahuvertretungsplan.presenter.state.BoardState
import javax.inject.Inject

/**
 * Created by robin on 18.01.2017.
 */
class BoardPresenter(component: BoardsComponent, state: BoardState) : BoardContract.Presenter {
    private var view: BoardContract.View? = null
    val boardName = state.boardName;
    @Inject internal lateinit var repository: BoardsRepository
    private val board: LiveData<Board>

    private val observer = Observer<Board?> {
        it ?: return@Observer
        onBoardLoaded(it)
    }

    init {
        component.inject(this)

        board = repository.loadBoard(boardName);
        board.observeForever(observer)
    }

    private fun onBoardLoaded(board: Board) {
        view?.title = board.name;
    }

    override fun attachView(view: BoardContract.View) {
        this.view = view;
        this.view?.title = this.board.value?.name ?: "";
    }

    override fun detachView() {
        this.view = null;
    }

    override fun destroy() {
        board.removeObserver(observer)
    }

    override fun saveState(): BoardState {
        return BoardState(boardName)
    }
}