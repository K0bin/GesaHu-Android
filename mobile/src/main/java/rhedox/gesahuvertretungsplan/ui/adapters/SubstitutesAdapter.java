package rhedox.gesahuvertretungsplan.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.Substitute_old;
import rhedox.gesahuvertretungsplan.util.NetworkUtils;
import rhedox.gesahuvertretungsplan.ui.fragment.MainFragment;
import rhedox.gesahuvertretungsplan.ui.viewHolders.ErrorViewHolder;
import rhedox.gesahuvertretungsplan.ui.viewHolders.SubstituteViewHolder;
import tr.xip.errorview.ErrorView;

/**
 * SubstitutesAdapter
 * Manages the views that are expected to be displayed by the RecyclerView
 * Also handles the selection of list entries
 *
 * Created by Robin on 28.10.2014.
 */
public class SubstitutesAdapter extends SelectableAdapter<Substitute, RecyclerView.ViewHolder> {
    private List<Substitute> list;

    @ColorInt private int circleColorImportant;
    @ColorInt private int circleColor;
    @ColorInt private int textColor;
    @ColorInt private int highlightedTextColor;
    @ColorInt private int activatedBackgroundColor;
    private int selected = -1;
    @Nullable private SubstituteViewHolder selectedViewHolder;

    @Nullable private MainFragment.MaterialActivity activity;
	private Context context;

	//#enumsmatter
    private static final int ITEM_TYPE_SUBSTITUTE = 0;
    private static final int ITEM_TYPE_CONTENT_AD = 1;
    private static final int ITEM_TYPE_INSTALL_AD = 2;
    private static final int ITEM_TYPE_EMPTY_VIEW = 3;
    @IntDef({ITEM_TYPE_SUBSTITUTE, ITEM_TYPE_CONTENT_AD, ITEM_TYPE_INSTALL_AD, ITEM_TYPE_EMPTY_VIEW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ItemType {}

	//#enumsmatter
    private static final int ERROR_NONE = 0;
    private static final int ERROR_EMPTY = 1;
    private static final int ERROR_CONNECTION = 2;
    private static final int ERROR_UNKNOWN = 3;
    @IntDef({ERROR_NONE, ERROR_EMPTY, ERROR_UNKNOWN, ERROR_CONNECTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Error {}

    private @Error int error = ERROR_NONE;
    private ErrorView.Config emptyConfig;
    private ErrorView.Config errorConfig;
    private ErrorView.Config connectionConfig;

    @SuppressWarnings("ResourceType")
    public SubstitutesAdapter(@NonNull Activity context) {
        this.list = new ArrayList<Substitute>(0);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{R.attr.circleColor, R.attr.circleImportantColor, R.attr.circleTextColor, R.attr.circleImportantTextColor, R.attr.activatedColor, R.attr.textPrimary, R.attr.textSecondary, R.attr.colorAccent});
        textColor = typedArray.getColor(2, 0);
        highlightedTextColor = typedArray.getColor(3, 0);
        circleColorImportant= typedArray.getColor(1, 0);
        circleColor = typedArray.getColor(0, 0);
        activatedBackgroundColor = typedArray.getColor(4,0);
        int errorTitleColor = typedArray.getColor(5,0);
        int errorMessageColor = typedArray.getColor(6,0);
        int errorRetryColor = typedArray.getColor(7,0);
        typedArray.recycle();

        String errorRetryText = context.getString(R.string.retry);

        emptyConfig = ErrorView.Config.create()
                .title(context.getString(R.string.no_substitutes))
                .titleColor(errorTitleColor)
                .image(R.drawable.no_substitutes)
                .subtitle(context.getString(R.string.no_substitutes_hint))
                .subtitleColor(errorMessageColor)
                .retryVisible(false)
                .build();

        errorConfig = ErrorView.Config.create()
                .title(context.getString(R.string.error))
                .titleColor(errorTitleColor)
                .image(R.drawable.error_view_cloud)
                .subtitle(context.getString(R.string.oops))
                .subtitleColor(errorMessageColor)
                .retryTextColor(errorRetryColor)
                .retryText(errorRetryText)
                .build();

	    connectionConfig = ErrorView.Config.create()
			    .title(context.getString(R.string.error))
			    .titleColor(errorTitleColor)
			    .image(R.drawable.error_view_cloud)
			    .subtitle(context.getString(R.string.error_no_connection))
			    .subtitleColor(errorMessageColor)
			    .retryTextColor(errorRetryColor)
			    .retryText(errorRetryText)
			    .build();

	    this.context = context;

        if(context instanceof MainFragment.MaterialActivity)
            activity = (MainFragment.MaterialActivity)context;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (getItemViewType(i)) {
            case ITEM_TYPE_SUBSTITUTE: {
                if (list == null || list.size() <= i)
                    return;

                SubstituteViewHolder substituteViewHolder = (SubstituteViewHolder) viewHolder;
                substituteViewHolder.setSubstitute(list.get(i));
                substituteViewHolder.setSelected(i == selected && selected != -1);

                if (i == selected && selected != -1)
                    selectedViewHolder = substituteViewHolder;
                else if (selectedViewHolder == substituteViewHolder)
                    selectedViewHolder = null;
            } break;

            case ITEM_TYPE_EMPTY_VIEW:
                ErrorViewHolder errorViewHolder = (ErrorViewHolder) viewHolder;

                if(error == ERROR_UNKNOWN)
                    errorViewHolder.setConfig(errorConfig);
                else if(error == ERROR_EMPTY)
                    errorViewHolder.setConfig(emptyConfig);
                else if(error == ERROR_CONNECTION)
	                errorViewHolder.setConfig(connectionConfig);

                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_SUBSTITUTE: {
                ViewGroup view = (ViewGroup) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_substitute, viewGroup, false);

                return new SubstituteViewHolder(view, this, circleColor, circleColorImportant, textColor, highlightedTextColor, activatedBackgroundColor);
            }

            case ITEM_TYPE_EMPTY_VIEW:
                ErrorView view = new ErrorView(viewGroup.getContext());
	            if(activity != null && activity.getVisibleFragment() != null)
                    view.setOnRetryListener(activity.getVisibleFragment());

                //Set width & margin
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                float margin = viewGroup.getContext().getResources().getDimension(R.dimen.errorView_margin_top);
                params.setMargins(0, (int)margin, 0, 0);
                view.setLayoutParams(params);

                return new ErrorViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if(list != null && list.size() > 0)
            return list.size();
        else if(error != ERROR_NONE)
            return 1;
        else
            return 0;
    }

    @Override
    public @ItemType int getItemViewType(int position) {
        if((list == null || list.size() == 0) && error != ERROR_NONE)
            return ITEM_TYPE_EMPTY_VIEW;

        return ITEM_TYPE_SUBSTITUTE;
    }

    /*
    * @param list The list of substitutes to display. If it's null, the RecyclerView will be cleared
     */
    public void showList(@Nullable List<Substitute> list) {
        if(list == null || list.size() == 0) {
            clear();
        } else {
            //Get count before replacing list
            int count = getItemCount();

            this.list = list;

            //Notify recyclerview about changes
            if(count != this.list.size()) {
                if (count > this.list.size())
                    notifyItemRangeRemoved(Math.max(this.list.size() - 1, 0), count - this.list.size());
                else
                    notifyItemRangeInserted(Math.max(count - 1, 0), this.list.size() - count);
            }

            notifyItemRangeChanged(0, Math.min(this.list.size(), count));

            //Update the selection
            if(selected >= this.list.size())
                clearSelection();

            //Show or hide error view
            if(this.list.size() > 0)
                error = ERROR_NONE;
            else
                error = ERROR_EMPTY;
        }
    }
    public void clear() {
        clearSelection();
        list = Collections.emptyList();

        //Set error
        error = ERROR_EMPTY;

        //Notify recyclerview about changes
        if(getItemCount() > 0) {
            int count = getItemCount();
            notifyItemRangeRemoved(0, count);
        }
        notifyItemInserted(0);
    }

    public void showError() {
        this.clear();

	    if(NetworkUtils.isNetworkConnected(context))
		    this.error = ERROR_UNKNOWN;
	    else
		    this.error = ERROR_CONNECTION;

        notifyItemChanged(0);
    }


    @Override
    public void setSelected(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder == null || selected == viewHolder.getAdapterPosition() || !(viewHolder instanceof SubstituteViewHolder))
            return;

        if(selectedViewHolder != null)
            selectedViewHolder.setSelected(false);

        selected = viewHolder.getAdapterPosition();
        selectedViewHolder = (SubstituteViewHolder)viewHolder;

        selectedViewHolder.setSelected(true);

        if(activity != null)
            activity.updateUI();
    }

    @Override
    public void clearSelection() {
        if(selected == -1 && selectedViewHolder == null)
            return;

        if(selectedViewHolder != null)
            selectedViewHolder.setSelected(false);

        selected = -1;
        selectedViewHolder = null;

        if(activity != null)
            activity.updateUI();
    }

    @Override
    public int getSelectedIndex() {
        return selected;
    }

    @Override
    public Substitute getSelected() {
        if(list == null || selected == -1) return null;

        return list.get(selected);
    }
}
