package rhedox.gesahuvertretungsplan.ui.fragment

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.android.appKodein
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_lessons.*
import kotlinx.android.synthetic.main.fragment_substitutes.*
import org.jetbrains.anko.displayMetrics
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
class BoardFragment : Fragment(), BoardContract.View, AppBarFragment {
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
    private var elevationAnimator: ObjectAnimator? = null

    private lateinit var marksFragment: MarksFragment;
    private lateinit var lessonsFragment: LessonsFragment;

    override var title: String = ""
        get() = field
        set(value) {
            field = value
            (activity as? DrawerActivity)?.supportActionBar?.title = value
        }

    override var hasAppBarElevation: Boolean = false
        get() = field
        set(value) {
            if (value != field) {
                if (value) {
                    elevationAnimator?.start()
                } else {
                    elevationAnimator?.reverse()
                }
            }
            field = value;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && viewPager != null) {
            val elevation = context.displayMetrics.density * 4f;
            elevationAnimator = ObjectAnimator.ofFloat(appbarLayout, "elevation", 0f, elevation)
        } else {
            elevationAnimator = null;
        }

        val _lessonsFragment = childFragmentManager.findFragmentByTag("lessons");
        if (_lessonsFragment != null) {
            lessonsFragment = _lessonsFragment as LessonsFragment
        } else {
            lessonsFragment = LessonsFragment.newInstance(boardId)
        }
        val _marksFragment = childFragmentManager.findFragmentByTag("marks");
        if (_marksFragment != null) {
            marksFragment = _marksFragment as MarksFragment
        } else {
            marksFragment = MarksFragment.newInstance(boardId)
        }

        if (viewPager != null) {
            viewPager.adapter = BoardPagerAdapter(lessonsFragment, marksFragment, childFragmentManager)
            tabLayout.setupWithViewPager(viewPager)
        } else {
            childFragmentManager.beginTransaction()
                    .replace(R.id.marksContainer, marksFragment, "marks")
                    .replace(R.id.lessonsContainer, lessonsFragment, "lessons")
                    .commit();
        }

        val drawerActivity = activity as? DrawerActivity
        drawerActivity?.setSupportActionBar(toolbar)
        if (!(drawerActivity?.isPermanentDrawer ?: true)) {
            drawerActivity?.supportActionBar!!.setHomeButtonEnabled(true)
            drawerActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            drawerActivity.syncDrawer()
        }

        presenter.attachView(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        elevationAnimator = null;
        (activity as? DrawerActivity)?.setSupportActionBar(null)
        presenter.detachView()
    }

    override fun onStart() {
        super.onStart()

        (activity as? DrawerActivity)?.supportActionBar?.title = title
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //Remove retained fragments from the layout so it doesn't crash (has to happen before onSaveInstanceState
        childFragmentManager.beginTransaction().remove(marksFragment).remove(lessonsFragment).commit();

        super.onSaveInstanceState(outState)

        outState.putParcelable(State.presenterState, presenter.saveState() as Parcelable)
    }
}