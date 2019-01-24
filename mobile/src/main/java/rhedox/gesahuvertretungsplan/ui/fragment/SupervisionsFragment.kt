package rhedox.gesahuvertretungsplan.ui.fragment

import android.graphics.Point
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.perf.metrics.AddTrace
import kotlinx.android.synthetic.main.fragment_substitutes.*
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.share
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.entity.Supervision
import rhedox.gesahuvertretungsplan.mvp.SupervisionsContract
import rhedox.gesahuvertretungsplan.presenter.SupervisionsPresenter
import rhedox.gesahuvertretungsplan.presenter.state.SupervisionsState
import rhedox.gesahuvertretungsplan.ui.activity.DrawerActivity
import rhedox.gesahuvertretungsplan.ui.activity.NavigationActivity
import rhedox.gesahuvertretungsplan.ui.adapter.SupervisionsPagerAdapter
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp
import rhedox.gesahuvertretungsplan.util.windowManager

/**
 * Created by robin on 24.01.2017.
 */
class SupervisionsFragment : AnimationFragment(), SupervisionsContract.View {
    private lateinit var presenter: SupervisionsContract.Presenter
    private var pagerAdapter: SupervisionsPagerAdapter? = null

    private object State {
        const val presenterState = "supervisionsPresenterState"
    }
    private object Argument {
        const val date = "date"
    }
    companion object {
        @JvmStatic
        fun newInstance(date: LocalDate? = null): SupervisionsFragment {
            val fragment = SupervisionsFragment()
            val arguments = Bundle()
            if (date != null) {
                arguments.putInt(Argument.date, date.unixTimeStamp)
            }
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun showList(position: Int, list: List<Supervision>) {
        val adapter = pagerAdapter?.getAdapter(position)
        adapter?.list = list
    }

    override fun showDatePicker(defaultDate: LocalDate) {
        val picker = DatePickerFragment.newInstance(defaultDate)
        picker.callback = {
            presenter.onDatePicked(it)
        }

        picker.show(fragmentManager, "Datepicker")
    }

    override var currentTab: Int
        get() = viewPager?.currentItem ?: -1
        set(value) {
            viewPager?.currentItem = value
        }

    override var isAppBarExpanded: Boolean = true
        set(value) {
            appbarLayout.setExpanded(value)
            field = value
        }

    override var tabTitles: Array<String> = arrayOf("", "", "", "", "")
        set(value) {
            field = value
            pagerAdapter?.tabTitles = value
            pagerAdapter?.notifyDataSetChanged()
        }

    override var isRefreshing: Boolean
        get() = swipeRefreshLayout.isRefreshing
        set(value) {
            swipeRefreshLayout.isRefreshing = value
            if(!value)
                swipeRefreshLayout.clearAnimation()
        }

    override var isCabVisible: Boolean = false
        set(value) {
            if(field != value) {
                if(value) {
                    cab.show()
                } else {
                    cab.hide()
                }
                field = value
            }
        }

    override var isSwipeRefreshEnabled: Boolean
        get() = swipeRefreshLayout?.isEnabled == true
        set(value) {
            swipeRefreshLayout?.isEnabled = value
        }

    @AddTrace(name = "SupvFragCreate", enabled = true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)

        val appComponent = (context?.applicationContext as App).appComponent

        val state: SupervisionsState
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable(State.presenterState)
        } else {
            val seconds = arguments?.getInt(Argument.date, 0) ?: 0
            val date: LocalDate?
            date = if(seconds != 0) {
                localDateFromUnix(seconds)
            } else {
                null
            }
            state = SupervisionsState(date)
        }
        presenter = SupervisionsPresenter(appComponent.plusSubstitutes(), state)
    }

    @AddTrace(name = "SupvFragCreateView", enabled = true)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_substitutes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagerAdapter = SupervisionsPagerAdapter(presenter)
        if(savedInstanceState != null) {
            pagerAdapter?.restoreState(savedInstanceState)
        }
        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        isCabVisible = false

        swipeRefreshLayout.setOnRefreshListener {
            presenter.onRefresh()
        }

        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                swipeRefreshLayout.isGestureEnabled = state == ViewPager.SCROLL_STATE_IDLE
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                presenter.onActivePageChanged(position)
            }
        })

        setupCab()

        val drawerActivity = activity as? DrawerActivity
        drawerActivity?.setSupportActionBar(toolbar)
        drawerActivity?.supportActionBar?.title = getString(R.string.activity_supervisions)
        if (drawerActivity?.isPermanentDrawer == false) {
            drawerActivity.supportActionBar!!.setHomeButtonEnabled(true)
            drawerActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            drawerActivity.syncDrawer()
        }

        val size = Point()
        context!!.windowManager.defaultDisplay.getSize(size)
        if (size.x / context!!.displayMetrics.density >= 1024) {
            tabLayout.tabMode = TabLayout.MODE_FIXED
        }

        presenter.attachView(this)
    }

    private fun setupCab() {
        cab.inflateMenu(R.menu.menu_cab_main)
        cab.setOnMenuItemClickListener {
            if(id == R.id.action_share) {
                return@setOnMenuItemClickListener true
            }
            presenter.onShareButtonClicked()
            false
        }
        cab.setNavigationOnClickListener {
            isCabVisible = false
            presenter.onCabClosed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (super.onOptionsItemSelected(item))
            return true

        when (item.itemId) {
            R.id.action_load -> presenter.onDatePickerIconClicked()
        }

        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as? DrawerActivity)?.setSupportActionBar(null)
        presenter.detachView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(State.presenterState, presenter.saveState() as Parcelable)
    }

    override fun setSelected(position: Int, listPosition: Int?) {
        val adapter = pagerAdapter?.getAdapter(position)
        adapter?.setSelected(listPosition ?: -1)
    }

    override fun openSupervisionsForDay(date: LocalDate) {
        (activity as? NavigationActivity)?.navigateToSupervisions(date)
    }

    override fun share(text: String) {
        activity?.share(text, "")
    }
}