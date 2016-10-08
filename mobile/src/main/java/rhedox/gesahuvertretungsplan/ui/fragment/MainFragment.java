package rhedox.gesahuvertretungsplan.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.leakcanary.RefWatcher;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rhedox.gesahuvertretungsplan.*;
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver;
import rhedox.gesahuvertretungsplan.model.GesaHuiApi;
import rhedox.gesahuvertretungsplan.model.LocalDateDeserializer;
import rhedox.gesahuvertretungsplan.model.QueryDate;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.Student;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.util.NetworkUtils;
import rhedox.gesahuvertretungsplan.ui.DividerItemDecoration;
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
    private GesaHuiApi gesahui;
    private retrofit2.Call<SubstitutesList> call;

    private MaterialActivity activity;

	public static final String STATE_KEY_SUBSTITUTE_LIST = "substitutelist";

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

        //Get Arguments
        Bundle arguments = getArguments();
        if(arguments != null)
            date = new DateTime(arguments.getLong(ARGUMENT_DATE, 0L)).toLocalDate();
        else
            date = SchoolWeek.next();

	    //Init Retrofit
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if(BuildConfig.DEBUG)
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));

	    Gson gson = new GsonBuilder()
			    .registerTypeAdapter(Substitute.class, new Substitute.Deserializer(this.getContext()))
	            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
			    .create();

        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gesahui.de")
		        .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        gesahui = retrofit.create(GesaHuiApi.class);

	    //Load data if user is on WIFI
        ConnectivityManager connMgr;
        if(savedInstanceState == null && getActivity() != null) {
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
	    } else if(savedInstanceState != null)
		    substitutesList = savedInstanceState.getParcelable(STATE_KEY_SUBSTITUTE_LIST);

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
	public void onDetach() {
		super.onDetach();
		activity = null;
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
	    activity = null;
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
            //outState.putParcelable(STATE_KEY_SUBSTITUTE_LIST, substitutesList);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        load(date);
    }

    public void load(LocalDate date) {
        if(date != null && !isLoading) {

            if(NetworkUtils.isNetworkConnected(getActivity().getApplicationContext())) {
                if (refreshLayout != null)
                    refreshLayout.setRefreshing(true);

                //Store date for refreshing
                this.date = date;

                call = gesahui.substitutes(new QueryDate(date));

                call.enqueue(this);

                isLoading = true;
            } else
                onFailure(null, new IOException(getString(R.string.error_no_connection)));
        }
    }

	@Nullable
    public SubstitutesList getSubstitutesList() {
        return substitutesList;
    }

	/**
	 * Enable or disable the SwipeToRefreshLayout
	 * (Intended to be called by the activity depending on the app bars state to prevent them
	 * from blocking each other)
	 * @param isEnabled Whether the SwipeRefreshLayout is enabled or not
	 */
    public void setSwipeToRefreshEnabled(boolean isEnabled) {
        if(refreshLayout != null) {

            refreshLayout.setEnabled(isEnabled || refreshLayout.isRefreshing());
        }
    }

	/**
	 * Display the loaded list
	 */
    private void populateList() {
        if (substitutesList != null && adapter != null) {

            if(sortImportant && filterImportant)
                Log.e("List","Can't both filter and sort at the same time.");

            List<Substitute> substitutes;
	        /*
            if(sortImportant)
                substitutes = Collections.unmodifiableList(SubstitutesList.sort(substitutesList.getSubstitutes()));
            else if (filterImportant)
                substitutes = Collections.unmodifiableList(SubstitutesList.filterImportant(substitutesList.getSubstitutes()));
            else*/
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

	    //FirebaseCrash.log("RetrofitFailure: "+t.getMessage());
	    //FirebaseCrash.report(t);

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
            if (getUserVisibleHint() && snackbar != null && getActivity() != null) {
	            //Fragment is not empty, keep previous entries and show snackbar

	            if (NetworkUtils.isNetworkConnected(getActivity().getApplicationContext()))
		            snackbar.setText(R.string.oops);
	            else
		            snackbar.setText(R.string.error_no_connection);

	            snackbar.show();
            }
        }
    }

	/**
	 * Called when the fragment just became visible on the ViewPager
	 */
    public void onDisplay() {
        if(substitutesList == null)
            onRefresh();
    }

	/**
	 * Prevent the SwipeRefreshLayout from keeping this fragments views on screen even if
	 * they should be destroyed.
	 * Workaround for shitty Android bugs
	 */
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
        /**
         * Update visibility of activity level ui views such as the Snackbar, the floating action button or the contextual action bar
         */
        void updateUI();

        CoordinatorLayout getCoordinatorLayout();
        MainFragment getVisibleFragment();

    }
}
