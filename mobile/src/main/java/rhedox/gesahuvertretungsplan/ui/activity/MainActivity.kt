package rhedox.gesahuvertretungsplan.ui.activity

import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.res.Configuration
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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.github.salomonbrys.kodein.android.KodeinAppCompatActivity
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
import rhedox.gesahuvertretungsplan.presenter.state.NavDrawerState
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import rhedox.gesahuvertretungsplan.ui.fragment.*
import rhedox.gesahuvertretungsplan.util.fixInputMethod
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.removeActivityFromTransitionManager

/**
 * Created by robin on 20.10.2016.
 */
class MainActivity : KodeinAppCompatActivity(), NavDrawerContract.View, DrawerActivity, NavigationActivity {
    private var toggle: ActionBarDrawerToggle? = null
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

    override fun getIsPermanentDrawer(): Boolean {
        return drawer == null;
    }

    companion object {
        const val currentFragmentTag = "pageFragment"
        const val state = "presenter"
    }

    object Extra {
        const val date = "date"
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
        val state = savedInstanceState?.getParcelable<NavDrawerState>(MainActivity.state) ?: NavDrawerState()
        if (lastCustomNonConfigurationInstance != null) {
            presenter = lastCustomNonConfigurationInstance as NavDrawerPresenter
        } else {
            presenter = NavDrawerPresenter(appKodein(), state)
        }

        //Setup view
        if (isAmoledBlackEnabled)
            this.setTheme(R.style.GesahuThemeAmoled)
        else
            this.setTheme(R.style.GesahuTheme)

        setContentView(R.layout.activity_main)
        setupDrawerLayout()

        headerUsername = navigationView.getHeaderView(0).findViewById<TextView>(R.id.headerUsername)
        navigationView.setNavigationItemSelectedListener {
            drawerSelected = it.itemId
            if (drawer != null) {
                drawer?.closeDrawer(GravityCompat.START)
            } else {
                onDrawerItemSelected(drawerSelected!!)
            }
            true
        }

        //Show initial fragment
        val fragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)
        val timestamp: Int? = intent?.getIntExtra(Extra.date, 0);
        val date = localDateFromUnix(timestamp)
        if (fragment != null && date == null) {
            currentFragment = fragment
        } else {
            currentFragment = SubstitutesFragment.newInstance(date)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, currentFragment, currentFragmentTag).commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //Fix Android memory leaks
        fixInputMethod()
        removeActivityFromTransitionManager()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(state, presenter.saveState() as NavDrawerState)
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
        if (drawer != null) {
            toggle = ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close)
            toggle!!.drawerArrowDrawable.color = Color.WHITE
            toggle!!.setHomeAsUpIndicator(null)
            toggle!!.isDrawerIndicatorEnabled = true
            drawer!!.addDrawerListener(toggle!!)

            listener = Listener()
            listener.callback = {
                if (drawerSelected != null) {
                    onDrawerItemSelected(drawerSelected!!)
                }
            }
            drawer!!.addDrawerListener(listener)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = 0

            if (drawer == null) {
                rootLayout.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            }
        }
    }

    private fun onDrawerItemSelected(id: Int) {
        when (id) {
            R.id.about -> presenter.onNavigationDrawerItemClicked(NavDrawerContract.DrawerIds.about);
            R.id.settings -> presenter.onNavigationDrawerItemClicked(NavDrawerContract.DrawerIds.settings);
            R.id.substitutes -> presenter.onNavigationDrawerItemClicked(NavDrawerContract.DrawerIds.substitutes);
            R.id.supervisions -> presenter.onNavigationDrawerItemClicked(NavDrawerContract.DrawerIds.supervisions);
            else -> presenter.onNavigationDrawerItemClicked(drawerSelected!!)
        }
    }

    override fun syncDrawer() {
        toggle?.syncState()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle?.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        toggle?.onConfigurationChanged(newConfig)
    }

    override fun onRetainCustomNonConfigurationInstance(): Any {
        return presenter
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (toggle?.onOptionsItemSelected(item) ?: false) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        if (drawer?.isDrawerOpen(GravityCompat.START) ?: false)
            drawer.closeDrawer(GravityCompat.START)
        else
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

            val imageView = navigationView.getHeaderView(0).findViewById<CircleImageView>(R.id.avatarView);
            if(field != null) {
                imageView.setPadding(0, 0, 0, 0)
                imageView.setImageBitmap(avatar)
            } else {
                val padding = (displayMetrics.density * 4).toInt()
                imageView.setPadding(padding, padding, padding, padding)
                imageView.setImageResource(R.drawable.ic_person)
            }
        }

    override fun navigateToSettings() {
        (currentFragment as? AnimationFragment)?.useSlideAnimation = true
        val fragment = PreferenceContainerFragment.newInstance()
        fragment.useSlideAnimation = true
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragment_container, fragment, currentFragmentTag)
                .commit()
        currentFragment = fragment
    }

    override fun navigateToAbout() {
        (currentFragment as? AnimationFragment)?.useSlideAnimation = true
        val fragment = AboutContainerFragment.newInstance()
        fragment.useSlideAnimation = true;
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, currentFragmentTag)
                .commit()
        this.currentFragment = fragment;
        title = getString(R.string.action_about)
    }

    override fun navigateToIntro() {
        startActivity(intentFor<WelcomeActivity>().newTask().clearTask())
    }

    override fun navigateToBoard(boardId: Long) {
        (currentFragment as? AnimationFragment)?.useSlideAnimation = true
        val fragment = BoardFragment.newInstance(boardId)
        fragment.useSlideAnimation = true;
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, currentFragmentTag)
                .commit()
        this.currentFragment = fragment;
    }

    override fun navigateToAuth() {
        accountManager.addAccount(GesaHuAccountService.GesaHuAuthenticator.accountType,
                null, null, null, this, null, null);
    }

    override fun navigateToSubstitutes(date: LocalDate?) {
        (currentFragment as? AnimationFragment)?.useSlideAnimation = true
        val fragment = SubstitutesFragment.newInstance(date)
        fragment.useSlideAnimation = date == null
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(if (date == null) R.anim.slide_in_from_right else R.anim.fade_in, if (date == null) R.anim.slide_out_to_left else R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragment_container, fragment, currentFragmentTag)
                .commit()
        this.currentFragment = fragment;
    }

    override fun navigateToSupervisions(date: LocalDate?) {
        (currentFragment as? AnimationFragment)?.useSlideAnimation = true
        val fragment = SupervisionsFragment.newInstance(date)
        fragment.useSlideAnimation = date == null
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(if (date == null) R.anim.slide_in_from_right else R.anim.fade_in, if (date == null) R.anim.slide_out_to_left else R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragment_container, fragment, currentFragmentTag)
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