package rhedox.gesahuvertretungsplan.ui.fragment

import android.graphics.Point
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.perf.metrics.AddTrace
import org.jetbrains.anko.displayMetrics
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.mvp.LessonsContract
import rhedox.gesahuvertretungsplan.presenter.LessonsPresenter
import rhedox.gesahuvertretungsplan.presenter.state.LessonsState
import rhedox.gesahuvertretungsplan.ui.adapter.LessonsAdapter
import rhedox.gesahuvertretungsplan.util.windowManager

/**
 * Created by robin on 19.01.2017.
 */
class LessonsFragment : Fragment(), LessonsContract.View {
    lateinit var presenter: LessonsContract.Presenter

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: LessonsAdapter

    object Arguments {
        const val boardName = "boardName"
    }

    companion object {
        const val stateBundleName = "state"
        const val layoutManagerBundleName = "layoutManager"

        @JvmStatic
        fun newInstance(boardName: String): LessonsFragment = LessonsFragment().apply {
            arguments = bundleOf(Arguments.boardName to boardName)
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

        val appComponent = (context?.applicationContext as App).appComponent

        val argName = arguments?.getString(Arguments.boardName) ?: ""
        val state = savedInstanceState?.getParcelable(stateBundleName) ?: LessonsState(argName)
        presenter = LessonsPresenter(appComponent.plusBoards(), state)
    }

    @AddTrace(name = "LessonsFragCreateView", enabled = true)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lessons, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        if (savedInstanceState != null) {
            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(layoutManagerBundleName))
        }
        recycler.layoutManager = layoutManager
        recycler.addItemDecoration(DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL))

        val size = Point()
        context!!.windowManager.defaultDisplay?.getSize(size)
        val isTablet = size.x >= (680 / context!!.displayMetrics.density )

        adapter = LessonsAdapter(context!!, isTablet)
        recycler.adapter = adapter

        val cardHeight = context!!.resources.getDimension(R.dimen.topCardHeight)
        recycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalScroll = recycler.computeVerticalScrollOffset()
                (parentFragment as? AppBarFragment)?.hasAppBarElevation = !adapter.hasTopHeader || totalScroll >= cardHeight
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
        adapter.list = list
    }
}