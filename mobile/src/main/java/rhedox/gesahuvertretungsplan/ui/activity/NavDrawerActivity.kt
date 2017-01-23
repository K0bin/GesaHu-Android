package rhedox.gesahuvertretungsplan.ui.activity

import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.TextView
import com.google.firebase.analytics.FirebaseAnalytics
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appwidget_list.*
import net.danlew.android.joda.JodaTimeAndroid
import android.support.v4.util.Pair;
import org.jetbrains.anko.accountManager
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.mvp.NavDrawerContract
import rhedox.gesahuvertretungsplan.presenter.NavDrawerPresenter
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment

/**
 * Created by robin on 20.10.2016.
 */
abstract class NavDrawerActivity : AppCompatActivity(), NavDrawerContract.View {

    protected lateinit var toggle: ActionBarDrawerToggle
        private set
    private lateinit var listener: Listener;
    private var drawerSelected: Int? = null;
    protected lateinit var analytics: FirebaseAnalytics;
    protected var isAmoledBlackEnabled = false
            private set;

    abstract val presenter: NavDrawerContract.Presenter;

    private lateinit var headerUsername: TextView;

    override var userName: String
        get() = headerUsername.text.toString();
        set(value) {
            headerUsername.text = value
        }

    override var currentDrawerId: Int = -1
        get() = field
        set(value) {
            field = value
            val menuItem = navigationView.menu.findItem(value);
            if(menuItem != null) {
                menuItem.isChecked = true;
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this)
        isAmoledBlackEnabled = prefs.getBoolean(PreferenceFragment.PREF_AMOLED, false)

        analytics = FirebaseAnalytics.getInstance(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setupTaskDescription()

        //Initialize UI
        if (isAmoledBlackEnabled)
            this.setTheme(R.style.GesahuThemeAmoled)
        else
            this.setTheme(R.style.GesahuTheme)
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

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setupDrawerLayout()

        headerUsername = navigationView.getHeaderView(0).findViewById(R.id.headerUsername) as TextView
        navigationView.setNavigationItemSelectedListener {
            drawerSelected = it.itemId
            drawer.closeDrawer(GravityCompat.START)
            true
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
                presenter.onNavigationDrawerItemClicked(drawerSelected!!);
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
            val item = menu.add(R.id.boardsSubheader, (board.id ?: 0).toInt() + 13, Menu.NONE, board.name)
            item.isCheckable = true
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
        val animBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair<View, String>(appbarLayout, "appbar"),
                                                                                  Pair<View, String>(toolbar, "toolbar"),
                                                                                  Pair<View, String>(tabLayout, "tabbar")).toBundle()
        startActivity(intentFor<BoardActivity>(BoardActivity.Extra.boardId to boardId), animBundle)
    }

    override fun navigateToAuth() {
        accountManager.addAccount(GesaHuAccountService.GesaHuAuthenticator.accountType,
                null, null, null, this, null, null);
    }

    class Listener: DrawerLayout.SimpleDrawerListener() {
        var callback: ((drawer: NavigationView) -> Unit)? = null

        override fun onDrawerClosed(drawerView: View?) {
            super.onDrawerClosed(drawerView)
            callback?.invoke(drawerView as NavigationView)
        }
    }
}