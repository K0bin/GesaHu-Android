package rhedox.gesahuvertretungsplan.ui.fragment;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import rhedox.gesahuvertretungsplan.net.SubstituteRequest;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.net.VolleySingleton;
import rhedox.gesahuvertretungsplan.ui.DividerItemDecoration;
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesAdapter;
import rhedox.gesahuvertretungsplan.util.TextUtils;

/**
 * Created by Robin on 30.06.2015.
 */
public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener, View.OnClickListener, Response.Listener<SubstitutesList>, Response.ErrorListener{
    //private SubstitutesList plan = new SubstitutesList();
    private SubstitutesAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private CoordinatorLayout coordinatorLayout;
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
        }

        if(studentInformation == null) {
            studentInformation = new StudentInformation("","");
        }
        if(date == null)
            date = SchoolWeek.next();

        load(date, studentInformation);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //RefreshLayout
        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe);
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
        if(isLoading)
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //Get a CoordinatorLayout from parent activity for the snackbar
        coordinatorLayout = (CoordinatorLayout)getActivity().findViewById(R.id.coordinator);

        return view;
    }



    @Override
    public void onPause() {
        super.onPause();

        //Remove AppBarLayout Listener so AppBarLayout and RefreshLayout work together
        AppBarLayout layout = (AppBarLayout) getActivity().findViewById(R.id.appbarLayout);
        if(layout != null)
            layout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Add AppBarLayout Listener so AppBarLayout and RefreshLayout work together
        AppBarLayout layout = (AppBarLayout) getActivity().findViewById(R.id.appbarLayout);
        if(layout != null)
            layout.addOnOffsetChangedListener(this);

        if(refreshLayout != null)
            refreshLayout.setEnabled(true);

        if(substitutes == null)
            onRefresh();
    }

    @Override
    public void onDestroyView() {
        //RefreshLayout keeping views in hierarchy bug workaround
        if(refreshLayout != null) {
            refreshLayout.setRefreshing(false);
            refreshLayout.clearAnimation();
        }
        //Remove all view references to prevent leaking
        refreshLayout = null;
        recyclerView = null;
        coordinatorLayout = null;
        adapter = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if(request != null) {
            request.cancel();
        }

        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        load(date, studentInformation);
    }

    public void load(LocalDate date, StudentInformation information) {
        if(date != null && !isLoading) {
            if(VolleySingleton.isNetworkConnected(getActivity())) {
                if (refreshLayout != null)
                    refreshLayout.setRefreshing(true);

                //Store date for refreshing
                this.date = date;

                request = new SubstituteRequest(getActivity(), date, studentInformation, this, this);
                VolleySingleton.getInstance(getActivity()).getRequestQueue().add(request);
                isLoading = true;
            } else {
                onErrorResponse(new VolleyError(getString(R.string.error_no_connection)));
            }
        }
    }

    public boolean getHasAnnouncement() {
        return !TextUtils.isEmpty(announcement) && !announcement.equals("keine");
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

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        if(offset == 0) {
            if(refreshLayout != null)
                refreshLayout.setEnabled(true);
        }
        else {
            if(refreshLayout != null)
                refreshLayout.setEnabled(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_fragment_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_announcement) {
            if(announcement == null)
                return true;

            AnnouncementFragment.newInstance(announcement).show(getChildFragmentManager(), AnnouncementFragment.TAG);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        AnnouncementFragment.newInstance(getHasAnnouncement() ? announcement : getString(R.string.no_announcements)).show(getChildFragmentManager(), AnnouncementFragment.TAG);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        isLoading = false;
        request = null;

        if(refreshLayout != null)
            refreshLayout.setRefreshing(false);

        String errorMessage = error.getMessage();
        if(TextUtils.isEmpty(errorMessage))
            errorMessage = getString(R.string.error);

        if(coordinatorLayout != null && getUserVisibleHint())
            Snackbar.make(coordinatorLayout, errorMessage, Snackbar.LENGTH_LONG).setAction(getString(R.string.retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainFragment.this.onRefresh();
                }
            }).show();
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

        if (getActivity() != null && getUserVisibleHint()) {
            FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
            if (floatingActionButton != null) {
                if(getHasAnnouncement()) {
                    floatingActionButton.setEnabled(true);
                    floatingActionButton.show();
                } else {
                    floatingActionButton.hide();
                    floatingActionButton.setEnabled(false);
                }
            }
        }
    }
}
