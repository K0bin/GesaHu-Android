package rhedox.gesahuvertretungsplan.ui.activity

import android.animation.Animator
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
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.support.v7.app.ActionBarDrawerToggle
import android.view.animation.DecelerateInterpolator
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.share
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 20.10.2016.
 */
class SubstitutesActivity : BaseActivity(), SubstitutesContract.View, ViewPager.OnPageChangeListener {
    object Extra {
        const val date = "date"
        const val back = "back"
    }

    override lateinit var presenter: SubstitutesContract.Presenter
    private var isRecreated: Boolean = false
    private var pagerAdapter: SubstitutesPagerAdapter? = null
            private set;
    private lateinit var cabFadeIn: Animation;
    private lateinit var cabFadeOut: Animation;
    private lateinit var cabDrawerAnimator: ValueAnimator;
    private lateinit var cabDrawerIcon: DrawerArrowDrawable;

    override var isFabVisible: Boolean = false
        get() = field
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
            if(!value)
                swipeRefreshLayout.clearAnimation();
        }

    override var isCabVisible: Boolean = false
        get() = field
        set(value) {
            if(field != value) {
                field = value;

                if(value) {
                    cab.clearAnimation()
                    cab.startAnimation(cabFadeIn)
                    if(!isBackButtonVisible)
                        cabDrawerAnimator.start()
                    else
                        cabDrawerAnimator.end()
                } else {
                    cab.clearAnimation()
                    cab.startAnimation(cabFadeOut)
                    if(!isBackButtonVisible)
                        cabDrawerAnimator.reverse()
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
        if(lastCustomNonConfigurationInstance != null) {
            presenter = lastCustomNonConfigurationInstance as SubstitutesPresenter
            isRecreated = true
        } else {
            if(savedInstanceState != null) {
                presenter = SubstitutesPresenter(this, state = savedInstanceState)
                isRecreated = true
            } else {
                val date = localDateFromUnix(intent.extras?.getInt(Extra.date))
                val canGoBack = intent.extras?.getBoolean(Extra.back, false) ?: false

                presenter = SubstitutesPresenter(this, date = date, canGoUp = canGoBack)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setupTaskDescription()

        setContentView(R.layout.activity_main)

        pagerAdapter = SubstitutesPagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE)
        viewPager.addOnPageChangeListener(this)

        fab.visibility = View.GONE
        fab.isEnabled = false

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
        cabDrawerIcon = DrawerArrowDrawable(this)
        cabDrawerIcon.color = Color.WHITE
        cab.navigationIcon = cabDrawerIcon
        cab.setOnMenuItemClickListener {
            if(it.itemId == R.id.action_share) {
                presenter.onShareButtonClicked()
                true
            }
            false
        }
        cab.setNavigationOnClickListener {
            presenter.onCabClosed()
            isCabVisible = false
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
        cabDrawerAnimator = ValueAnimator.ofFloat(0f, 1f)
        cabDrawerAnimator.addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->
            val slideOffset = valueAnimator.animatedValue as Float
            cabDrawerIcon.progress = slideOffset
        })
        cabDrawerAnimator.interpolator = DecelerateInterpolator()
        cabDrawerAnimator.duration = 250
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

    override fun onStart() {
        super.onResume()
        presenter.attachView(this, isRecreated)
    }

    override fun onStop() {
        super.onPause()
        presenter.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!isChangingConfigurations)
            presenter.destroy()
    }

    override fun showList(position: Int, list: List<Substitute>) {
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

    override fun openSubstitutesForDate(date: LocalDate) {
        val intent = intentFor<SubstitutesActivity>(Extra.back to true, Extra.date to date.unixTimeStamp)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun share(text: String) {
        share(text, "")
    }

    override fun onPageScrollStateChanged(state: Int) { }
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
    override fun onPageSelected(position: Int) {
        presenter.onActiveTabChanged(position)
    }

    override fun onRetainCustomNonConfigurationInstance(): Any {
        return presenter
    }
}