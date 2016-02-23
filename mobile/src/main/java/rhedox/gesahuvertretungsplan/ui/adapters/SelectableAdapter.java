package rhedox.gesahuvertretungsplan.ui.adapters;

import android.support.v7.widget.RecyclerView;

import rhedox.gesahuvertretungsplan.model.Substitute;

/**
 * Created by Robin on 27.12.2015.
 */
public abstract class SelectableAdapter<T,VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    public abstract void setSelected(RecyclerView.ViewHolder viewHolder);
    public abstract void clearSelection(boolean cabFinished);
    public abstract int getSelectedIndex();
    public abstract T getSelected();
}
