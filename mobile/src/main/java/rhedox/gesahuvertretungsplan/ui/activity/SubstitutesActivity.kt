package rhedox.gesahuvertretungsplan.ui.activity

import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.ColorInt
import android.support.v4.app.NotificationCompatSideChannelService
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.util.Pair
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.pawegio.kandroid.d
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.mvp.BaseContract
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.presenter.BasePresenter
import rhedox.gesahuvertretungsplan.presenter.SubstitutesPresenter
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesPagerAdapter
import rhedox.gesahuvertretungsplan.ui.fragment.AnnouncementFragment
import rhedox.gesahuvertretungsplan.ui.fragment.DatePickerFragment

/**
 * Created by robin on 20.10.2016.
 */
class SubstitutesActivity : BaseActivity(), SubstitutesContract.View, ViewPager.OnPageChangeListener {
    companion object {
        const val EXTRA_DATE = "date";
        const val EXTRA_BACK = "back";

        const val STATE_TITLES = "titles"
    }

    override lateinit var presenter: SubstitutesContract.Presenter
    private var pagerAdapter: SubstitutesPagerAdapter? = null
            private set;
    private lateinit var cabFadeIn: Animation;
    private lateinit var cabFadeOut: Animation;

    override var isFabVisible: Boolean = false
        get() = fab.visibility == View.VISIBLE
        set(value) {
            if(value != field) {
                if(value)
                    fab.show()
                else
                    fab.hide()

                field = value
                fab.isEnabled = value
            }
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
            toggle.isDrawerIndicatorEnabled = !value
        }

    override var isAppBarExpanded: Boolean = true
        get() = field
        set(value) {
            field = value
            appbarLayout.setExpanded(value)
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
        }

    override var isCabVisible: Boolean = false
        get() = field
        set(value) {
            if(field != value) {
                field = value;

                if(value) {
                    cab.clearAnimation()
                    cab.startAnimation(cabFadeIn)
                } else {
                    cab.clearAnimation()
                    cab.startAnimation(cabFadeOut)
                }
            }
        }

    override var isSwipeRefreshEnabled: Boolean
        get() = swipeRefreshLayout?.isEnabled ?: false
        set(value) {
            swipeRefreshLayout?.isEnabled = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Create presenter
        val presenterFragment = supportFragmentManager.findFragmentByTag(SubstitutesPresenter.tag);
        if(presenterFragment != null) {
            presenter = presenterFragment as SubstitutesContract.Presenter;
        } else {
            val _presenter = SubstitutesPresenter()
            _presenter.arguments = intent?.extras ?: Bundle.EMPTY
            supportFragmentManager.beginTransaction().add(_presenter, SubstitutesPresenter.tag).commit();
            presenter = _presenter
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setupTaskDescription()

        setContentView(R.layout.activity_main)

        val titles = savedInstanceState?.getStringArray(STATE_TITLES)

        pagerAdapter = SubstitutesPagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        if(titles != null)
            pagerAdapter!!.tabTitles = titles;
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE)
        viewPager.addOnPageChangeListener(this)

        fab.visibility = View.GONE

        swipeRefreshLayout.setOnRefreshListener {
            presenter.onRefresh()
        }

        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                swipeRefreshLayout.isGuestureEnabled = state == ViewPager.SCROLL_STATE_IDLE
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {}
        })

        fab.setOnClickListener { presenter.onFabClicked() }

        cab.inflateMenu(R.menu.menu_cab_main)
        cab.setOnMenuItemClickListener {
            if(it.itemId == R.id.action_share) {
                presenter.onShareButtonClicked()
                true
            }
            false
        }
        cab.setNavigationOnClickListener {
            presenter.onCabClosed()
            cab.startLayoutAnimation()
            cab.visibility = View.GONE
        }
        cabFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        cabFadeIn.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {cab.visibility = View.VISIBLE}
            override fun onAnimationStart(animation: Animation) {cab.visibility = View.VISIBLE}
        })
        cabFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        cabFadeOut.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {cab.visibility = View.GONE}
            override fun onAnimationStart(animation: Animation) {cab.visibility = View.VISIBLE}
        })

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupTaskDescription() {
        val a = obtainStyledAttributes(intArrayOf(R.attr.colorPrimary))
        val primaryColor = a.getColor(0, 0)
        a.recycle()

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_task)
        val description = ActivityManager.TaskDescription(getString(R.string.app_name), bitmap, primaryColor)
        this.setTaskDescription(description)
    }

    override fun populateList(position: Int, list: List<Substitute>) {
        Log.d("SubstituesActivity", "Populating list: $position, ${list.size} items")
        val fragment = pagerAdapter?.getFragment(supportFragmentManager, position)
        fragment?.populateList(list)
    }

    override fun showDatePicker(defaultDate: LocalDate) {
        val picker = DatePickerFragment.newInstance(defaultDate);
        picker.callback = {
            presenter.onDatePicked(it)
        }

        picker.show(supportFragmentManager, "Datepicker")
    }

    override fun setSelected(position: Int, listPosition: Int) {
        val fragment = pagerAdapter?.getFragment(supportFragmentManager, position)
        fragment?.setSelected(listPosition)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item == null) return false;

        if (super.onOptionsItemSelected(item))
            return true

        when (item.itemId) {
            R.id.action_load -> presenter.onDatePickerIconClicked()
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return false
    }

    override fun showDialog(text: String) {
        AnnouncementFragment.newInstance(text).show(supportFragmentManager, AnnouncementFragment.TAG)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray(STATE_TITLES, pagerAdapter?.tabTitles)
    }

    override fun onPageScrollStateChanged(state: Int) { }
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
    override fun onPageSelected(position: Int) {
        presenter.onActiveTabChanged(position)
    }
}