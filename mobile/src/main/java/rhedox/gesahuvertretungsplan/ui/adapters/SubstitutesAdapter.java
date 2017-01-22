package rhedox.gesahuvertretungsplan.ui.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

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
 * Also handles the selection of layoutManager entries
 *
 * Created by Robin on 28.10.2014.
 */
public class SubstitutesAdapter extends SelectableAdapter<RecyclerView.ViewHolder> {
    @Nullable private List<Substitute> list = new ArrayList<Substitute>(0);

    private int selected = -1;

	@Nullable private SubstitutesContract.Presenter presenter;
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

	@Dimension private float selectedElevation;

	@NonNull private SubstituteView ankoComponent;

	private static final String STATE_SELECTED = "selected";

    @SuppressWarnings("ResourceType")
    public SubstitutesAdapter(@Nullable SubstitutesContract.Presenter presenter, @NonNull Context context) {
	    this.presenter = presenter;

	    TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{R.attr.circleColor, R.attr.circleImportantColor, R.attr.circleTextColor, R.attr.circleImportantTextColor});
	    textColor = typedArray.getColor(2, 0);
	    textColorRelevant = typedArray.getColor(3, 0);
	    circleColorRelevant = typedArray.getColor(1, 0);
	    circleColor = typedArray.getColor(0, 0);
	    typedArray.recycle();

	    selectedElevation = context.getResources().getDimension(R.dimen.touch_raise);

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
                substituteViewHolder.bindSubstitute(list.get(i));
                substituteViewHolder.setSelected(i == selected && selected != -1, false);
            } break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_SUBSTITUTE: {
	            View view = ankoComponent.createView(viewGroup);
                return new SubstituteViewHolder(view, presenter, textColor, textColorRelevant, circleColor, circleColorRelevant, selectedElevation);
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
    * @param layoutManager The layoutManager of substitutes to display. If it's null, the RecyclerView will be cleared
     */
    public void setSubstitutes(@Nullable List<Substitute> list) {
        if(list == null || list.size() == 0) {
            clear();
        } else {
            //Get count before replacing layoutManager
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

            //Clear the selection
            setSelected(-1);

	        if(count < this.list.size() && recyclerView != null)
		        recyclerView.scrollToPosition(0);
        }
    }
	@Nullable
	public List<Substitute> getSubstitutes() {
		return list;
	}

    private void clear() {
	    setSelected(-1);
	    int previousCount = getItemCount();
        list = Collections.emptyList();

        //Notify recyclerview about changes
        if(previousCount > 0) {
            notifyItemRangeRemoved(0, previousCount);
        }
        notifyItemInserted(0);

	    if(recyclerView != null)
		    recyclerView.scrollToPosition(0);
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
		if(recyclerView == null || position == -1)
			return;

		RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
		if (viewHolder instanceof SubstituteViewHolder) {
			SubstituteViewHolder substituteViewHolder = (SubstituteViewHolder) viewHolder;
			substituteViewHolder.setSelected(isSelected, true);
		}
	}
}
