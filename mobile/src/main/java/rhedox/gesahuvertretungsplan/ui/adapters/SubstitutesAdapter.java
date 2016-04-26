package rhedox.gesahuvertretungsplan.ui.adapters;

import android.app.Activity;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.ui.fragment.MainFragment;
import rhedox.gesahuvertretungsplan.ui.viewHolders.SubstituteViewHolder;

/**
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

    private static final int ITEM_TYPE_SUBSTITUTE = 0;

    @SuppressWarnings("ResourceType")
    public SubstitutesAdapter(@NonNull Activity context) {
        this.list = new ArrayList<Substitute>(0);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{R.attr.circleColor, R.attr.circleImportantColor, R.attr.circleTextColor, R.attr.circleImportantTextColor, R.attr.activatedColor});
        textColor = typedArray.getColor(2, 0);
        highlightedTextColor = typedArray.getColor(3, 0);
        circleColorImportant= typedArray.getColor(1, 0);
        circleColor = typedArray.getColor(0, 0);
        activatedBackgroundColor = typedArray.getColor(4,0);
        typedArray.recycle();

        if(context instanceof MainFragment.MaterialActivity)
            activity = (MainFragment.MaterialActivity)context;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(list == null || list.size() <= i)
            return;

        SubstituteViewHolder substituteViewHolder = (SubstituteViewHolder) viewHolder;
        substituteViewHolder.setSubstitute(list.get(i));
        substituteViewHolder.setSelected(i == selected && selected != -1);

        if(i == selected && selected != -1)
            selectedViewHolder = substituteViewHolder;
        else if(selectedViewHolder == substituteViewHolder)
            selectedViewHolder = null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        FrameLayout view = (FrameLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_substitute, viewGroup, false);
        return new SubstituteViewHolder(view, this, circleColor, circleColorImportant, textColor, highlightedTextColor, activatedBackgroundColor);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(list == null || list.size() <= position)
            return -1;

        return ITEM_TYPE_SUBSTITUTE;
    }

    public int setList(@Nullable List<Substitute> list, boolean filterImportant, boolean sortImportant) {
        if(filterImportant && sortImportant) {
            Log.e("Adapter", "Can't both filter and sort!");
            return 0;
        }

        if(list == null || list.size() == 0) {
            clear();
            return 0;
        }
        else {
            int count = getItemCount();

            if(sortImportant)
                this.list = Collections.unmodifiableList(SubstitutesList.sort(list));
            else if (filterImportant)
                this.list = Collections.unmodifiableList(SubstitutesList.filterImportant(list));
            else
                this.list = list;

            if(count != this.list.size()) {
                if (count > this.list.size())
                    notifyItemRangeRemoved(Math.max(this.list.size() - 1, 0), count - this.list.size());
                else
                    notifyItemRangeInserted(Math.max(count - 1, 0), this.list.size() - count);
            }

            notifyItemRangeChanged(0, Math.min(this.list.size(), count));

            if(selected >= this.list.size())
                clearSelection(false);

            return this.list.size();
        }
    }
    public void clear() {
        clearSelection(false);
        if(getItemCount() > 0) {
            int count = getItemCount();
            list.clear();
            notifyItemRangeRemoved(0, count);
        }
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
            activity.setCabVisibility(true);
    }

    @Override
    public void clearSelection(boolean cabFinished) {
        if(selectedViewHolder != null)
            selectedViewHolder.setSelected(false);

        selected = -1;
        selectedViewHolder = null;

        if(activity != null && !cabFinished)
                activity.setCabVisibility(false);
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
