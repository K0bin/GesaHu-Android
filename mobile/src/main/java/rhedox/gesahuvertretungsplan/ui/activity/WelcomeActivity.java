package rhedox.gesahuvertretungsplan.ui.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Debug;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.squareup.leakcanary.RefWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rhedox.gesahuvertretungsplan.App;
import rhedox.gesahuvertretungsplan.BuildConfig;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;
import rhedox.gesahuvertretungsplan.ui.fragment.WelcomePreferenceFragment;

public class WelcomeActivity extends AppIntro {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		super.onCreate(savedInstanceState);

		addSlide(AppIntroFragment.newInstance(getString(R.string.feature_design), getString(R.string.feature_design_description), R.drawable.ic_view_list, ContextCompat.getColor(this, R.color.intro_slide_1)));
		addSlide(AppIntroFragment.newInstance(getString(R.string.feature_highlight), getString(R.string.feature_highlight_description), R.drawable.ic_invert_colors, ContextCompat.getColor(this, R.color.intro_slide_2)));
		addSlide(AppIntroFragment.newInstance(getString(R.string.feature_notify), getString(R.string.feature_notify_description), R.drawable.ic_bell, ContextCompat.getColor(this, R.color.intro_slide_3)));
		addSlide(WelcomePreferenceFragment.newInstance());

		setBarColor(Color.parseColor("#1c000000"));
		setSeparatorColor(Color.parseColor("#1c000000"));

		// Check if the version of Android is Lollipop or higher
		if (Build.VERSION.SDK_INT >= 21) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	@Override
	public void onDonePressed(Fragment currentFragment) {
		super.onDonePressed(currentFragment);

		loadMainActivity();
	}

	private void loadMainActivity() {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(PreferenceFragment.PREF_PREVIOUSLY_STARTED, true);
		editor.apply();

		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);

	}
}
