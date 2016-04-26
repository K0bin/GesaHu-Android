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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rhedox.gesahuvertretungsplan.BuildConfig;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;

public class PreferenceActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Theming
        this.setTheme(R.style.GesahuTheme);
        setContentView(R.layout.activity_settings);

        unbinder = ButterKnife.bind(this);

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

        if(BuildConfig.DEBUG)
            System.gc();

        unbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }
}
