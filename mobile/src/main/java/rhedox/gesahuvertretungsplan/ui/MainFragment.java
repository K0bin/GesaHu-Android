package rhedox.gesahuvertretungsplan.ui;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import junit.runner.Version;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.WeakHashMap;

import rhedox.gesahuvertretungsplan.*;
import rhedox.gesahuvertretungsplan.net.Error;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.net.OnDownloadedListener;
import rhedox.gesahuvertretungsplan.net.SubstitutesList;
import rhedox.gesahuvertretungsplan.net.SubstitutesListResult;
import rhedox.gesahuvertretungsplan.net.SubstitutesLoader;

/**
 * Created by Robin on 30.06.2015.
 */
public class MainFragment extends Fragment implements OnDownloadedListener, SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener, View.OnClickListener, LoaderManager.LoaderCallbacks<SubstitutesListResult>{
    //private SubstitutesList plan = new SubstitutesList();
    private SubstitutesAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;

    private StudentInformation studentInformation;

    public static final String ARGUMENT_STUDENT_INFORMATION = "ARGUMENT_STUDENT_INFORMATION";
    public static final String ARGUMENT_DATE = "ARGUMENT_DATE";
    public static final String TAG ="MAIN_FRAGMENT";

    private LocalDate date;
    private String announcement;
    private List<Substitute> substitutes;

    private static final int LOADER_SUBSTITUTES = 1;

    public MainFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);

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

        load(date, studentInformation);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(this);
        TypedArray typedArray = getActivity().getTheme().obtainStyledAttributes(new int[]{R.attr.colorAccent});
        refreshLayout.setColorSchemeColors(typedArray.getColor(0, 0xff000000));
        typedArray.recycle();
        //DisplayMetrics metrics = getResources().getDisplayMetrics();
        //refreshLayout.setProgressViewOffset(false, 0, (int)(metrics.density * 64));

        //RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recylcler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(manager);


        adapter = new SubstitutesAdapter(this.getActivity());
        if(substitutes != null)
            adapter.addAll(substitutes);
        else
            onRefresh();
        recyclerView.setAdapter(adapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            recyclerView.setItemAnimator(null);
        else
            recyclerView.setItemAnimator(new DefaultItemAnimator());

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

        if(substitutes == null)
            onRefresh();
    }

    @Override
    public void onDownloaded(List<Substitute> substitutes, String announcement) {
        this.announcement = announcement;
        this.substitutes = substitutes;

        if(recyclerView != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getUserVisibleHint()) {
                int top = refreshLayout != null ? refreshLayout.getTop() : recyclerView.getTop() + recyclerView.getHeight() / 2;
                int left = refreshLayout != null ? refreshLayout.getLeft() : recyclerView.getLeft() + recyclerView.getWidth() / 2;

                Animator animator = ViewAnimationUtils.createCircularReveal(recyclerView, left, top, 0, Math.max(Math.abs(left - recyclerView.getRight()), Math.abs(top - recyclerView.getHeight())));
                animator.start();
            }

            adapter.removeAll();
            adapter.addAll(substitutes);

            if (refreshLayout != null)
                refreshLayout.setRefreshing(false);
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
    public void onDestroyView() {
        if(refreshLayout != null) {
            refreshLayout.setRefreshing(false);
            refreshLayout.clearAnimation();
        }
        refreshLayout = null;
        recyclerView = null;
        coordinatorLayout = null;
        adapter = null;
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        load(date, studentInformation);
    }

    public void load(LocalDate date, StudentInformation information) {

        //if(!plan.isLoading() && date != null) {
        if(date != null) {

            if(refreshLayout != null)
                refreshLayout.setRefreshing(true);

            this.date = date;

            getLoaderManager().initLoader(LOADER_SUBSTITUTES, Bundle.EMPTY, this);

            //plan.load(getActivity(), date, information, this);
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

            AnnouncementFragment.newInstance(announcement).show(getChildFragmentManager(), AnnouncementFragment.TAG);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        AnnouncementFragment.newInstance(announcement != null ? announcement : "Keine Ankündigungen").show(getChildFragmentManager(), AnnouncementFragment.TAG);
    }

    @Override
    public Loader<SubstitutesListResult> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_SUBSTITUTES:
                return new SubstitutesLoader(getActivity(), date, studentInformation);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<SubstitutesListResult> loader, SubstitutesListResult data) {
        switch (loader.getId()) {
            case LOADER_SUBSTITUTES:
                if(data.getStatus() == Error.SUCCESS)
                    onDownloaded(data.getSubstitutes(), data.getAnnouncement());
                else
                    onDownloadFailed(data.getStatus());
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<SubstitutesListResult> loader) {

    }
}
