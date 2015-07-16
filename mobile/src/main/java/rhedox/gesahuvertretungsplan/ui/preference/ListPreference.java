package rhedox.gesahuvertretungsplan.ui.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.net.SubstituteRequest;


/**
 * Created by Robin on 16.07.2015.
 */
public class ListPreference extends android.preference.ListPreference {

    //SubLollipop
    private TextView titleView;
    private TextView summaryView;

    private ImageView imageView;
    private View imageFrame;

    private int iconResId;
    private Drawable icon;

    public ListPreference(Context context) {
        super(context);
    }
    public ListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListPreference(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListPreference(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        super(context, attributeSet, defStyleAttr, defStyleRes);
    }


    @Override
    protected View onCreateView(ViewGroup parent) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return super.onCreateView(parent);
        else {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = layoutInflater.inflate(R.layout.mp_preference, parent, false);

            ViewGroup widgetFrame = (ViewGroup) layout.findViewById(R.id.widget_frame);
            int widgetLayoutResId = getWidgetLayoutResource();
            if (widgetLayoutResId != 0) {
                layoutInflater.inflate(widgetLayoutResId, widgetFrame);
            }
            widgetFrame.setVisibility(widgetLayoutResId != 0 ? View.VISIBLE : View.GONE);

            return layout;
        }
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CharSequence title = getTitle();
            titleView = (TextView) view.findViewById(R.id.title);
            titleView.setText(title);
            titleView.setVisibility(!SubstituteRequest.isEmpty(title.toString()) ? View.VISIBLE : View.GONE);

            CharSequence summary = getSummary();
            summaryView = (TextView) view.findViewById(R.id.summary);
            summaryView.setText(summary);
            summaryView.setVisibility(!SubstituteRequest.isEmpty(summary.toString()) ? View.VISIBLE : View.GONE);

            if (icon == null && iconResId > 0)
                icon = ResourcesCompat.getDrawable(getContext().getResources(), iconResId, getContext().getTheme());

            imageView = (ImageView) view.findViewById(R.id.icon);
            imageView.setImageDrawable(icon);
            imageView.setVisibility(icon != null ? View.VISIBLE : View.GONE);

            imageFrame = view.findViewById(R.id.icon_frame);
            imageFrame.setVisibility(icon != null ? View.VISIBLE : View.GONE);
        }
    }
}
