package rhedox.gesahuvertretungsplan.ui.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;

import net.danlew.android.joda.JodaTimeAndroid;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Retrofit;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.GesaHuApi;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;

/**
 * Created by robin on 28.09.2016.
 */

public abstract class BaseActivity extends AppCompatActivity {
	private static boolean isInitialized = false;
	private FirebaseAnalytics analytics;
	private ActionBarDrawerToggle toggle;

	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.drawer) DrawerLayout drawerLayout;
	@BindView(R.id.navigation_view) NavigationView navigationView;
	private Unbinder unbinder;

	private GesaHuApi gesaHui;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences prefs = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
		if(!isInitialized) {
			JodaTimeAndroid.init(getApplication());

			PreferenceFragment.applyDarkTheme(prefs);
		}
		super.onCreate(savedInstanceState);

		boolean isAmoledBlackEnabled = prefs.getBoolean(PreferenceFragment.PREF_AMOLED, false);

		analytics = FirebaseAnalytics.getInstance(this);

		//Initialize UI
		if(isAmoledBlackEnabled)
			this.setTheme(R.style.GesahuThemeAmoled);
		else
			this.setTheme(R.style.GesahuTheme);
	}

	@Override
	public void setContentView(@LayoutRes int layoutResID) {
		super.setContentView(layoutResID);
		unbinder = ButterKnife.bind(this);
		setupDrawerLayout();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(unbinder != null)
			unbinder.unbind();
	}

	protected FirebaseAnalytics getAnalytics() {
		return analytics;
	}

	protected void setupDrawerLayout() {
		setSupportActionBar(toolbar);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
		drawerLayout.addDrawerListener(toggle);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(0);
		}

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://gesahui.de")
				//.addConverterFactory(MoshiConverterFactory.create())
				.build();

		gesaHui = retrofit.create(GesaHuApi.class);


		//navigationView.getMenu().findItem(R.id.boards);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		toggle.syncState();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (toggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onBackPressed() {
		if(drawerLayout.isDrawerOpen(GravityCompat.START))
			drawerLayout.closeDrawer(GravityCompat.START);

		super.onBackPressed();
	}
}
