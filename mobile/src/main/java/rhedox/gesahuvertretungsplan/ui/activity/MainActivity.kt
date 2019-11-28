package rhedox.gesahuvertretungsplan.ui.activity

import android.Manifest
import android.accounts.Account
import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.mvp.MainContract
import rhedox.gesahuvertretungsplan.presenter.MainPresenter
import rhedox.gesahuvertretungsplan.presenter.state.MainState
import rhedox.gesahuvertretungsplan.service.CalendarSyncService
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import rhedox.gesahuvertretungsplan.ui.fragment.*
import rhedox.gesahuvertretungsplan.util.accountManager
import rhedox.gesahuvertretungsplan.util.isCalendarWritingPermissionGranted
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import javax.inject.Inject

/**
 * Created by robin on 20.10.2016.
 */
class MainActivity : AppCompatActivity(), MainContract.View, DrawerActivity, NavigationActivity {
    private var toggle: ActionBarDrawerToggle? = null
    private lateinit var listener: Listener
    private var drawerSelected: Int? = null
    private lateinit var analytics: FirebaseAnalytics
    private var isAmoledBlackEnabled = false

    private lateinit var headerUsername: TextView

    private lateinit var currentFragment: Fragment

    private lateinit var presenter: MainContract.Presenter

    @Inject internal lateinit var prefs: SharedPreferences

    override var userName: String
        get() = headerUsername.text.toString()
        set(value) {
            headerUsername.text = value
        }

    override var currentDrawerId: Int = -1
        set(value) {
            field = value
            val menuItem: MenuItem? = when (value) {
                MainContract.DrawerIds.settings -> navigationView.menu.findItem(R.id.settings)
                MainContract.DrawerIds.about -> navigationView.menu.findItem(R.id.about)
                MainContract.DrawerIds.substitutes -> navigationView.menu.findItem(R.id.substitutes)
                else -> navigationView.menu.findItem(value)
            }
            if(menuItem != null) {
                menuItem.isChecked = true
            }
        }

    override fun getIsPermanentDrawer(): Boolean {
        return drawer == null
    }

    companion object {
        const val currentFragmentTag = "pageFragment"
        const val state = "navPresenterState"
        const val calendarPermissionRequestCode = 9
    }

    object Extra {
        const val date = "date"
    }

    object Action {
        const val calendarPermission = "calendarPermission"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appComponent = (application as App).appComponent
        appComponent.inject(this)

        isAmoledBlackEnabled = prefs.getBoolean(PreferenceFragment.PREF_AMOLED, false)

        analytics = FirebaseAnalytics.getInstance(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupTaskDescription()
        }

        //Restore presenter
        val state = savedInstanceState?.getParcelable(MainActivity.state) ?: MainState()
        presenter = if (lastCustomNonConfigurationInstance != null) {
            lastCustomNonConfigurationInstance as MainPresenter
        } else {
            MainPresenter(appComponent.plusBoards(), state)
        }

        //Setup view
        if (isAmoledBlackEnabled)
            this.setTheme(R.style.GesahuThemeAmoled)
        else
            this.setTheme(R.style.GesahuTheme)

        setContentView(R.layout.activity_main)
        setupDrawerLayout()

        headerUsername = navigationView.getHeaderView(0).findViewById(R.id.headerUsername)
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
        val timestamp: Int? = intent?.getIntExtra(Extra.date, 0)
        val date = localDateFromUnix(timestamp)
        if (fragment != null && date == null) {
            currentFragment = fragment
        } else {
            currentFragment = SubstitutesFragment.newInstance(date)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, currentFragment, currentFragmentTag).commit()
        }

        if (intent?.action == Action.calendarPermission) {
            askForCalendarPermission()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(state, presenter.saveState())
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    @Suppress("DEPRECATION")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupTaskDescription() {
        val a = obtainStyledAttributes(intArrayOf(R.attr.colorPrimary))
        val primaryColor = a.getColor(0, 0)
        a.recycle()

        val description = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ActivityManager.TaskDescription(getString(R.string.app_name), R.drawable.ic_task, primaryColor)
        } else {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_task)
            ActivityManager.TaskDescription(getString(R.string.app_name), bitmap, primaryColor)
        }
        this.setTaskDescription(description)
    }

    override fun onResume() {
        super.onResume()
        val menuItem = navigationView.menu.findItem(currentDrawerId)
        if(menuItem != null) {
            menuItem.isChecked = true
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
                rootLayout?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        }
    }

    private fun onDrawerItemSelected(id: Int) {
        when (id) {
            R.id.about -> presenter.onNavigationDrawerItemClicked(MainContract.DrawerIds.about)
            R.id.settings -> presenter.onNavigationDrawerItemClicked(MainContract.DrawerIds.settings)
            R.id.substitutes -> presenter.onNavigationDrawerItemClicked(MainContract.DrawerIds.substitutes)
            R.id.supervisions -> presenter.onNavigationDrawerItemClicked(MainContract.DrawerIds.supervisions)
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

    override fun onDestroy() {
        super.onDestroy()

        if (!isChangingConfigurations) {
            presenter.destroy()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle?.onConfigurationChanged(newConfig)
    }

    override fun onRetainCustomNonConfigurationInstance(): Any {
        return presenter
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (toggle?.onOptionsItemSelected(item) == true) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        if (drawer?.isDrawerOpen(GravityCompat.START) == true)
            drawer!!.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun showBoards(boards: List<Board>) {
        val menu = navigationView.menu
        menu.removeGroup(R.id.boardsSubheader)
        for (board in boards) {
            val item = menu.add(R.id.boardsSubheader, board.name.hashCode() + MainContract.DrawerIds.board, Menu.NONE, board.name)
            item.isCheckable = true
            item.isChecked = currentDrawerId == board.name.hashCode() + MainContract.DrawerIds.board
        }
    }

    override var avatar: Bitmap? = null
        set(value) {
            field = value

            val imageView = navigationView.getHeaderView(0).findViewById<CircleImageView>(R.id.avatarView)
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
                .replace(R.id.fragment_container, fragment, currentFragmentTag)
                .commit()
        currentFragment = fragment
    }

    override fun navigateToAbout() {
        (currentFragment as? AnimationFragment)?.useSlideAnimation = true
        val fragment = AboutContainerFragment.newInstance()
        fragment.useSlideAnimation = true
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, currentFragmentTag)
                .commit()
        this.currentFragment = fragment
        title = getString(R.string.action_about)
    }

    override fun navigateToIntro() {
        startActivity(intentFor<WelcomeActivity>().newTask().clearTask())
    }

    override fun navigateToBoard(boardName: String) {
        (currentFragment as? AnimationFragment)?.useSlideAnimation = true
        val fragment = BoardFragment.newInstance(boardName)
        fragment.useSlideAnimation = true
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, currentFragmentTag)
                .commit()
        this.currentFragment = fragment
    }

    override fun navigateToAuth() {
        accountManager.addAccount(GesaHuAccountService.GesaHuAuthenticator.accountType,
                null, null, null, this, null, null)
    }

    override fun navigateToSubstitutes(date: LocalDate?) {
        (currentFragment as? AnimationFragment)?.useSlideAnimation = currentFragment !is SubstitutesFragment
        val fragment = SubstitutesFragment.newInstance(date)
        fragment.useSlideAnimation = currentFragment !is SubstitutesFragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, currentFragmentTag)
                .commit()
        this.currentFragment = fragment
    }

    override fun navigateToSupervisions(date: LocalDate?) {
        (currentFragment as? AnimationFragment)?.useSlideAnimation = currentFragment !is SupervisionsFragment
        val fragment = SupervisionsFragment.newInstance(date)
        fragment.useSlideAnimation = currentFragment !is SupervisionsFragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, currentFragmentTag)
                .commit()
        this.currentFragment = fragment
    }

    private fun askForCalendarPermission() {
        if (isCalendarWritingPermissionGranted) return
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR), calendarPermissionRequestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != calendarPermissionRequestCode) return
        presenter.onCalendarPermissionResult(permissions.firstOrNull() == Manifest.permission.WRITE_CALENDAR && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED)
    }

    override fun updateCalendarSync(account: Account) {
        prefs.edit {
            this.putBoolean(CalendarSyncService.alreadyAskedForCalendarPreference, true)
        }
        CalendarSyncService.updateIsSyncable(account, applicationContext, prefs)
    }

    class Listener: DrawerLayout.SimpleDrawerListener() {
        var callback: ((drawer: NavigationView) -> Unit)? = null

        override fun onDrawerClosed(drawerView: View) {
            super.onDrawerClosed(drawerView)
            callback?.invoke(drawerView as NavigationView)
        }
    }
}