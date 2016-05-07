package rhedox.gesahuvertretungsplan.ui.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import tr.xip.errorview.ErrorView;

/**
 * Created by Robin on 07.05.2016.
 */
public class ErrorViewHolder extends RecyclerView.ViewHolder {
    private ErrorView view;

    public ErrorViewHolder(ErrorView itemView) {
        super(itemView);

        this.view = itemView;
    }

    public void setConfig(ErrorView.Config config) {
        if(config == null || view == null)
            return;

        this.view.setConfig(config);
    }
}
