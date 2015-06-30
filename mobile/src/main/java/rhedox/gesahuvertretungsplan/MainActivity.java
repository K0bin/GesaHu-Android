package rhedox.gesahuvertretungsplan;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.List;

import rhedox.gesahuvertretungsplan.RecyclerView.*;

public class MainActivity extends AppCompatActivity {
    public final String PREF_YEAR ="pref_year";
    public final String PREF_CLASS ="pref_class";
    public final String PREF_DARK ="pref_dark";

    private MainFragment fragment;
    private boolean darkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        darkTheme = prefs.getBoolean(PREF_DARK, false);
        StudentInformation studentInformation = new StudentInformation(prefs.getString(PREF_YEAR,"5"), prefs.getString(PREF_CLASS, "a"));

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

        fragment = (MainFragment)getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
        if(fragment == null) {
            fragment = MainFragment.newInstance(studentInformation);
            getSupportFragmentManager().beginTransaction().add(R.id.coordinator, fragment, MainFragment.TAG).commit();
        }
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
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            }
                break;

            case R.id.action_load: {
                if (fragment != null)
                    fragment.showPicker();
            }
                break;

            case R.id.action_about: {

                new LibsBuilder()
                        .withFields(R.string.class.getFields())
                        .withAboutAppName(getResources().getString(R.string.app_name))
                        .withActivityTitle("Über")
                        .withAboutIconShown(true)
                        .withAboutVersionShown(true)
                        .withAboutDescription("Zeigt den <b>Gesahu Vertretungsplan</b> in einem für Smartphones optimierten Layout an.<br>Entwickelt von Robin Kertels<br>Feedback von Felix Bastian")
                        .withVersionShown(true)
                        .withActivityStyle(darkTheme ? Libs.ActivityStyle.DARK : Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        .withLibraries("AppCompat","MaterialDesignIcons")
                        .start(this);
            }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class MainFragment extends Fragment implements OnDownloadedListener, SwipeRefreshLayout.OnRefreshListener, DatePickerDialog.OnDateSetListener {
        private ReplacementsList plan = new ReplacementsList();
        private ReplacementsAdapter adapter;
        private SwipeRefreshLayout refreshLayout;

        private DatePickerFragment datePickerDialog;

        private CoordinatorLayout coordinatorLayout;

        private StudentInformation studentInformation;

        public static final String ARGUMENT_STUDENT_INFORMATION = "ARGUMENT_STUDENT_INFORMATION";
        public static final String TAG ="MAIN_FRAGMENT";

        private LocalDate lastDate;

        //Datepicker double workaround
        private boolean picked = false;

        public MainFragment() {}

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //Get Arguments
            Bundle arguments = getArguments();
            if(arguments != null)
                studentInformation = arguments.getParcelable(ARGUMENT_STUDENT_INFORMATION);

            if(studentInformation == null) {
                studentInformation = new StudentInformation("","");
            }

            adapter = new ReplacementsAdapter(this.getActivity());
            load(SchoolWeek.next());

            setRetainInstance(true);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_main, container, false);

            refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe);
            refreshLayout.setOnRefreshListener(this);

            //RecyclerView
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recylcler);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getActivity());
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);

            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL_LIST);
            recyclerView.addItemDecoration(itemDecoration);

            recyclerView.setItemAnimator(new SlideAnimator(recyclerView));

            datePickerDialog = (DatePickerFragment)DatePickerFragment.newInstance();

            coordinatorLayout = (CoordinatorLayout)getActivity().findViewById(R.id.coordinator);

            return view;
        }

        @Override
        public void onDownloaded(List<Replacement> replacements) {
            if(adapter != null)
                adapter.addAll(replacements);
            else {
                adapter = new ReplacementsAdapter(getActivity());
                adapter.setReplacements(replacements);
            }

            if(refreshLayout != null)
                refreshLayout.setRefreshing(false);

            if(getActivity() != null && getActivity() instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity)getActivity();
                if(activity.getSupportActionBar() != null) {
                    String title = lastDate.toString(DateTimeFormat.forPattern("dd.MM.yyyy"));
                    activity.setTitle(title);
                }
            }
        }

        @Override
        public void onDownloadFailed(int error) {
            if(refreshLayout != null)
                refreshLayout.setRefreshing(false);

            String errorMessage = "Fehler";
            if(error == Error.NO_CONNECTION)
                errorMessage = "Keine Internetverbindung";
            else if(error == Error.NO_DATA)
                errorMessage = "Keine Daten empfangen";

            if(coordinatorLayout != null)
            Snackbar.make(coordinatorLayout, errorMessage, Snackbar.LENGTH_LONG).setAction("Erneut versuchen", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFragment.this.onRefresh();
                }
            }).show();
        }


        @Override
        public void onDetach() {
            if(plan != null)
                plan.stop();

            super.onDetach();
        }

        @Override
        public void onStop() {
            if(plan != null)
                plan.stop();

            super.onStop();
        }

        @Override
        public void onRefresh() {
            load(lastDate);
        }

        public void load(LocalDate date) {
            if(!plan.isLoading()) {
                if(refreshLayout != null)
                    refreshLayout.setRefreshing(true);

                if(adapter != null)
                    adapter.removeAll();

                lastDate = date;

                plan.load(getActivity(), date, studentInformation, this);
            }
        }

        public void showPicker() {
            picked = false;
            if(datePickerDialog != null)
                datePickerDialog.show(lastDate, getChildFragmentManager(), "datePicker", this);
        }

        public static MainFragment newInstance(StudentInformation information) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(ARGUMENT_STUDENT_INFORMATION, information);
            MainFragment fragment = new MainFragment();
            fragment.setArguments(arguments);

            return fragment;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if(!picked) {
                LocalDate date = SchoolWeek.next(new LocalDate(year, monthOfYear + 1, dayOfMonth));
                load(date);
            }
            picked = true;
        }
    }
}
