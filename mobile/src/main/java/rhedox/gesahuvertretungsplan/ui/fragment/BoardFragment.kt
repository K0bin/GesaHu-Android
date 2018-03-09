package rhedox.gesahuvertretungsplan.ui.fragment

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.os.bundleOf
import com.github.salomonbrys.kodein.android.appKodein
import com.google.firebase.perf.metrics.AddTrace
import kotlinx.android.synthetic.main.fragment_substitutes.*
import org.jetbrains.anko.displayMetrics
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.mvp.BoardContract
import rhedox.gesahuvertretungsplan.presenter.BoardPresenter
import rhedox.gesahuvertretungsplan.presenter.state.BoardState
import rhedox.gesahuvertretungsplan.ui.activity.DrawerActivity
import rhedox.gesahuvertretungsplan.ui.adapter.BoardPagerAdapter

/**
 * Created by robin on 24.01.2017.
 */
class BoardFragment : AnimationFragment(), BoardContract.View, AppBarFragment {
    private object Arguments {
        const val boardName = "boardName"
    }
    private object State {
        const val presenterState = "boardPresenterState"
    }
    companion object {
        @JvmStatic
        fun newInstance(boardName: String): BoardFragment = BoardFragment().apply {
            arguments = bundleOf(Arguments.boardName to boardName)
        }
    }

    private lateinit var presenter: BoardContract.Presenter;
    private var boardName: String = "";
    private var elevationAnimator: ObjectAnimator? = null

    private lateinit var marksFragment: MarksFragment;
    private lateinit var lessonsFragment: LessonsFragment;

    override var title: String = ""
        set(value) {
            field = value
            (activity as? DrawerActivity)?.supportActionBar?.title = value
        }

    override var hasAppBarElevation: Boolean = false
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

    @AddTrace(name = "BoardFragCreate", enabled = true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        Log.d("Board", "Create")

        val state: BoardState;
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable(State.presenterState)
            boardName = state.boardName
        } else {
            boardName = arguments?.getString(Arguments.boardName) ?: "";
            state = BoardState(boardName)
        }

        presenter = BoardPresenter(appKodein(), state)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.destroy()
    }

    @AddTrace(name = "BoardFragCreateView", enabled = true)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Board", "ViewCreated")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && viewPager != null) {
            val elevation = context!!.displayMetrics.density * 4f;
            elevationAnimator = ObjectAnimator.ofFloat(appbarLayout, "elevation", 0f, elevation)
        } else {
            elevationAnimator = null;
        }

        val _lessonsFragment = childFragmentManager.findFragmentByTag("lessons");
        if (_lessonsFragment != null) {
            lessonsFragment = _lessonsFragment as LessonsFragment
        } else {
            lessonsFragment = LessonsFragment.newInstance(boardName)
        }
        val _marksFragment = childFragmentManager.findFragmentByTag("marks");
        if (_marksFragment != null) {
            marksFragment = _marksFragment as MarksFragment
        } else {
            marksFragment = MarksFragment.newInstance(boardName)
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
        if (drawerActivity?.isPermanentDrawer == false) {
            drawerActivity.supportActionBar!!.setHomeButtonEnabled(true)
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
        //Remove retained fragments from the layout so it doesn't crash (has to happen before onSaveInstanceState)
        if (activity?.isChangingConfigurations ?: false) {
            childFragmentManager.beginTransaction().remove(marksFragment).remove(lessonsFragment).commit();
        }

        super.onSaveInstanceState(outState)

        outState.putParcelable(State.presenterState, presenter.saveState() as Parcelable)
    }
}