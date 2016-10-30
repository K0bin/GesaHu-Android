package rhedox.gesahuvertretungsplan.ui.viewHolders;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import rhedox.gesahuvertretungsplan.R;
import tr.xip.errorview.ErrorView;

/**
 * Created by Robin on 07.05.2016.
 */
public class ErrorViewHolder extends RecyclerView.ViewHolder {
    public ErrorViewHolder(ErrorView itemView) {
        super(itemView);
        Context context = itemView.getContext();

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{R.attr.circleColor, R.attr.circleImportantColor, R.attr.circleTextColor, R.attr.circleImportantTextColor, R.attr.activatedColor, R.attr.textPrimary, R.attr.textSecondary, R.attr.colorAccent});
        int errorTitleColor = typedArray.getColor(5,0);
        int errorMessageColor = typedArray.getColor(6,0);
        typedArray.recycle();

        ErrorView.Config emptyConfig = ErrorView.Config.create()
                .title(context.getString(R.string.no_substitutes))
                .titleColor(errorTitleColor)
                .image(R.drawable.no_substitutes)
                .subtitle(context.getString(R.string.no_substitutes_hint))
                .subtitleColor(errorMessageColor)
                .retryVisible(false)
                .build();

        //Set width & margin
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        float margin = context.getResources().getDimension(R.dimen.errorView_margin_top);
        params.setMargins(0, (int)margin, 0, 0);
        itemView.setLayoutParams(params);
        itemView.setConfig(emptyConfig);
    }
}
