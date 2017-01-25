package rhedox.gesahuvertretungsplan.ui.activity

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.app.ActivityManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.github.salomonbrys.kodein.android.appKodein
import com.google.firebase.analytics.FirebaseAnalytics
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.accountManager
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.mvp.NavDrawerContract
import rhedox.gesahuvertretungsplan.presenter.NavDrawerPresenter
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import rhedox.gesahuvertretungsplan.ui.fragment.BoardFragment
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment
import rhedox.gesahuvertretungsplan.ui.fragment.SubstitutesFragment
import rhedox.gesahuvertretungsplan.util.removeActivityFromTransitionManager

/**
 * Created by robin on 20.10.2016.
 */
class MainActivity : AppCompatActivity(), NavDrawerContract.View {
    private lateinit var cabFadeIn: Animation;
    private lateinit var cabFadeOut: Animation;
    private lateinit var cabDrawerAnimator: ValueAnimator;
    private lateinit var cabDrawerIcon: DrawerArrowDrawable;

    private lateinit var toggle: ActionBarDrawerToggle
        private set
    private lateinit var listener: Listener;
    private var drawerSelected: Int? = null;
    private lateinit var analytics: FirebaseAnalytics;
    private var isAmoledBlackEnabled = false
            private set;

    private lateinit var presenter: NavDrawerContract.Presenter;

    private lateinit var headerUsername: TextView;

    private lateinit var currentFragment: Fragment;

    override var userName: String
        get() = headerUsername.text.toString();
        set(value) {
            headerUsername.text = value
        }

    var isCabVisible: Boolean = false
        get() = field
        set(value) {
            if(field != value) {
                field = value;

                if(value) {
                    cab.clearAnimation()
                    cab.startAnimation(cabFadeIn)
                    //if(!isBackButtonVisible)
                    //    cabDrawerAnimator.start()
                    //else
                    //    cabDrawerAnimator.end()
                } else {
                    cab.clearAnimation()
                    cab.startAnimation(cabFadeOut)
                    //if(!isBackButtonVisible)
                    //    cabDrawerAnimator.reverse()
                }
            }
        }

    var isFabVisible: Boolean = false
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


    override var currentDrawerId: Int = -1
        get() = field
        set(value) {
            field = value
            val menuItem: MenuItem?
            when (value) {
                NavDrawerContract.DrawerIds.settings -> menuItem = navigationView.menu.findItem(R.id.settings);
                NavDrawerContract.DrawerIds.about -> menuItem = navigationView.menu.findItem(R.id.about);
                NavDrawerContract.DrawerIds.substitutes -> menuItem = navigationView.menu.findItem(R.id.substitutes);
                else -> menuItem = navigationView.menu.findItem(value);
            }
            if(menuItem != null) {
                menuItem.isChecked = true;
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this)
        isAmoledBlackEnabled = prefs.getBoolean(PreferenceFragment.PREF_AMOLED, false)

        analytics = FirebaseAnalytics.getInstance(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupTaskDescription()
        }

        //Restore presenter
        if (lastCustomNonConfigurationInstance != null) {
            presenter = lastCustomNonConfigurationInstance as NavDrawerPresenter
        } else {
            presenter = NavDrawerPresenter(appKodein())
        }

        //Setup view
        if (isAmoledBlackEnabled)
            this.setTheme(R.style.GesahuThemeAmoled)
        else
            this.setTheme(R.style.GesahuTheme)

        setContentView(R.layout.activity_main)
        setupDrawerLayout()

        headerUsername = navigationView.getHeaderView(0).findViewById(R.id.headerUsername) as TextView
        navigationView.setNavigationItemSelectedListener {
            drawerSelected = it.itemId
            drawer.closeDrawer(GravityCompat.START)
            true
        }

        setupCab()

        //Show initial fragment
        currentFragment = SubstitutesFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, currentFragment).commit()
    }

    fun setupCab() {
        cabDrawerIcon = DrawerArrowDrawable(this)
        cabDrawerIcon.color = Color.WHITE
        cab.navigationIcon = cabDrawerIcon
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

    fun resetUi() {
        fab.hide()
        cab.visibility = View.GONE
        cab.menu.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeActivityFromTransitionManager()
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
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

    override fun onResume() {
        super.onResume()
        val menuItem = navigationView.menu.findItem(currentDrawerId);
        if(menuItem != null) {
            menuItem.isChecked = true;
        }
    }

    private fun setupDrawerLayout() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toggle = ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close)
        drawer.addDrawerListener(toggle)
        listener = Listener()
        listener.callback = {
            if (drawerSelected != null) {
                when (drawerSelected) {
                    R.id.about -> presenter.onNavigationDrawerItemClicked(NavDrawerContract.DrawerIds.about);
                    R.id.settings -> presenter.onNavigationDrawerItemClicked(NavDrawerContract.DrawerIds.settings);
                    R.id.substitutes -> presenter.onNavigationDrawerItemClicked(NavDrawerContract.DrawerIds.substitutes);
                    else -> presenter.onNavigationDrawerItemClicked(drawerSelected!!)
                }
            }
        }
        drawer.addDrawerListener(listener)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = 0
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onRetainCustomNonConfigurationInstance(): Any {
        return presenter
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START)

        super.onBackPressed()
    }

    override fun showBoards(boards: List<Board>) {
        val menu = navigationView.menu

        for (board in boards) {
            val item = menu.add(R.id.boardsSubheader, (board.id ?: 0).toInt() + NavDrawerContract.DrawerIds.board, Menu.NONE, board.name)
            item.isCheckable = true
            item.isChecked = currentDrawerId == (board.id ?: 0).toInt() + NavDrawerContract.DrawerIds.board
        }
    }

    override var avatar: Bitmap? = null
        set(value) {
            field = value

            if(field != null) {
                val imageView = navigationView.getHeaderView(0).findViewById(R.id.avatarView) as CircleImageView;
                imageView.setImageBitmap(avatar)
            }
        }

    override fun navigateToSettings() {
        startActivity(intentFor<PreferenceActivity>());
    }

    override fun navigateToAbout() {
        AboutLibs.start(this)
    }

    override fun navigateToIntro() {
        startActivity(intentFor<WelcomeActivity>().newTask().clearTask())
    }

    override fun navigateToBoard(boardId: Long) {
        resetUi()

        val fragment = BoardFragment.newInstance(boardId)
        supportFragmentManager.beginTransaction().remove(currentFragment).replace(R.id.fragment_container, fragment).commit()
        this.currentFragment = fragment;
    }

    override fun navigateToAuth() {
        accountManager.addAccount(GesaHuAccountService.GesaHuAuthenticator.accountType,
                null, null, null, this, null, null);
    }

    override fun navigateToSubstitutes() {
        resetUi()

        val fragment = SubstitutesFragment.newInstance()
        supportFragmentManager.beginTransaction().remove(currentFragment).replace(R.id.fragment_container, fragment).commit()
        this.currentFragment = fragment;
    }

    class Listener: DrawerLayout.SimpleDrawerListener() {
        var callback: ((drawer: NavigationView) -> Unit)? = null

        override fun onDrawerClosed(drawerView: View?) {
            super.onDrawerClosed(drawerView)
            callback?.invoke(drawerView as NavigationView)
        }
    }
}