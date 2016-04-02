package rhedox.gesahuvertretungsplan.ui.fragment;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.leakcanary.RefWatcher;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rhedox.gesahuvertretungsplan.*;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.ShortNameResolver;
import rhedox.gesahuvertretungsplan.model.Student;
import rhedox.gesahuvertretungsplan.net.GesahuiApi;
import rhedox.gesahuvertretungsplan.net.NetworkChecker;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.net.SubstitutesListConverterFactory;
import rhedox.gesahuvertretungsplan.ui.DividerItemDecoration;
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesAdapter;
import rhedox.gesahuvertretungsplan.ui.widget.SwipeRefreshLayoutFix;
import tr.xip.errorview.ErrorView;

/**
 * Created by Robin on 30.06.2015.
 */
public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Callback<SubstitutesList>, ErrorView.RetryListener {
    private SubstitutesAdapter adapter;
    @Bind(R.id.swipe) SwipeRefreshLayoutFix refreshLayout;
    @Bind(R.id.recycler) RecyclerView recyclerView;

    private boolean filterImportant = false;
    private boolean sortImportant = false;

    public static final String ARGUMENT_DATE = "ARGUMENT_DATE";
    public static final String TAG ="MAIN_FRAGMENT";

    private LocalDate date;
    @Nullable private SubstitutesList substitutesList;
    private boolean isLoading = false;
    private GesahuiApi gesahui;
    private retrofit2.Call<SubstitutesList> call;

    @Bind(R.id.error_view) ErrorView errorView;
    @Bind(R.id.error_view_scroll) NestedScrollView errorViewScroll;
    @Bind(R.id.error_view_swipe) SwipeRefreshLayoutFix errorViewRefresh;
    @BindDrawable(R.drawable.error_view_cloud) Drawable errorImage;
    @BindDrawable(R.drawable.no_substitutes) Drawable noneImage;

    private MaterialActivity activity;

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

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if(BuildConfig.DEBUG)
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));

        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gesahui.de")
                .addConverterFactory(new SubstitutesListConverterFactory(new ShortNameResolver(getActivity(), specialMode), student))
                .client(client)
                .build();

        gesahui = retrofit.create(GesahuiApi.class);

        load(date);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //RefreshLayout color scheme
        TypedArray typedArray = getActivity().getTheme().obtainStyledAttributes(new int[]{R.attr.colorAccent, R.attr.about_libraries_card});
        int accentColor = typedArray.getColor(0, 0xff000000);
        int cardColor = typedArray.getColor(1, 0xff000000);
        typedArray.recycle();

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        //RefreshLayout
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(accentColor);
        refreshLayout.setProgressBackgroundColorSchemeColor(cardColor);

        //RecyclerView
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(manager);

        //RecyclerView Adapter
        adapter = new SubstitutesAdapter(this.getActivity());
        if(substitutesList != null) {
            int count = 0;
            if(substitutesList.hasSubstitutes())
                count = adapter.setList(substitutesList.getSubstitutes(), filterImportant, sortImportant);
            if(count <= 0)
                showError(getString(R.string.no_substitutes_hint), getString(R.string.no_substitutes), noneImage);
        } else
            onRefresh();
        recyclerView.setAdapter(adapter);

        //RefreshLayout immediate refresh bug workaround
        if(isLoading)
            refreshLayout.setRefreshing(true);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getActivity());
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        errorView.setVisibility(View.GONE);
        errorView.setOnRetryListener(this);

        errorViewScroll.setVisibility(View.GONE);
        errorViewRefresh.setOnRefreshListener(this);
        errorViewRefresh.setVisibility(View.GONE);
        errorViewRefresh.setColorSchemeColors(accentColor);
        errorViewRefresh.setProgressBackgroundColorSchemeColor(cardColor);

        if(getActivity() instanceof MaterialActivity)
            activity = (MaterialActivity) getActivity();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(isLoading && refreshLayout != null && refreshLayout.isEnabled() && refreshLayout.getVisibility() == View.VISIBLE)
            refreshLayout.setRefreshing(true);
        else if(substitutesList == null)
            onRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {

        ButterKnife.unbind(this);

        //Remove all view references to prevent leaking
        refreshLayout = null;
        recyclerView = null;
        adapter = null;
        errorView = null;
        activity = null;
        errorImage = null;
        noneImage = null;
        errorViewScroll = null;
        errorView = null;
        errorViewRefresh = null;
        errorImage = null;
        noneImage = null;

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
    public void onRefresh() {
        load(date);
    }

    public void load(LocalDate date) {
        if(date != null && !isLoading) {

            if(NetworkChecker.isNetworkConnected(getActivity())) {
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

    public SubstitutesList getSubstitutesList() {
        return substitutesList;
    }

    public void setSwipeToRefreshEnabled(boolean isEnabled) {
        if(refreshLayout != null) {
            if(!isEnabled)
                refreshLayout.setRefreshing(false);

            refreshLayout.setEnabled(isEnabled);
        }
    }
    @Override
    public void onResponse(retrofit2.Call<SubstitutesList> call, Response<SubstitutesList> response) {
        isLoading = false;

        if(getActivity() == null)
            return;

        if(!response.isSuccessful()) {
            onFailure(call, new NullPointerException());
            return;
        }

        substitutesList = response.body();

        int count = 0;
        if (substitutesList != null && adapter != null) {
            count = adapter.setList(substitutesList.getSubstitutes(), filterImportant, sortImportant);
        }

        if(count > 0)
            hideError();
        else
            showError(getString(R.string.no_substitutes_hint), getString(R.string.no_substitutes), noneImage);

        //Update the floating action button
        if (activity != null && getUserVisibleHint())
            activity.setFabVisibility(substitutesList != null && substitutesList.hasAnnouncement());
    }

    @Override
    public void onFailure(retrofit2.Call<SubstitutesList> call, Throwable t) {

        isLoading = false;
        Log.e("net-error", "Message: "+t.getMessage());

        if(getActivity() == null)
            return;

        if(substitutesList == null) {
            showError(getString(R.string.error), getString(R.string.oops), errorImage);

            if (activity != null) {
                //Update the floating action button
                if (getUserVisibleHint())
                    activity.setFabVisibility(false);
                 else if (activity.getCoordinatorLayout() != null)
                    //Fragment is not empty, keep previous entries and show snackbar
                    Snackbar.make(activity.getCoordinatorLayout(), getString(R.string.oops), Snackbar.LENGTH_LONG);
            }
        }
        else {
            hideError();

            if(activity.getCoordinatorLayout() != null)
                //Fragment is not empty, keep previous entries and show snackbar
                Snackbar.make(activity.getCoordinatorLayout(), getString(R.string.oops), Snackbar.LENGTH_LONG);
        }
    }

    //Hide the error ui
    private void hideError() {
        clearRefreshViews();

        if(errorViewScroll != null) {
            errorViewScroll.setVisibility(View.GONE);
            errorViewScroll.setEnabled(false);
        }
        if(errorViewRefresh != null) {
            errorViewRefresh.setRefreshing(false);
            errorViewRefresh.clearAnimation();
            errorViewRefresh.setVisibility(View.GONE);
            errorViewRefresh.setEnabled(false);
        }
        if(errorView != null) {
            errorView.setVisibility(View.GONE);
            errorView.setEnabled(false);
        }
        if(recyclerView != null) {
            recyclerView.setEnabled(true);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
    //Show the error ui
    private void showError(String errorMessage, String title, Drawable image) {
        clearRefreshViews();

        if(recyclerView != null) {
            recyclerView.setEnabled(false);
            recyclerView.setVisibility(View.GONE);
        }
        if(activity != null)
            activity.setAppBarExpanded(true);

        if(errorViewScroll != null) {
            errorViewScroll.setEnabled(true);
            errorViewScroll.setVisibility(View.VISIBLE);
        }
        if(errorViewRefresh != null) {
            errorViewRefresh.setRefreshing(false);
            errorViewRefresh.clearAnimation();
            errorViewRefresh.setEnabled(true);
            errorViewRefresh.setVisibility(View.VISIBLE);
        }

        if(errorView != null) {
            errorView.setEnabled(true);
            errorView.setVisibility(View.VISIBLE);
            errorView.setSubtitle(errorMessage);
            errorView.setTitle(title);
            errorView.setImage(image);
        }
    }

    private void clearRefreshViews() {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
            refreshLayout.clearAnimation();
        }
        if (errorViewRefresh != null) {
            errorViewRefresh.setRefreshing(false);
            errorViewRefresh.clearAnimation();
        }
    }

    @Override
    public void onRetry() {
        hideError();
        onRefresh();
    }

    public SubstitutesAdapter getAdapter() {
        return adapter;
    }

    public static MainFragment newInstance(LocalDate date) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARGUMENT_DATE, date.toDateTimeAtCurrentTime().getMillis());
        MainFragment fragment = new MainFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    public interface MaterialActivity {
        FloatingActionButton getFloatingActionButton();
        void setFabVisibility(boolean isVisible);
        AppBarLayout getAppBarLayout();
        CoordinatorLayout getCoordinatorLayout();
        void setAppBarExpanded(boolean isExpanded);
        void setCabVisibility(boolean isVisible);
    }
}
