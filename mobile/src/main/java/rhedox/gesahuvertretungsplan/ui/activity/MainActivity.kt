package rhedox.gesahuvertretungsplan.ui.activity

import android.animation.LayoutTransition
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.transition.TransitionManager
import android.support.v4.animation.ValueAnimatorCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.*
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.github.salomonbrys.kodein.android.appKodein
import com.google.firebase.analytics.FirebaseAnalytics
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.mvp.NavDrawerContract
import rhedox.gesahuvertretungsplan.presenter.NavDrawerPresenter
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import rhedox.gesahuvertretungsplan.ui.`interface`.ContextualActionBarListener
import rhedox.gesahuvertretungsplan.ui.`interface`.FloatingActionButtonListener
import rhedox.gesahuvertretungsplan.ui.fragment.*
import rhedox.gesahuvertretungsplan.util.removeActivityFromTransitionManager

/**
 * Created by robin on 20.10.2016.
 */
class MainActivity : AppCompatActivity(), NavDrawerContract.View, DrawerActivity, NavigationActivity {
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

        //Show initial fragment
        currentFragment = SubstitutesFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, currentFragment).commit()
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
        toggle = ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close)
        toggle.drawerArrowDrawable.color = Color.WHITE
        toggle.setHomeAsUpIndicator(null)
        toggle.isDrawerIndicatorEnabled = true
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

    override fun syncDrawer() {
        toggle.syncState()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
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
        menu.removeGroup(R.id.boardsSubheader)
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
        val fragment = PreferenceContainerFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragment_container, fragment, PreferenceContainerFragment.tag)
                .commit()
    }

    override fun navigateToAbout() {
        val fragment = AboutContainerFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragment_container, fragment, AboutContainerFragment.tag)
                .commit()
        this.currentFragment = fragment;
        title = getString(R.string.action_about)
    }

    override fun navigateToIntro() {
        startActivity(intentFor<WelcomeActivity>().newTask().clearTask())
    }

    override fun navigateToBoard(boardId: Long) {
        val fragment = BoardFragment.newInstance(boardId)
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragment_container, fragment, BoardFragment.tag)
                .commit()
        this.currentFragment = fragment;
    }

    override fun navigateToAuth() {
        accountManager.addAccount(GesaHuAccountService.GesaHuAuthenticator.accountType,
                null, null, null, this, null, null);
    }

    override fun navigateToSubstitutes(date: LocalDate?) {
        val fragment = SubstitutesFragment.newInstance(date)
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragment_container, fragment, SubstitutesFragment.tag)
                .commit()
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