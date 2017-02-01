package rhedox.gesahuvertretungsplan.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.*
import com.github.salomonbrys.kodein.android.appKodein
import org.jetbrains.anko.share
import kotlinx.android.synthetic.main.fragment_substitutes.*
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.presenter.SubstitutesPresenter
import rhedox.gesahuvertretungsplan.presenter.state.SubstitutesState
import rhedox.gesahuvertretungsplan.ui.`interface`.ContextualActionBarListener
import rhedox.gesahuvertretungsplan.ui.`interface`.FloatingActionButtonListener
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity
import rhedox.gesahuvertretungsplan.ui.adapter.SubstitutesPagerAdapter
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 24.01.2017.
 */
class SubstitutesFragment : Fragment(), SubstitutesContract.View, FloatingActionButtonListener, ContextualActionBarListener {
    private lateinit var presenter: SubstitutesContract.Presenter;
    private var pagerAdapter: SubstitutesPagerAdapter? = null
        private set;
    private var mainActivity: MainActivity? = null;

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
        get() = mainActivity?.isFabVisible ?: false
        set(value) {
            mainActivity?.isFabVisible = value
        }

    override var currentTab: Int
        get() = viewPager?.currentItem ?: -1
        set(value) {
            viewPager?.currentItem = value
        }

    override var isAppBarExpanded: Boolean
        get() = mainActivity?.isAppBarLayoutExpanded ?: false
        set(value) {
            mainActivity?.isAppBarLayoutExpanded = value
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
        get() = mainActivity?.isCabVisible ?: false
        set(value) {
            mainActivity?.isCabVisible = value
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

        mainActivity = context as? MainActivity
    }

    override fun onDetach() {
        super.onDetach()

        mainActivity = null;
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
        mainActivity?.setupTabBarForFragment(viewPager, TabLayout.MODE_SCROLLABLE);
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

        mainActivity?.setupCabForFragment(R.menu.menu_cab_main)
        presenter.attachView(this)
    }

    override fun onStart() {
        super.onStart()

        mainActivity?.title = getString(R.string.activity_substitutes)
    }

    override fun onFabClicked() { presenter.onFabClicked() }

    override fun onItemClicked(id: Int) {
        if(id == R.id.action_share) {
            true
        }
        presenter.onShareButtonClicked()
    }

    override fun onCabClosed() { presenter.onCabClosed() }

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
        mainActivity?.navigateToSubstitutes(date)
    }

    override fun share(text: String) {
        activity.share(text, "")
    }
}