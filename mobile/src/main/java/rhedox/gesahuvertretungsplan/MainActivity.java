package rhedox.gesahuvertretungsplan;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import java.util.List;

import rhedox.gesahuvertretungsplan.RecyclerView.*;

public class MainActivity extends AppCompatActivity {
    public final String PREF_YEAR ="pref_year";
    public final String PREF_CLASS ="pref_class";
    public final String PREF_DARK ="pref_dark";

    MainFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkTheme = prefs.getBoolean(PREF_DARK, false);
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
            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment, MainFragment.TAG).commit();
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
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.action_load:
                if(fragment != null)
                    fragment.showPicker();
                break;

            case R.id.action_about:
                String credits = "Vertretungsplanx3" + System.getProperty("line.separator") + "Entwickelt von Robin Kertels" + System.getProperty("line.separator") + "Viel Feedback von Felix Bastian" + System.getProperty("line.separator") + "Wollen unbedingt erwähnt werden: Jonas Dietz, Robin Möbus und Heidi Meyer";
                Toast.makeText(this, credits, Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class MainFragment extends Fragment implements OnDownloadedListener, SwipeRefreshLayout.OnRefreshListener {
        private Date currentDate;
        private ReplacementsList plan = new ReplacementsList();
        private ReplacementsAdapter adapter;
        private SwipeRefreshLayout refreshLayout;
        private boolean loading = false;
        private DatePickerFragment datePickerDialog;

        private StudentInformation studentInformation;

        public static final String ARGUMENT_STUDENT_INFORMATION = "ARGUMENT_STUDENT_INFORMATION";
        public static final String TAG ="MAIN_FRAGMENT";

        public MainFragment() {}

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            adapter = new ReplacementsAdapter(this.getActivity());

            setRetainInstance(true);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            //Get Arguments
            Bundle arguments = getArguments();
            if(arguments != null)
                studentInformation = arguments.getParcelable(ARGUMENT_STUDENT_INFORMATION);

            if(studentInformation == null) {
                studentInformation = new StudentInformation("","");
            }

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

            if(adapter.getItemCount() == 0)
                loadToday();

            return view;
        }

        @Override
        public void onDownloaded(Context context, List<Replacement> replacements) {
            adapter.addAll(replacements);

            refreshLayout.setRefreshing(false);
            loading = false;

            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(currentDate.toString());
        }


        @Override
        public void onDetach() {
            plan.stop();

            super.onDetach();
        }

        @Override
        public void onStop() {
            plan.stop();

            super.onStop();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onRefresh() {
            load(currentDate);
        }

        public void load(Date date) {
            if(isNetworkConnected())
            {
                if(!loading) {
                    refreshLayout.setRefreshing(true);
                    loading = true;
                    adapter.removeAll();

                    this.currentDate = date;

                    plan.load(this.getActivity(), currentDate, studentInformation, this);
                }
            } else {
                refreshLayout.setRefreshing(false);
                loading = false;

                String noConnection = "Keine Internetverbindung!";
                Toast.makeText(this.getActivity(), noConnection, Toast.LENGTH_SHORT).show();
            }
        }
        public void loadToday() {
            load(SchoolWeek.next());
        }

        // Check network connection
        private boolean isNetworkConnected(){
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        public void showPicker() {
            if(datePickerDialog != null)
                datePickerDialog.show(currentDate, getChildFragmentManager(), "datePicker");
        }

        public static MainFragment newInstance(StudentInformation information) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(ARGUMENT_STUDENT_INFORMATION, information);
            MainFragment fragment = new MainFragment();
            fragment.setArguments(arguments);

            return fragment;
        }
    }
}
