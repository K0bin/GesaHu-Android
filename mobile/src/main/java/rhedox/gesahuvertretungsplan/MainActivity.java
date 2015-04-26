package rhedox.gesahuvertretungsplan;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.widget.Toast;

import java.util.GregorianCalendar;
import java.util.List;

import rhedox.gesahuvertretungsplan.RecyclerView.*;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, OnDownloadedListener  {
    private int day=1;
    private int month=1;
    private int year=2014;
    private ReplacementsList plan = new ReplacementsList();
    private ReplacementsAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private boolean loading = false;

    //Preferences
    private boolean darkTheme;
    private String schoolYear, schoolClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        darkTheme = prefs.getBoolean("pref_dark", false);
        String time = prefs.getString("pref_notification_time", "00:00");
        schoolClass = prefs.getString("pref_class", "a");
        schoolYear = prefs.getString("pref_year","5");

        //Theming
        if(darkTheme) {
            this.setTheme(R.style.GesahuThemeDark);
        } else {
            this.setTheme(R.style.GesahuTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionToolBar);
        setSupportActionBar(toolbar);

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(this);


        //RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recylcler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new ReplacementsAdapter(this);
        recyclerView.setAdapter(adapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setItemAnimator(new SlideAnimator(recyclerView));

        loadToday();
    }

    @Override
    public void onRefresh() {
        load(day, month, year);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent TargetActivity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.action_load:
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.Picked=false;
                fragment.Day = day;
                fragment.Month = month;
                fragment.Year = year;
                fragment.show(getFragmentManager(), "datePicker");
                break;

            case R.id.action_about:
                String credits = "Vertretungsplanx3" + System.getProperty("line.separator") + "Entwickelt von Robin Kertels" + System.getProperty("line.separator") + "Viel Feedback von Felix Bastian" + System.getProperty("line.separator") + "Wollen unbedingt erwähnt werden: Jonas Dietz, Robin Möbus und Heidi Meyer";
                Toast.makeText(this, credits, Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void load(int day, int month, int year) {
        if(isNetworkConnected())
        {
            if(!loading) {
                refreshLayout.setRefreshing(true);
                loading = true;
                adapter.removeAll();

                this.day = day;
                this.month = month;
                this.year = year;

                plan.load(this, day, month, year, schoolClass, schoolYear, this);
            }
        } else {
            refreshLayout.setRefreshing(false);
            loading = false;

            String noConnection = "Keine Internetverbindung!";
            Toast.makeText(this, noConnection, Toast.LENGTH_SHORT).show();
        }
    }
    public void loadToday() {
        GregorianCalendar calendar = SchoolWeek.next();
        int todayDay = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        int todayMonth = calendar.get(GregorianCalendar.MONTH) + 1;
        int todayYear = calendar.get(GregorianCalendar.YEAR);

        load(todayDay, todayMonth, todayYear);
    }

    // Check network connection
    private boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDownloaded(Context context,  List<Replacement> replacements) {
        getSupportActionBar().setTitle(SchoolWeek.getDateString(day, month, year));

        adapter.addAll(replacements);

        refreshLayout.setRefreshing(false);
        loading = false;

    }
}
