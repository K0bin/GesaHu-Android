package rhedox.gesahuvertretungsplan.ui.activity;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.ui.Theming;
import rhedox.gesahuvertretungsplan.ui.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean darkTheme = prefs.getBoolean("pref_dark", false);
        int color = prefs.getInt(SettingsFragment.PREF_COLOR, getResources().getColor(R.color.colorDefaultAccent));

        //Theming
        this.setTheme(Theming.getTheme(darkTheme, color));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionToolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the activity_main content.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent TargetActivity in AndroidManifest.xml.

        if(item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
