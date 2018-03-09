package rhedox.gesahuvertretungsplan.ui.fragment

import android.content.DialogInterface
import android.graphics.Point
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.*
import com.github.salomonbrys.kodein.android.appKodein
import com.google.firebase.perf.metrics.AddTrace
import kotlinx.android.synthetic.main.fragment_substitutes.*
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.share
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.presenter.SubstitutesPresenter
import rhedox.gesahuvertretungsplan.presenter.state.SubstitutesState
import rhedox.gesahuvertretungsplan.ui.activity.DrawerActivity
import rhedox.gesahuvertretungsplan.ui.activity.NavigationActivity
import rhedox.gesahuvertretungsplan.ui.adapter.SubstitutesPagerAdapter
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp
import rhedox.gesahuvertretungsplan.util.windowManager

/**
 * Created by robin on 24.01.2017.
 */
class SubstitutesFragment : AnimationFragment(), SubstitutesContract.View, DialogInterface.OnShowListener, DialogInterface.OnDismissListener {
    private lateinit var presenter: SubstitutesContract.Presenter;
    private var pagerAdapter: SubstitutesPagerAdapter? = null
        private set;

    private val fabPosition = PointF()
    private val fabSize = Point()
    private var fabElevation = 0f

    private object State {
        const val presenterState = "substitutesPresenterState"
    }
    private object Argument {
        const val date = "date"
    }
    companion object {
        @JvmStatic
        fun newInstance(date: LocalDate? = null): SubstitutesFragment {
            val fragment = SubstitutesFragment();
            val arguments = Bundle()
            if (date != null) {
                arguments.putInt(Argument.date, date.unixTimeStamp)
            }
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun showList(position: Int, list: List<Substitute>) {
        val adapter = pagerAdapter?.getAdapter(position)
        adapter?.list = list
    }

    override fun showDatePicker(defaultDate: LocalDate) {
        if (isResumed) {
            val picker = DatePickerFragment.newInstance(defaultDate);
            picker.callback = {
                presenter.onDatePicked(it)
            }

            picker.show(fragmentManager, "Datepicker")
        }
    }

    private var isFabMeasured = false;
    override var isFabVisible: Boolean = false
        set(value) {
            if (isFabMeasured) {
                fab.isEnabled = value
                if (value) {
                    fab.show()
                } else {
                    fab.hide()
                }
            }
            field = value
        }

    override var currentTab: Int
        get() = viewPager?.currentItem ?: -1
        set(value) {
            viewPager?.currentItem = value
        }

    override var isAppBarExpanded: Boolean = true
        get() = field
        set(value) {
            appbarLayout.setExpanded(value)
            field = value
        }

    override var tabTitles: Array<String> = arrayOf("", "", "", "", "")
        get() = field
        set(value) {
            field = value
            pagerAdapter?.tabTitles = value;
            pagerAdapter?.notifyDataSetChanged()
        }

    override var isRefreshing: Boolean
        get() = swipeRefreshLayout.isRefreshing
        set(value) {
            swipeRefreshLayout.isRefreshing = value
            if(!value)
                swipeRefreshLayout.clearAnimation();
        }

    override var isCabVisible: Boolean = false
        get() = field
        set(value) {
            if(field != value) {
                if(value) {
                    cab.show()
                } else {
                    cab.hide()
                }
                field = value;
            }
        }

    override var isSwipeRefreshEnabled: Boolean
        get() = swipeRefreshLayout?.isEnabled ?: false
        set(value) {
            swipeRefreshLayout?.isEnabled = value
        }

    @AddTrace(name = "SubFragCreate", enabled = true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)

        val state: SubstitutesState;
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable(State.presenterState)
        } else {
            val seconds = arguments?.getInt(Argument.date, 0) ?: 0
            val date: LocalDate?;
            date = if(seconds != 0) {
                localDateFromUnix(seconds)
            } else {
                null
            }
            state = SubstitutesState(date)
        }
        presenter = SubstitutesPresenter(appKodein(), state)
    }

    @AddTrace(name = "SubFragCreateView", enabled = true)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_substitutes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagerAdapter = SubstitutesPagerAdapter(presenter)
        if(savedInstanceState != null) {
            pagerAdapter?.restoreState(savedInstanceState)
        }
        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        isFabVisible = false
        isCabVisible = false

        swipeRefreshLayout.setOnRefreshListener {
            presenter.onRefresh()
        }

        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                swipeRefreshLayout?.isGestureEnabled = state == ViewPager.SCROLL_STATE_IDLE
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                presenter.onActivePageChanged(position)
            }
        })

        setupCab()

        fab.setOnClickListener {
            presenter.onFabClicked()
        }

        val drawerActivity = activity as? DrawerActivity
        drawerActivity?.setSupportActionBar(toolbar)
        drawerActivity?.supportActionBar?.title = getString(R.string.activity_substitutes)
        if (drawerActivity?.isPermanentDrawer == false) {
            drawerActivity.supportActionBar!!.setHomeButtonEnabled(true)
            drawerActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            drawerActivity.syncDrawer()
        }

        val size = Point();
        context!!.windowManager.defaultDisplay.getSize(size)
        if (size.x / context!!.displayMetrics.density >= 1024) {
            tabLayout.tabMode = TabLayout.MODE_FIXED
        }


        fab.post {
            if (fab == null) {
                return@post
            }
            fabSize.x = fab.width
            fabSize.y = fab.height

            fabPosition.x = fab.left.toFloat()
            fabPosition.y = fab.top.toFloat()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fabElevation = fab.elevation
            }
            isFabMeasured = true
            //Execute current fab state
            isFabVisible = isFabVisible
        }

        presenter.attachView(this)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        if (isFabVisible && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab?.visibility = View.VISIBLE
        }
    }

    override fun onShow(dialog: DialogInterface?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab.visibility = View.INVISIBLE
        }
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

    override fun showDialog(text: String) {
        val announcementFragment = AnnouncementFragment.newInstance("")
        announcementFragment.showListener = this
        announcementFragment.dismissListener = this
        announcementFragment.fabPosition.x = fabPosition.x
        announcementFragment.fabPosition.y = fabPosition.y
        announcementFragment.fabSize.x = fabSize.x.toFloat()
        announcementFragment.fabSize.y = fabSize.y.toFloat()
        announcementFragment.fabElevation = fabElevation
        announcementFragment.text = text;
        announcementFragment.show(fragmentManager, AnnouncementFragment.tag)
    }

    override fun openSubstitutesForDate(date: LocalDate) {
        (activity as? NavigationActivity)?.navigateToSubstitutes(date)
    }

    override fun share(text: String) {
        activity?.share(text, "")
    }
}