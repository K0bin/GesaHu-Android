package rhedox.gesahuvertretungsplan.ui.adapters;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Robin on 27.12.2015.
 */
public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    public abstract void setSelected(int position);
    public abstract void clearSelection();
    public abstract int getSelectedIndex();
}
