package rhedox.gesahuvertretungsplan.ui.activity;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;

public class SettingsActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Theming
        this.setTheme(R.style.GesahuTheme);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the activity_main content.
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
    protected void onDestroy() {
        super.onDestroy();

        ButterKnife.unbind(this);
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
