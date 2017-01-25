package rhedox.gesahuvertretungsplan.ui.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.*
import com.github.salomonbrys.kodein.android.appKodein
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_substitutes.*
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.presenter.SubstitutesPresenter
import rhedox.gesahuvertretungsplan.presenter.state.SubstitutesState
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesPagerAdapter
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 24.01.2017.
 */
class SubstitutesFragment : Fragment(), SubstitutesContract.View {
    private lateinit var presenter: SubstitutesContract.Presenter;
    private var pagerAdapter: SubstitutesPagerAdapter? = null
        private set;
    private lateinit var mainActivity: MainActivity;

    private object State {
        const val presenterState = "presenterState"
    }
    private object Argument {
        const val date = "date"
    }
    companion object {
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
        val picker = DatePickerFragment.newInstance(defaultDate);
        picker.callback = {
            presenter.onDatePicked(it)
        }

        picker.show(fragmentManager, "Datepicker")
    }

    override var isFabVisible: Boolean
        get() = mainActivity.isFabVisible
        set(value) {
            mainActivity.isFabVisible = value
        }

    override var currentTab: Int
        get() = viewPager?.currentItem ?: -1
        set(value) {
            viewPager?.currentItem = value
        }

    override var isBackButtonVisible: Boolean = false;
        get() = field
        set(value) {
            field = value
            //toggle.isDrawerIndicatorEnabled = !value
        }

    override var isAppBarExpanded: Boolean = true
        get() = field
        set(value) {
            field = value
            activity.appbarLayout.setExpanded(value)
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

    override var isCabVisible: Boolean
        get() = mainActivity.isCabVisible
        set(value) {
            mainActivity.isCabVisible = value
        }

    override var isSwipeRefreshEnabled: Boolean
        get() = swipeRefreshLayout?.isEnabled ?: false
        set(value) {
            swipeRefreshLayout?.isEnabled = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)

        val state: SubstitutesContract.State;
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable<SubstitutesState>(State.presenterState)
        } else {
            val seconds = arguments?.getInt(Argument.date, 0) ?: 0
            val date: LocalDate?;
            if(seconds != 0) {
                date = localDateFromUnix(seconds)
            } else {
                date = null
            }
            state = SubstitutesState(date)
        }
        presenter = SubstitutesPresenter(appKodein(), state)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_substitutes, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagerAdapter = SubstitutesPagerAdapter(presenter)
        if(savedInstanceState != null) {
            pagerAdapter?.restoreState(savedInstanceState)
        }
        viewPager.adapter = pagerAdapter

        activity.tabLayout.setupWithViewPager(viewPager)
        activity.tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        activity.tabLayout.visibility = View.VISIBLE
        activity.tabLayout.setSelectedTabIndicatorColor(Color.WHITE)

        isFabVisible = false

        swipeRefreshLayout.setOnRefreshListener {
            presenter.onRefresh()
        }

        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                swipeRefreshLayout.isGuestureEnabled = state == ViewPager.SCROLL_STATE_IDLE
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                presenter.onActivePageChanged(position)
            }
        })
        activity.fab.setOnClickListener { presenter.onFabClicked() }


        activity.cab.inflateMenu(R.menu.menu_cab_main)
        activity.cab.setOnMenuItemClickListener {
            if(it.itemId == R.id.action_share) {
                true
            }
            false
        }
        activity.cab.setNavigationOnClickListener {
            presenter.onCabClosed()
            isCabVisible = false
        }

        presenter.attachView(this)
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
        AnnouncementFragment.newInstance(text).show(fragmentManager, AnnouncementFragment.TAG)
    }

    override fun openSubstitutesForDate(date: LocalDate) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun share(text: String) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun goBack() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun finish() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}