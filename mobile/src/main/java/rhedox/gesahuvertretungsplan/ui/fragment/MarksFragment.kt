package rhedox.gesahuvertretungsplan.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.salomonbrys.kodein.android.appKodein
import rhedox.gesahuvertretungsplan.mvp.MarksContract
import rhedox.gesahuvertretungsplan.presenter.BoardPresenter
import rhedox.gesahuvertretungsplan.presenter.MarksPresenter
import rhedox.gesahuvertretungsplan.presenter.state.MarksState

/**
 * Created by robin on 19.01.2017.
 */
class MarksFragment : Fragment(), MarksContract.View {
    lateinit var presenter: MarksContract.Presenter;

    object Extra {
        const val boardId = "boardId"
    }

    companion object {
        const val stateBundleName = "state"

        @JvmStatic
        fun createInstance(boardId: Long): MarksFragment {
            val args = Bundle()
            args.putLong(Extra.boardId, boardId)
            val fragment = MarksFragment()
            fragment.arguments = args
            return fragment;
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        val state: MarksContract.State;
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable<MarksState>(stateBundleName)
        } else {
            val id = arguments?.getLong(Extra.boardId, -1) ?: -1
            state = MarksState(id)
        }
        presenter = MarksPresenter(appKodein(), state)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(stateBundleName, presenter.saveState() as MarksState)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this, false)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }
}