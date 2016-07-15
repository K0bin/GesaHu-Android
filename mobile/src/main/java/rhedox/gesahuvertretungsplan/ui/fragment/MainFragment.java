package rhedox.gesahuvertretungsplan.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.crash.FirebaseCrash;
import com.squareup.leakcanary.RefWatcher;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rhedox.gesahuvertretungsplan.*;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.ShortNameResolver;
import rhedox.gesahuvertretungsplan.model.Student;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.net.GesahuiApi;
import rhedox.gesahuvertretungsplan.net.NetworkChecker;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.net.SubstitutesListConverterFactory;
import rhedox.gesahuvertretungsplan.ui.DividerItemDecoration;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesAdapter;
import rhedox.gesahuvertretungsplan.ui.widget.SwipeRefreshLayoutFix;
import tr.xip.errorview.ErrorView;

/**
 * Created by Robin on 30.06.2015.
 */
public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Callback<SubstitutesList>, ErrorView.RetryListener {
    private SubstitutesAdapter adapter;
    @BindView(R.id.swipe) SwipeRefreshLayoutFix refreshLayout;
    @BindView(R.id.recycler) RecyclerView recyclerView;
    private Snackbar snackbar;
    private Unbinder unbinder;

    private boolean filterImportant = false;
    private boolean sortImportant = false;

    public static final String ARGUMENT_DATE = "ARGUMENT_DATE";
    public static final String TAG ="MAIN_FRAGMENT";

    private LocalDate date;
    @Nullable private SubstitutesList substitutesList;
    private boolean isLoading = false;
    private GesahuiApi gesahui;
    private retrofit2.Call<SubstitutesList> call;

    private MaterialActivity activity;

	public static final String STATE_KEY_SUBSTITUTE_LIST = "substitutelsit";

    public MainFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        //Get Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String infoClass = prefs.getString(PreferenceFragment.PREF_CLASS, "");
        String infoYear = prefs.getString(PreferenceFragment.PREF_YEAR, "");
        Student student = new Student(infoYear, infoClass);
        filterImportant = prefs.getBoolean(PreferenceFragment.PREF_FILTER, false);
        sortImportant = prefs.getBoolean(PreferenceFragment.PREF_SORT, false);
        boolean specialMode = prefs.getBoolean(PreferenceFragment.PREF_SPECIAL_MODE, false);

        //Get Arguments
        Bundle arguments = getArguments();
        if(arguments != null)
            date = new DateTime(arguments.getLong(ARGUMENT_DATE,0l)).toLocalDate();
        else
            date = SchoolWeek.next();

	    //Init Retrofit
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if(BuildConfig.DEBUG)
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));

        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gesahui.de")
                .addConverterFactory(new SubstitutesListConverterFactory(new ShortNameResolver(getActivity().getApplicationContext(), specialMode), student))
                .client(client)
                .build();

        gesahui = retrofit.create(GesahuiApi.class);

	    //Load data if user is on WIFI
        ConnectivityManager connMgr;
        if(getActivity() != null) {
            connMgr = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            if(!ConnectivityManagerCompat.isActiveNetworkMetered(connMgr))
                load(date);
        }
    }

    @Nullable
    @Override
    @SuppressWarnings("ResourceType")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	    if(getActivity() instanceof MaterialActivity)
		    activity = (MaterialActivity) getActivity();

	    //RefreshLayout color scheme
	    TypedArray typedArray = getActivity().getTheme().obtainStyledAttributes(new int[]{R.attr.colorAccent, R.attr.about_libraries_card});
	    int accentColor = typedArray.getColor(0, 0xff000000);
	    int cardColor = typedArray.getColor(1, 0xff000000);
	    typedArray.recycle();

	    View view = inflater.inflate(R.layout.fragment_main, container, false);
	    unbinder = ButterKnife.bind(this, view);

	    //RefreshLayout
	    refreshLayout.setOnRefreshListener(this);
	    refreshLayout.setColorSchemeColors(accentColor);
	    refreshLayout.setProgressBackgroundColorSchemeColor(cardColor);

	    //RecyclerView
	    recyclerView.setHasFixedSize(true);
	    RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getActivity());
	    recyclerView.setLayoutManager(manager);
	    RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getActivity());
	    recyclerView.addItemDecoration(itemDecoration);
	    recyclerView.setItemAnimator(new DefaultItemAnimator());

	    //RecyclerView Adapter
	    //Try to display retained data on newly created view
	    adapter = new SubstitutesAdapter(this.getActivity());
	    if(substitutesList != null) {

		    if(activity != null)
			    activity.updateUI();

		    populateList();
	    } else if(savedInstanceState != null) {
		    substitutesList = savedInstanceState.getParcelable(STATE_KEY_SUBSTITUTE_LIST);
	    }

	    recyclerView.setAdapter(adapter);

	    //Show the refresh indicator if the view got recreated while it's still loading
	    if(isLoading)
		    refreshLayout.setRefreshing(true);

	    //Create the snackbar that's used to display errors when there's still items on screen
	    snackbar = Snackbar.make(activity.getCoordinatorLayout(), getString(R.string.oops), Snackbar.LENGTH_LONG).setAction(R.string.retry, new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    onRefresh();
		    }
	    });

	    return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(isLoading && refreshLayout != null && refreshLayout.isEnabled())
            refreshLayout.setRefreshing(true);
        else if(substitutesList == null && getUserVisibleHint())
            onRefresh();
    }

    @Override
    public void onPause() {

        clearRefreshViews();

        super.onPause();
    }

    @Override
    public void onDestroyView() {

        unbinder.unbind();

        clearRefreshViews();

        //Remove all view references to prevent leaking
        if (snackbar != null && snackbar.isShown())
            snackbar.dismiss();
        snackbar = null;

        refreshLayout = null;
        recyclerView = null;
        adapter = null;
        //activity = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if(call != null) {
            call.cancel();
            call = null;
        }

        //LeakCanary
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        if(refWatcher != null)
            refWatcher.watch(this);

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(substitutesList != null) {
            outState.putParcelable(STATE_KEY_SUBSTITUTE_LIST, substitutesList);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        load(date);
    }

    public void load(LocalDate date) {
        if(date != null && !isLoading) {

            if(NetworkChecker.isNetworkConnected(getActivity().getApplicationContext())) {
                if (refreshLayout != null)
                    refreshLayout.setRefreshing(true);

                //Store date for refreshing
                this.date = date;

                call = gesahui.getSubstitutesList(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());

                call.enqueue(this);

                isLoading = true;
            } else
                onFailure(null, new Exception(getString(R.string.error_no_connection)));
        }
    }

	@Nullable
    public SubstitutesList getSubstitutesList() {
        return substitutesList;
    }

	//Activity will call this depending on the AppBars state to prevent them from blocking each other
    public void setSwipeToRefreshEnabled(boolean isEnabled) {
        if(refreshLayout != null) {

            refreshLayout.setEnabled(isEnabled || refreshLayout.isRefreshing());
        }
    }

	//Display the loaded list
    private void populateList() {
        if (substitutesList != null && adapter != null) {

            if(sortImportant && filterImportant)
                Log.e("List","Can't both filter and sort at the same time.");

            List<Substitute> substitutes;
            if(sortImportant)
                substitutes = Collections.unmodifiableList(SubstitutesList.sort(substitutesList.getSubstitutes()));
            else if (filterImportant)
                substitutes = Collections.unmodifiableList(SubstitutesList.filterImportant(substitutesList.getSubstitutes()));
            else
                substitutes = substitutesList.getSubstitutes();

            adapter.showList(substitutes);
            recyclerView.scrollToPosition(0);
        }
    }


    @Override
    public void onResponse(retrofit2.Call<SubstitutesList> call, Response<SubstitutesList> response) {
        isLoading = false;

        clearRefreshViews();

        if(!response.isSuccessful()) {
            onFailure(call, new Exception(response.errorBody().toString()));
            return;
        }

        substitutesList = response.body();
        populateList();

        //Update the floating action button
        if(activity != null)
            activity.updateUI();
    }

    @Override
    public void onFailure(retrofit2.Call<SubstitutesList> call, Throwable t) {

        isLoading = false;
        Log.e("net-error", "Message: " + t.getMessage());

	    FirebaseCrash.log("RetrofitFailure: "+t.getMessage());
	    FirebaseCrash.report(t);

	    /*PROGUARD DEBUGGING
	    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(t.getMessage());
        builder.setTitle(t.toString());
        builder.create().show();*/

        clearRefreshViews();


        if(activity != null)
            activity.updateUI();

        if (substitutesList == null) {
            if (adapter != null)
                adapter.showError();
        } else {
            if (getUserVisibleHint())
                //Fragment is not empty, keep previous entries and show snackbar
                snackbar.show();
        }
    }

	//The fragment just got scrolled to
    public void onDisplay() {
        if(substitutesList == null)
            onRefresh();
    }

	//Prevent the SwipeRefreshLayout from keeping this fragments views on screen even if they should be destroyed.
	//shitty Android bugs
    private void clearRefreshViews() {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
            refreshLayout.clearAnimation();
        }
    }

    @Override
    public void onRetry() {
        onRefresh();
    }

    public SubstitutesAdapter getAdapter() {
        return adapter;
    }

    public LocalDate getDate() { return date; }

    public static MainFragment newInstance(LocalDate date) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARGUMENT_DATE, date.toDateTimeAtCurrentTime().getMillis());
        MainFragment fragment = new MainFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    public interface MaterialActivity {
        void updateUI();

        CoordinatorLayout getCoordinatorLayout();
        MainFragment getVisibleFragment();

    }
}
