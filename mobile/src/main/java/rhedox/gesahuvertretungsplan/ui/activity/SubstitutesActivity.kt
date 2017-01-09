package rhedox.gesahuvertretungsplan.ui.activity

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.view.ViewPager
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import com.github.salomonbrys.kodein.android.appKodein
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.share
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.presenter.SubstitutesPresenter
import rhedox.gesahuvertretungsplan.presenter.state.SubstitutesState
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesPagerAdapter
import rhedox.gesahuvertretungsplan.ui.fragment.AnnouncementFragment
import rhedox.gesahuvertretungsplan.ui.fragment.DatePickerFragment
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 20.10.2016.
 */
class SubstitutesActivity : BaseActivity(), SubstitutesContract.View {
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

    private object State {
        const val presenter = "presenter"
    }

    object Extra {
        const val date = "date"
        const val canGoUp = "cangoup"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Create presenter
        if(lastCustomNonConfigurationInstance != null) {
            presenter = lastCustomNonConfigurationInstance as SubstitutesPresenter
            isRecreated = true
        } else {
            val state: SubstitutesState?;
            if(savedInstanceState != null) {
                state = savedInstanceState.getParcelable<SubstitutesState>(State.presenter)
                isRecreated = true
            } else {
                val seconds = intent?.extras?.getInt(Extra.date, 0) ?: 0
                val date: LocalDate?;
                if(seconds != 0) {
                    date = localDateFromUnix(seconds)
                } else {
                    date = null
                }
                val canGoUp = intent?.extras?.getBoolean(Extra.canGoUp, false) ?: false
                state = SubstitutesState(date, canGoUp = canGoUp)
            }
            presenter = SubstitutesPresenter(appKodein(), state)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setupTaskDescription()

        setContentView(R.layout.activity_main)

        pagerAdapter = SubstitutesPagerAdapter(presenter)
        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE)

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
            override fun onPageSelected(position: Int) {
                presenter.onActivePageChanged(position)
            }
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
        val adapter = pagerAdapter?.getAdapter(position)
        adapter?.showList(list)
    }

    override fun showDatePicker(defaultDate: LocalDate) {
        val picker = DatePickerFragment.newInstance(defaultDate);
        picker.callback = {
            presenter.onDatePicked(it)
        }

        picker.show(supportFragmentManager, "Datepicker")
    }

    override fun setSelected(position: Int, listPosition: Int?) {
        val adapter = pagerAdapter?.getAdapter(position)
        adapter?.setSelected(listPosition ?: -1)
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

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun goBack() {
        super.onBackPressed()
    }

    override fun showDialog(text: String) {
        AnnouncementFragment.newInstance(text).show(supportFragmentManager, AnnouncementFragment.TAG)
    }

    override fun openSubstitutesForDate(date: LocalDate) {
        val intent = intentFor<SubstitutesActivity>()
        intent.putExtra(Extra.date, date.unixTimeStamp)
        intent.putExtra(Extra.canGoUp, true)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun share(text: String) {
        share(text, "")
    }

    override fun onRetainCustomNonConfigurationInstance(): Any {
        return presenter
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if(outState == null) {
            return
        }
        outState.putParcelable(State.presenter, presenter.saveState() as Parcelable)
    }
}