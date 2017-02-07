package rhedox.gesahuvertretungsplan.ui.activity;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import rhedox.gesahuvertretungsplan.BuildConfig;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;

public class PreferenceActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isAmoledBlackEnabled = prefs.getBoolean(PreferenceFragment.PREF_AMOLED, false);

        //Theming
	    if(isAmoledBlackEnabled)
		    this.setTheme(R.style.GesahuThemeAmoled);
	    else
            this.setTheme(R.style.GesahuTheme);

        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the fragment_substitutes content.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PreferenceFragment())
                .commit();


        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            TypedArray a = obtainStyledAttributes(new int[] { R.attr.colorPrimary });
            int primaryColor = a.getColor(0, 0);
            a.recycle();

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_task);
            ActivityManager.TaskDescription description = new ActivityManager.TaskDescription(getString(R.string.app_name), bitmap, primaryColor);
            this.setTaskDescription(description);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(super.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(BuildConfig.DEBUG)
            System.gc();
    }
}
