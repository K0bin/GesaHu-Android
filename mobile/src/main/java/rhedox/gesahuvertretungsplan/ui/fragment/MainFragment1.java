package rhedox.gesahuvertretungsplan.ui.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.leakcanary.RefWatcher;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rhedox.gesahuvertretungsplan.*;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentObserver;
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider;
import rhedox.gesahuvertretungsplan.model.database.SubstitutesLoaderHelper;
import rhedox.gesahuvertretungsplan.ui.DividerItemDecoration;
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesAdapter;
import rhedox.gesahuvertretungsplan.ui.widget.SwipeRefreshLayoutFix;
import tr.xip.errorview.ErrorView;

/**
 * Created by Robin on 30.06.2015.
 */
public class MainFragment1 extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SubstitutesLoaderHelper.Callback, ErrorView.RetryListener, SyncStatusObserver {
	private SubstitutesAdapter adapter;
	@BindView(R.id.swipe)
	SwipeRefreshLayoutFix refreshLayout;
	@BindView(R.id.recycler)
	RecyclerView recyclerView;
	private Snackbar snackbar;
	private Unbinder unbinder;

	private boolean filterImportant = false;
	private boolean sortImportant = false;

	public static final String ARGUMENT_DATE = "ARGUMENT_DATE";
	public static final String TAG = "MAIN_FRAGMENT";

	private LocalDate date;
	@Nullable
	private SubstitutesList substitutesList;
	private boolean isLoading = false;

	private MaterialActivity activity;

	public static final String STATE_KEY_SUBSTITUTE_LIST = "substitutelist";

	private SubstitutesLoaderHelper callback;
	private SubstitutesContentObserver observer;
	private Object statusListenerHandle;
	private Account account;

	public MainFragment1() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		//Get Preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		filterImportant = prefs.getBoolean(PreferenceFragment.PREF_FILTER, false);
		sortImportant = prefs.getBoolean(PreferenceFragment.PREF_SORT, false);

		//Get Arguments
		Bundle arguments = getArguments();
		if (arguments != null)
			date = new DateTime(arguments.getLong(ARGUMENT_DATE, 0L)).toLocalDate();
		else
			date = SchoolWeek.nextFromNow();


		if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
			AccountManager accountManager = AccountManager.get(getContext());

			Account[] accounts = accountManager.getAccountsByType(App.ACCOUNT_TYPE);
			if (accounts.length > 0)
				account = accounts[0];
		}

		statusListenerHandle = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE | ContentResolver.SYNC_OBSERVER_TYPE_PENDING | ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS, this);

	    callback = new SubstitutesLoaderHelper(getLoaderManager(), getContext(), date, this);
		callback.load();
		//observer = new SubstitutesContentObserver(new Handler(), date, callback);
		getContext().getContentResolver().registerContentObserver(Uri.parse("content://rhedox.gesahuvertretungsplan.substitutes/substitutes"), true, observer);
		getContext().getContentResolver().registerContentObserver(Uri.parse("content://rhedox.gesahuvertretungsplan.substitutes/announcements"), true, observer);
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
        //else if(substitutesList == null && getUserVisibleHint())
        //    onRefresh();
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

        //LeakCanary
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        if(refWatcher != null)
            refWatcher.watch(this);

	    ContentResolver.removeStatusChangeListener(statusListenerHandle);
	    getContext().getContentResolver().unregisterContentObserver(observer);

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
	    refreshLayout.setRefreshing(false);
	    if(account != null) {
		    if(!ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority) && !ContentResolver.isSyncPending(account, SubstitutesContentProvider.authority)) {
			    Bundle bundle = new Bundle();
			    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
			    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			    ContentResolver.requestSync(account, App.ACCOUNT_TYPE, bundle);
		    }
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
	public void onSubstitutesLoaded(@NotNull SubstitutesList substitutesList) {
		isLoading = false;

		clearRefreshViews();

		this.substitutesList = substitutesList;
		populateList();

		//Update the floating action button
		if(activity != null)
			activity.updateUI();
	}

	/**
	 * Called when the fragment just became visible on the ViewPager
	 */
    public void onDisplay() {
        //if(substitutesList == null)
        //    onRefresh();
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

    public static MainFragment1 newInstance(LocalDate date) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARGUMENT_DATE, date.toDateTimeAtCurrentTime().getMillis());
        MainFragment1 fragment = new MainFragment1();
        fragment.setArguments(arguments);

        return fragment;
    }

	@Override
	public void onStatusChanged(int which) {
		Log.d("SyncStatus", "STATUS CHANGED");

		if(account != null) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority))
						refreshLayout.setRefreshing(true);
					else
						clearRefreshViews();
				}
			});
		}
	}

	public interface MaterialActivity {
        /**
         * Update visibility of activity level ui views such as the Snackbar, the floating action button or the contextual action bar
         */
        void updateUI();

        CoordinatorLayout getCoordinatorLayout();
        MainFragment1 getVisibleFragment();

    }
}
