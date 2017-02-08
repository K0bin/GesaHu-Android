package rhedox.gesahuvertretungsplan.ui.viewHolder;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import rhedox.gesahuvertretungsplan.R;
import tr.xip.errorview.ErrorView;

/**
 * Created by Robin on 07.05.2016.
 */
public class ErrorViewHolder extends RecyclerView.ViewHolder {
	private ErrorView view;

    public ErrorViewHolder(@NonNull ErrorView itemView) {
        super(itemView);
	    this.view = itemView;
        Context context = itemView.getContext();

        //Set width & margin
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        float margin = context.getResources().getDimension(R.dimen.errorView_margin_top);
        params.setMargins(0, (int)margin, 0, (int)margin);
        itemView.setLayoutParams(params);
    }

	public void bind(@NonNull ErrorView.Config config) {
		view.setConfig(config);
	}
}
