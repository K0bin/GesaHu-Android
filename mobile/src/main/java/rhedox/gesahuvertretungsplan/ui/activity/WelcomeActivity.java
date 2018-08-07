package rhedox.gesahuvertretungsplan.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;

public class WelcomeActivity extends AppIntro {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		super.onCreate(savedInstanceState);

		SliderPage page1 = new SliderPage();
		page1.setTitle(getString(R.string.feature_design));
		page1.setDescription(getString(R.string.feature_design_description));
		page1.setImageDrawable(R.drawable.ic_phone);
		page1.setBgColor(ContextCompat.getColor(this, R.color.intro_slide_1));
		addSlide(AppIntroFragment.newInstance(page1));

		SliderPage page2 = new SliderPage();
		page2.setTitle(getString(R.string.feature_notify));
		page2.setDescription(getString(R.string.feature_notify_description));
		page2.setImageDrawable(R.drawable.ic_bell);
		page2.setBgColor(ContextCompat.getColor(this, R.color.intro_slide_2));
		addSlide(AppIntroFragment.newInstance(page2));

		SliderPage page3 = new SliderPage();
		page3.setTitle(getString(R.string.feature_calendar));
		page3.setDescription(getString(R.string.feature_calendar_description));
		page3.setImageDrawable(R.drawable.ic_calendar);
		page3.setBgColor(ContextCompat.getColor(this, R.color.intro_slide_3));
		addSlide(AppIntroFragment.newInstance(page3));

		SliderPage page4 = new SliderPage();
		page4.setTitle(getString(R.string.feature_boards));
		page4.setDescription(getString(R.string.feature_boards_description));
		page4.setImageDrawable(R.drawable.ic_board);
		page4.setBgColor(ContextCompat.getColor(this, R.color.intro_slide_4));
		addSlide(AppIntroFragment.newInstance(page4));

		askForPermissions(new String[] { Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR }, 3);

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

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(PreferenceFragment.PREF_PREVIOUSLY_STARTED, true);
		editor.apply();

		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}
}
