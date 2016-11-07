package rhedox.gesahuvertretungsplan.ui.activity

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu
import android.widget.TextView
import com.google.firebase.analytics.FirebaseAnalytics
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import net.danlew.android.joda.JodaTimeAndroid
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.mvp.BaseContract
import rhedox.gesahuvertretungsplan.presenter.BasePresenter
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment

/**
 * Created by robin on 20.10.2016.
 */
abstract class BaseActivity : AppCompatActivity(), BaseContract.View {

    protected lateinit var toggle: ActionBarDrawerToggle
        private set
    protected lateinit var analytics: FirebaseAnalytics;
    protected var isAmoledBlackEnabled = false
            private set;

    abstract val presenter: BaseContract.Presenter;

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

        //Initialize UI
        if (isAmoledBlackEnabled)
            this.setTheme(R.style.GesahuThemeAmoled)
        else
            this.setTheme(R.style.GesahuTheme)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setupDrawerLayout()

        headerUsername = navigationView.getHeaderView(0).findViewById(R.id.headerUsername) as TextView
        navigationView.setNavigationItemSelectedListener {
            presenter.onNavigationDrawerItemClicked(it.itemId);
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

    override fun setBoards(boards: List<Board>) {
        val menu = navigationView.menu
        for(i in 0..boards.size-1) {
            menu.add(R.id.boardsSubheader, i + 13, Menu.NONE, boards[i].name)
        }
    }

    override fun setAvatar(avatar: Bitmap) {
        val imageView = navigationView.getHeaderView(0).findViewById(R.id.avatarView) as CircleImageView;
        imageView.setImageBitmap(avatar)
    }


}