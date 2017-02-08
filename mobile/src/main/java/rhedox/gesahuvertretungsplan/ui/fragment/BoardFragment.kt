package rhedox.gesahuvertretungsplan.ui.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.android.appKodein
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_substitutes.*
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.mvp.BoardContract
import rhedox.gesahuvertretungsplan.presenter.BoardPresenter
import rhedox.gesahuvertretungsplan.presenter.state.BoardState
import rhedox.gesahuvertretungsplan.ui.activity.DrawerActivity
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity
import rhedox.gesahuvertretungsplan.ui.adapter.BoardPagerAdapter

/**
 * Created by robin on 24.01.2017.
 */
class BoardFragment : Fragment(), BoardContract.View {
    private object Arguments {
        const val boardId = "boardId"
    }
    private object State {
        const val presenterState = "presenterState"
    }
    companion object {
        @JvmStatic
        fun newInstance(boardId: Long): BoardFragment {
            val fragment = BoardFragment()
            val arguments = Bundle()
            arguments.putLong(Arguments.boardId, boardId)
            fragment.arguments = arguments
            return fragment
        }
    }

    private lateinit var presenter: BoardContract.Presenter;
    private var boardId: Long = 0L;

    override var title: String = ""
        get() = field
        set(value) {
            field = value
            (activity as? DrawerActivity)?.supportActionBar?.title = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        val state: BoardContract.State;
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable<BoardState>(State.presenterState)
            boardId = state.boardId
        } else {
            boardId = arguments?.getLong(Arguments.boardId, 0L) ?: 0L;
            state = BoardState(boardId)
        }

        presenter = BoardPresenter(appKodein(), state)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.destroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board, container, false);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager.adapter = BoardPagerAdapter(boardId, childFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.tabMode = TabLayout.MODE_FIXED

        val drawerActivity = activity as? DrawerActivity
        drawerActivity?.setSupportActionBar(toolbar)
        drawerActivity?.supportActionBar!!.setHomeButtonEnabled(true)
        drawerActivity?.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        drawerActivity?.syncDrawer()

        presenter.attachView(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as? DrawerActivity)?.setSupportActionBar(null)
        presenter.detachView()
    }

    override fun onStart() {
        super.onStart()

        (activity as? DrawerActivity)?.supportActionBar?.title = title
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(State.presenterState, presenter.saveState() as Parcelable)
    }
}