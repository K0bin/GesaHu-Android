package rhedox.gesahuvertretungsplan.ui.fragment;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import rhedox.gesahuvertretungsplan.*;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.net.SubstituteJSoupRequest;
import rhedox.gesahuvertretungsplan.net.SubstituteRequest;
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
    private SwipeRefreshLayoutFix refreshLayout;
    private RecyclerView recyclerView;

    private StudentInformation studentInformation;
    private boolean filterImportant = false;

    public static final String ARGUMENT_STUDENT_INFORMATION = "ARGUMENT_STUDENT_INFORMATION";
    public static final String ARGUMENT_DATE = "ARGUMENT_DATE";
    public static final String ARGUMENT_IMPORTANT = "ARGUMENT_IMPORTANT";
    public static final String TAG ="MAIN_FRAGMENT";

    private LocalDate date;
    private String announcement;
    private List<Substitute> substitutes;

    private boolean isLoading = false;
    private SubstituteRequest request;

    private ErrorView errorView;

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

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //RefreshLayout
        refreshLayout = (SwipeRefreshLayoutFix)view.findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(this);

        //RefreshLayout color scheme
        TypedArray typedArray = getActivity().getTheme().obtainStyledAttributes(new int[]{R.attr.colorAccent});
        refreshLayout.setColorSchemeColors(typedArray.getColor(0, 0xff000000));
        typedArray.recycle();

        //RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recylcler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(manager);

        //RecyclerView Adapter
        adapter = new SubstitutesAdapter(this.getActivity());
        if(substitutes != null)
            adapter.addAll(substitutes);
        else
            onRefresh();
        recyclerView.setAdapter(adapter);

        //RefreshLayout immediate refresh bug workaround
        if(isLoading) {
            refreshLayout.setRefreshing(true);
        }

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        errorView = (ErrorView)view.findViewById(R.id.error_view);

        if(errorView != null) {
            errorView.setVisibility(View.GONE);
            errorView.setOnRetryListener(this);
        }

        if(getActivity() instanceof MaterialActivity)
            activity = (MaterialActivity)getActivity();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(isLoading)
            refreshLayout.setRefreshing(true);
        else if(substitutes == null || substitutes.isEmpty())
            onRefresh();
    }

    @Override
    public void onDestroyView() {
        //Remove all view references to prevent leaking
        refreshLayout = null;
        recyclerView = null;
        adapter = null;
        errorView = null;
        activity = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if(request != null)
            request.cancel();

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

                //request = new SubstituteRequest(getActivity(), date, studentInformation, this, this);
                request = new SubstituteJSoupRequest(getActivity(), date, studentInformation, this, this);
                VolleySingleton.getInstance(getActivity()).getRequestQueue().add(request);
                isLoading = true;
            } else {
                onErrorResponse(new VolleyError(getString(R.string.error_no_connection)));
            }
        }
    }

    public boolean hasAnnouncement() {
        return !TextUtils.isEmpty(announcement) && !announcement.equals("keine");
    }

    public LocalDate getDate() {
        return date;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setSwipeToRefreshEnabled(boolean isEnabled) {
        if(refreshLayout != null)
            refreshLayout.setEnabled(isEnabled);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        isLoading = false;
        request = null;

        if (refreshLayout != null)
            refreshLayout.setRefreshing(false);

        if(adapter != null && adapter.getItemCount() == 0) {
            if (error != null && error.networkResponse != null)
                showError(error.networkResponse.statusCode);
            else if (error != null && TextUtils.isEmpty(error.getMessage()))
                showError(error.getMessage());
            else
                showError(getString(R.string.error));
        }
    }

    @Override
    public void onResponse(SubstitutesList response) {
        isLoading = false;
        request = null;

        if (refreshLayout != null)
            refreshLayout.setRefreshing(false);

        if (response == null)
            return;

        this.announcement = response.getAnnouncement();
        if(!filterImportant)
            this.substitutes = response.getSubstitutes();
        else
            this.substitutes = SubstitutesList.filterImportant(getActivity(), response.getSubstitutes());

        if (recyclerView != null) {
            adapter.removeAll();

            if(substitutes == null)
                substitutes = new ArrayList<Substitute>();

            if (substitutes.size() == 0) {
                Substitute substitute = Substitute.makeEmptyListSubstitute(getActivity());
                substitutes.add(substitute);
            }
            adapter.addAll(this.substitutes);
        }

        if (activity != null && getUserVisibleHint())
            activity.setFabVisibility(hasAnnouncement());

        hideError();
    }

    private void hideError() {
        if(errorView != null) {
            errorView.setVisibility(View.GONE);
        }
        if(refreshLayout != null)
            refreshLayout.setVisibility(View.VISIBLE);
    }

    private void showError(int errorCode) {
        if(errorView != null) {
            errorView.setVisibility(View.VISIBLE);
            errorView.setError(errorCode);
        }
        if(refreshLayout != null)
            refreshLayout.setVisibility(View.GONE);
    }
    private void showError(String errorMessage) {
        if(errorView != null) {
            errorView.setVisibility(View.VISIBLE);
            errorView.setSubtitle(errorMessage);
        }
        if(refreshLayout != null)
            refreshLayout.setVisibility(View.GONE);
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
        void setAppBarExpanded(boolean expanded);
        void setCabVisibility(boolean visibility);
    }
}
