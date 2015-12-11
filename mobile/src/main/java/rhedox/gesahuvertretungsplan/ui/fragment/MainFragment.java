package rhedox.gesahuvertretungsplan.ui.fragment;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.squareup.leakcanary.RefWatcher;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import rhedox.gesahuvertretungsplan.*;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.net.SubstituteJSoupRequest;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.net.VolleySingleton;
import rhedox.gesahuvertretungsplan.ui.DividerItemDecoration;
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesAdapter;
import rhedox.gesahuvertretungsplan.ui.widget.SwipeRefreshLayoutFix;
import rhedox.gesahuvertretungsplan.util.TextUtils;
import tr.xip.errorview.ErrorView;

/**
 * Created by Robin on 30.06.2015.
 */
public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Response.Listener<SubstitutesList>, Response.ErrorListener, ErrorView.RetryListener {
    private SubstitutesAdapter adapter;
    @Bind(R.id.swipe) SwipeRefreshLayoutFix refreshLayout;
    @Bind(R.id.recylcler) RecyclerView recyclerView;

    private StudentInformation studentInformation;
    private boolean filterImportant = false;

    public static final String ARGUMENT_STUDENT_INFORMATION = "ARGUMENT_STUDENT_INFORMATION";
    public static final String ARGUMENT_DATE = "ARGUMENT_DATE";
    public static final String ARGUMENT_IMPORTANT = "ARGUMENT_IMPORTANT";
    public static final String TAG ="MAIN_FRAGMENT";

    private LocalDate date;
    private SubstitutesList substitutesList;

    private boolean isLoading = false;
    private SubstituteJSoupRequest request;

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

        //Get Arguments
        Bundle arguments = getArguments();
        if(arguments != null) {
            studentInformation = arguments.getParcelable(ARGUMENT_STUDENT_INFORMATION);
            date = new DateTime(arguments.getLong(ARGUMENT_DATE,0l)).toLocalDate();
            filterImportant = arguments.getBoolean(ARGUMENT_IMPORTANT, false);
        } else {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String infoClass = prefs.getString(SettingsFragment.PREF_CLASS, "");
            String infoYear = prefs.getString(SettingsFragment.PREF_YEAR, "");
            studentInformation = new StudentInformation(infoYear, infoClass);
            filterImportant = prefs.getBoolean(ARGUMENT_IMPORTANT, false);

            date = SchoolWeek.next();
        }

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
            if(substitutesList.hasSubstitutes())
                adapter.setSubstitutes(substitutesList.getSubstitutes());
            else
                showError(getString(R.string.no_substitutes_hint), getString(R.string.no_substitutes), noneImage);
        } else
            onRefresh();
        recyclerView.setAdapter(adapter);

        //RefreshLayout immediate refresh bug workaround
        if(isLoading)
            refreshLayout.setRefreshing(true);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL_LIST);
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
        if(request != null)
            request.cancel();

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
            if(VolleySingleton.isNetworkConnected(getActivity())) {
                if (refreshLayout != null)
                    refreshLayout.setRefreshing(true);

                //Store date for refreshing
                this.date = date;

                request = new SubstituteJSoupRequest(getActivity(), date, studentInformation, this, this);
                request.setRetryPolicy(new DefaultRetryPolicy(30000,5,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getInstance(getActivity()).getRequestQueue().add(request);
                isLoading = true;
            } else {
                onErrorResponse(new VolleyError(getString(R.string.error_no_connection)));
            }
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
    public void onErrorResponse(VolleyError error) {
        isLoading = false;
        request = null;

        Log.e("net-error", "Millis: "+Long.toString(error.getNetworkTimeMs()));
        Log.e("net-error", "Message: "+error.getMessage());

        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
            refreshLayout.clearAnimation();
        }
        if (errorViewRefresh != null) {
            errorViewRefresh.setRefreshing(false);
            errorViewRefresh.clearAnimation();
        }

        if (error.networkResponse != null) {
            Log.d("net-error", "Status: " + Integer.toString(error.networkResponse.statusCode));
        }

        if(substitutesList == null)
            showError(getString(R.string.error), getString(R.string.oops), errorImage);
        else
            Snackbar.make(activity.getCoordinatorLayout(), getString(R.string.oops), Snackbar.LENGTH_LONG);
    }

    @Override
    public void onResponse(SubstitutesList response) {
        isLoading = false;
        request = null;

        //Hide both swipe to refresh views
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
            refreshLayout.clearAnimation();
        }
        if (errorViewRefresh != null) {
            errorViewRefresh.setRefreshing(false);
            errorViewRefresh.clearAnimation();
        }

        if(response == null)
            return;

        if(!filterImportant)
            this.substitutesList = response;
        else
            this.substitutesList = response.filterImportant();

        //Display UI
        if (!substitutesList.hasSubstitutes()) {
            showError(getString(R.string.no_substitutes_hint), getString(R.string.no_substitutes), noneImage);
        } else {
            if (adapter != null)
                adapter.setSubstitutes(substitutesList.getSubstitutes());

            hideError();
        }

        //Update the floating action button
        if (activity != null && getUserVisibleHint())
            activity.setFabVisibility(substitutesList != null && substitutesList.hasAnnouncement());
    }

    private void hideError() {
        if(errorViewScroll != null) {
            errorViewScroll.setVisibility(View.GONE);
            errorViewScroll.setEnabled(false);
        }
        if(errorViewRefresh != null) {
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
    private void showError(String errorMessage, String title, Drawable image) {
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

    @Override
    public void onRetry() {
        hideError();
        onRefresh();
    }

    public SubstitutesAdapter getAdapter() {
        return adapter;
    }

    public static MainFragment newInstance(StudentInformation information, LocalDate date, boolean filterImportant) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_STUDENT_INFORMATION, information);
        arguments.putLong(ARGUMENT_DATE, date.toDateTimeAtCurrentTime().getMillis());
        arguments.putBoolean(ARGUMENT_IMPORTANT,  filterImportant);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(arguments);

        return fragment;
    }
    public static MainFragment newInstance(StudentInformation information, LocalDate date) {
        return MainFragment.newInstance(information, date, false);
    }

    public interface MaterialActivity {
        FloatingActionButton getFloatingActionButton();
        void setFabVisibility(boolean visible);
        AppBarLayout getAppBarLayout();
        CoordinatorLayout getCoordinatorLayout();
        void setAppBarExpanded(boolean expanded);
        void setCabVisibility(boolean visibility);
    }
}
