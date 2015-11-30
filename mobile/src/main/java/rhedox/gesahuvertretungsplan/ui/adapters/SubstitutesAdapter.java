package rhedox.gesahuvertretungsplan.ui.adapters;

import android.app.Activity;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.ui.fragment.MainFragment;
import rhedox.gesahuvertretungsplan.ui.viewHolders.SubstituteViewHolder;

/**
 * Created by Robin on 28.10.2014.
 */
public class SubstitutesAdapter extends RecyclerView.Adapter<SubstituteViewHolder> {
    private List<Substitute> substitutes;

    @ColorInt private int circleColorImportant;
    @ColorInt private int circleColor;
    @ColorInt private int textColor;
    @ColorInt private int highlightedTextColor;
    @ColorInt private int activatedBackgroundColor;
    private int selected = -1;
    private SubstituteViewHolder selectedViewHolder;

    private MainFragment.MaterialActivity activity;

    public SubstitutesAdapter(@NonNull Activity context) {
        this.substitutes = new ArrayList<Substitute>(0);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{R.attr.circleColor, R.attr.circleHighlightedColor, R.attr.circleTextColor, R.attr.circleHighlightedTextColor, R.attr.activatedColor});
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
    public void onBindViewHolder(@NonNull SubstituteViewHolder viewHolder, int i) {
        if(substitutes != null && substitutes.size() > i) {
            viewHolder.setSubstitute(substitutes.get(i));
            viewHolder.setSelected(i == selected && selected != -1);

            if(i == selected && selected != -1)
                selectedViewHolder = viewHolder;
            else if(selectedViewHolder == viewHolder)
                selectedViewHolder = null;
        }
    }

    @Override
    public SubstituteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        FrameLayout view;
        view = (FrameLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_replacement, viewGroup, false);

        SubstituteViewHolder viewHolder = new SubstituteViewHolder(view, this, circleColor, circleColorImportant, textColor, highlightedTextColor, activatedBackgroundColor);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return substitutes.size();
    }

    public void setSubstitutes(@Nullable List<Substitute> substitutes) {
        if(substitutes == null)
            clear();

        this.substitutes = new ArrayList<Substitute>(substitutes);
        if(getItemCount() > 0) {
            notifyItemRangeInserted(0, getItemCount());
        }
    }
    public void clear() {
        clearSelection(false);
        if(getItemCount() > 0) {
            int count = getItemCount();
            substitutes.clear();
            notifyItemRangeRemoved(0, count);
        }
    }

    public void setSelected(SubstituteViewHolder viewHolder) {
        if(viewHolder == null || selected == viewHolder.getAdapterPosition())
            return;

        if(selectedViewHolder != null)
            selectedViewHolder.setSelected(false);

        selected = viewHolder.getAdapterPosition();
        selectedViewHolder = viewHolder;

        if(activity != null) {
            activity.setCabVisibility(true);
        }
    }

    public void clearSelection(boolean cabFinished) {
        if(selectedViewHolder != null)
            selectedViewHolder.setSelected(false);

        selected = -1;
        selectedViewHolder = null;

        if(activity != null && !cabFinished)
                activity.setCabVisibility(false);
    }

    public int getSelectedIndex() {
        return selected;
    }

    public Substitute getSelected() {
        if(substitutes == null || selected == -1) return null;

        return substitutes.get(selected);
    }
}
