package rhedox.gesahuvertretungsplan.ui.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.anko.AnkoContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract;
import rhedox.gesahuvertretungsplan.ui.anko.SubstituteView;
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
public class SubstitutesAdapter extends SelectableAdapter<RecyclerView.ViewHolder> {
    @Nullable private List<Substitute> list = new ArrayList<Substitute>(0);

    private int selected = -1;

	@Nullable private SubstitutesContract.Presenter presenter;
	private int pagerPosition = -1;
	@Nullable private RecyclerView recyclerView;

	//#enumsmatter
    private static final int ITEM_TYPE_SUBSTITUTE = 0;
    private static final int ITEM_TYPE_CONTENT_AD = 1;
    private static final int ITEM_TYPE_INSTALL_AD = 2;
    private static final int ITEM_TYPE_EMPTY_VIEW = 3;
    @IntDef({ITEM_TYPE_SUBSTITUTE, ITEM_TYPE_CONTENT_AD, ITEM_TYPE_INSTALL_AD, ITEM_TYPE_EMPTY_VIEW})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ItemType {}

	//Colors
	@ColorInt private int textColor;
	@ColorInt private int textColorRelevant;
	@ColorInt private int circleColor;
	@ColorInt private int circleColorRelevant;

	@NonNull private AnkoContext ankoContext;
	@NonNull private SubstituteView ankoComponent;

    @SuppressWarnings("ResourceType")
    public SubstitutesAdapter(int pagerPosition, @Nullable SubstitutesContract.Presenter presenter, @NonNull Context context) {
	    this.pagerPosition = pagerPosition;
	    this.presenter = presenter;

	    TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{R.attr.circleColor, R.attr.circleImportantColor, R.attr.circleTextColor, R.attr.circleImportantTextColor});
	    textColor = typedArray.getColor(2, 0);
	    textColorRelevant = typedArray.getColor(3, 0);
	    circleColorRelevant = typedArray.getColor(1, 0);
	    circleColor = typedArray.getColor(0, 0);
	    typedArray.recycle();

	    ankoContext = AnkoContext.Companion.createReusable(context, this, false);
	    ankoComponent = new SubstituteView(context);
    }

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);

		this.recyclerView = recyclerView;
	}

	@Override
	public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
		super.onDetachedFromRecyclerView(recyclerView);

		this.recyclerView = null;
	}

	@Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (getItemViewType(i)) {
            case ITEM_TYPE_SUBSTITUTE: {
                if (list == null || list.size() <= i)
                    return;

                SubstituteViewHolder substituteViewHolder = (SubstituteViewHolder) viewHolder;
                substituteViewHolder.setSubstitute(list.get(i));
                substituteViewHolder.setSelected(i == selected && selected != -1, false);
            } break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_SUBSTITUTE: {
	            View view = ankoComponent.createView(viewGroup);
                return new SubstituteViewHolder(view, presenter, pagerPosition, textColor, textColorRelevant, circleColor, circleColorRelevant);
            }

            case ITEM_TYPE_EMPTY_VIEW:
                return new ErrorViewHolder(new ErrorView(viewGroup.getContext()));
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if(list != null && list.size() > 0)
            return list.size();
        else
            return 1;
    }

    @Override
    public @ItemType int getItemViewType(int position) {
        if(list == null || list.size() == 0)
            return ITEM_TYPE_EMPTY_VIEW;

        return ITEM_TYPE_SUBSTITUTE;
    }

    /*
    * @param list The list of substitutes to display. If it's null, the RecyclerView will be cleared
     */
    public void showList(@Nullable List<Substitute> list) {
	    Log.d("SubstitutesAdapter", "Showed list");
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
        }
    }
    private void clear() {
        clearSelection();
	    int previousCount = getItemCount();
        list = Collections.emptyList();

        //Notify recyclerview about changes
        if(previousCount > 0) {
            notifyItemRangeRemoved(0, previousCount);
        }
        notifyItemInserted(0);
    }

	@Override
	public void setSelected(int position) {
		if(position != -1) {
			setViewHolderSelected(selected, false);
			setViewHolderSelected(position, true);
		} else if(selected != -1) {
			setViewHolderSelected(selected, false);
		}
		selected = position;
	}

	private void setViewHolderSelected(int position, boolean isSelected) {
		if(recyclerView == null)
			return;

		RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
		if (viewHolder instanceof SubstituteViewHolder) {
			SubstituteViewHolder substituteViewHolder = (SubstituteViewHolder) viewHolder;
			substituteViewHolder.setSelected(isSelected, true);
		}
	}

	@Override
    public void clearSelection() {
        if(selected == -1)
            return;

		setViewHolderSelected(selected, false);
        selected = -1;

	    if(presenter != null)
		    presenter.onListItemSelected(pagerPosition, -1);
    }

    @Override
    public int getSelectedIndex() {
        return selected;
    }
}
