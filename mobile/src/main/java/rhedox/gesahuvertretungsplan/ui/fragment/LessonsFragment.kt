package rhedox.gesahuvertretungsplan.ui.fragment

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.android.appKodein
import com.google.firebase.perf.metrics.AddTrace
import kotlinx.android.synthetic.main.fragment_lessons.*
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.windowManager
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.mvp.LessonsContract
import rhedox.gesahuvertretungsplan.mvp.MarksContract
import rhedox.gesahuvertretungsplan.presenter.BoardPresenter
import rhedox.gesahuvertretungsplan.presenter.LessonsPresenter
import rhedox.gesahuvertretungsplan.presenter.MarksPresenter
import rhedox.gesahuvertretungsplan.presenter.state.LessonsState
import rhedox.gesahuvertretungsplan.presenter.state.MarksState
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity
import rhedox.gesahuvertretungsplan.ui.adapter.LessonsAdapter

/**
 * Created by robin on 19.01.2017.
 */
class LessonsFragment : Fragment(), LessonsContract.View {
    lateinit var presenter: LessonsContract.Presenter;

    private lateinit var layoutManager: LinearLayoutManager;
    private lateinit var adapter: LessonsAdapter;

    object Extra {
        const val boardId = "boardId"
    }

    companion object {
        const val stateBundleName = "state"
        const val layoutManagerBundleName = "layoutManager"

        @JvmStatic
        fun newInstance(boardId: Long): LessonsFragment {
            val args = Bundle()
            args.putLong(Extra.boardId, boardId)
            val fragment = LessonsFragment()
            fragment.arguments = args
            return fragment;
        }
    }

    override var lessonsTotal: Int
        get() = adapter.lessonsTotal
        set(value) {adapter.lessonsTotal = value}
    override var lessonsMissed: Int
        get() = adapter.lessonsMissed
        set(value) {adapter.lessonsMissed = value}
    override var lessonsMissedWithSickNote: Int
        get() = adapter.lessonsMissedWithSickNote
        set(value) {adapter.lessonsMissedWithSickNote = value}

    @AddTrace(name = "LessonsFragCreate", enabled = true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        val state: LessonsState;
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable(stateBundleName)
        } else {
            val id = arguments?.getLong(Extra.boardId, -1) ?: -1
            state = LessonsState(id)
        }
        presenter = LessonsPresenter(appKodein(), state)
    }

    @AddTrace(name = "LessonsFragCreateView", enabled = true)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lessons, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        if (savedInstanceState != null) {
            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(layoutManagerBundleName))
        }
        recycler.layoutManager = layoutManager
        recycler.addItemDecoration(DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL))

        val size = Point()
        context!!.windowManager.defaultDisplay?.getSize(size)
        val isTablet = size.x >= (680 / context!!.displayMetrics.density );

        adapter = LessonsAdapter(context!!, isTablet);
        recycler.adapter = adapter;

        val cardHeight = context!!.resources.getDimension(R.dimen.topCardHeight);
        recycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalScroll = recycler.computeVerticalScrollOffset();
                (parentFragment as? AppBarFragment)?.hasAppBarElevation = totalScroll >= cardHeight
            }
        })

        presenter.attachView(this)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.detachView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(stateBundleName, presenter.saveState())
        outState.putParcelable(layoutManagerBundleName, layoutManager.onSaveInstanceState())
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

    override fun showList(list: List<Lesson>) {
        adapter.list = list;
    }
}