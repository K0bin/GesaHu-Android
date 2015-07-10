package rhedox.gesahuvertretungsplan.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;

import rhedox.gesahuvertretungsplan.*;
import rhedox.gesahuvertretungsplan.net.Error;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.net.OnDownloadedListener;
import rhedox.gesahuvertretungsplan.net.SubstitutesList;

/**
 * Created by Robin on 30.06.2015.
 */
public class MainFragment extends Fragment implements OnDownloadedListener, SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener{
    private SubstitutesList plan = new SubstitutesList();
    private SubstitutesAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;

    private StudentInformation studentInformation;

    public static final String ARGUMENT_STUDENT_INFORMATION = "ARGUMENT_STUDENT_INFORMATION";
    public static final String ARGUMENT_DATE = "ARGUMENT_DATE";
    public static final String TAG ="MAIN_FRAGMENT";

    private LocalDate lastDate;
    private String announcement;

    public MainFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        LocalDate date = null;

        //Get Arguments
        Bundle arguments = getArguments();
        if(arguments != null) {
            studentInformation = arguments.getParcelable(ARGUMENT_STUDENT_INFORMATION);
            date = new DateTime(arguments.getLong(ARGUMENT_DATE,0l)).toLocalDate();
        }

        if(studentInformation == null) {
            studentInformation = new StudentInformation("","");
        }
        if(date == null)
            date = SchoolWeek.next();

        adapter = new SubstitutesAdapter(this.getActivity());

        load(date, studentInformation);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(this);

        //RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recylcler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);

        //recyclerView.setItemAnimator(new SlideAnimator(recyclerView));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        float listMargin = getActivity().getResources().getDimension(R.dimen.listMargin);
        float listMarginBottom = getActivity().getResources().getDimension(R.dimen.listMarginBottom);

        if(getActivity().findViewById(R.id.fab) != null)
            recyclerView.setPadding(0,(int)listMargin,0, (int)listMarginBottom);
        else
            recyclerView.setPadding(0,(int)listMargin,0, (int)listMargin);

        recyclerView.setClipToPadding(false);

        coordinatorLayout = (CoordinatorLayout)getActivity().findViewById(R.id.coordinator);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        AppBarLayout layout =(AppBarLayout) getActivity().findViewById(R.id.appbarLayout);
        if(layout != null)
            layout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        AppBarLayout layout = (AppBarLayout) getActivity().findViewById(R.id.appbarLayout);
        if(layout != null)
            layout.addOnOffsetChangedListener(this);

        if(!plan.isLoading() && (adapter == null || adapter.getItemCount() == 0)) {
            onRefresh();
        }
    }

    @Override
    public void onDownloaded(List<Substitute> substitutes, String announcement) {
        if(adapter != null) {
            adapter.removeAll();
            adapter.addAll(substitutes);
        }
        else if(recyclerView != null){
            adapter = new SubstitutesAdapter(getActivity());
            adapter.setSubstitutes(substitutes);
            recyclerView.setAdapter(adapter);
        }

        if(refreshLayout != null)
            refreshLayout.setRefreshing(false);

        this.announcement = announcement;
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
    public void onDestroy() {
        if(plan != null)
            plan.stop();

        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        load(lastDate, studentInformation);
    }

    public void load(LocalDate date, StudentInformation information) {
        if(!plan.isLoading() && date != null) {
            if(refreshLayout != null)
                refreshLayout.setRefreshing(true);

            lastDate = date;

            plan.load(getActivity(), date, information, this);
        }
    }

    public static MainFragment newInstance(StudentInformation information, LocalDate date) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_STUDENT_INFORMATION, information);
        arguments.putLong(ARGUMENT_DATE, date.toDateTimeAtCurrentTime().getMillis());
        MainFragment fragment = new MainFragment();
        fragment.setArguments(arguments);

        return fragment;
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

            //Toast.makeText(getActivity(), announcement, Toast.LENGTH_LONG).show();
            AnnouncementFragment.newInstance(announcement).show(getChildFragmentManager(), AnnouncementFragment.TAG);
        }

        return super.onOptionsItemSelected(item);
    }
}
