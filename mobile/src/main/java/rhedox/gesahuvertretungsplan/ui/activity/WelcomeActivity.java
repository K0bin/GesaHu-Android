package rhedox.gesahuvertretungsplan.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;

public class WelcomeActivity extends AppIntro {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		super.onCreate(savedInstanceState);

		addSlide(AppIntroFragment.newInstance(getString(R.string.feature_design), getString(R.string.feature_design_description), R.drawable.ic_phone, ContextCompat.getColor(this, R.color.intro_slide_1)));
		addSlide(AppIntroFragment.newInstance(getString(R.string.feature_notify), getString(R.string.feature_notify_description), R.drawable.ic_bell, ContextCompat.getColor(this, R.color.intro_slide_2)));
		addSlide(AppIntroFragment.newInstance(getString(R.string.feature_calendar), getString(R.string.feature_calendar_description), R.drawable.ic_calendar, ContextCompat.getColor(this, R.color.intro_slide_3)));
		addSlide(AppIntroFragment.newInstance(getString(R.string.feature_boards), getString(R.string.feature_boards_description), R.drawable.ic_board, ContextCompat.getColor(this, R.color.intro_slide_4)));
		askForPermissions(new String[] { Manifest.permission.WRITE_CALENDAR }, 3);

		this.showSkipButton(false);

		setBarColor(Color.parseColor("#1c000000"));
		setSeparatorColor(Color.parseColor("#1c000000"));
		setDoneText(getText(R.string.login));

		// Check if the version of Android is Lollipop or higher
		if (Build.VERSION.SDK_INT >= 21) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	@Override
	public void onDonePressed(Fragment currentFragment) {
		super.onDonePressed(currentFragment);

		finish();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(PreferenceFragment.PREF_PREVIOUSLY_STARTED, true);
		editor.apply();

		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}
}
