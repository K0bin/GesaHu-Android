package rhedox.gesahuvertretungsplan.ui.preference;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.Interval;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.net.SubstituteRequest;
import rhedox.gesahuvertretungsplan.util.AlarmReceiver;
import rhedox.gesahuvertretungsplan.util.BootReceiver;
import rhedox.gesahuvertretungsplan.util.NotificationJob;
import rhedox.gesahuvertretungsplan.util.TextUtils;

/**
 * Created by Robin on 15.11.2014.
 */
public class NotificationPreference extends CheckBoxPreference {
    public static final String PREF_NOTIFICATION_TIME="pref_notification_time";
    public static final String PREF_NOTIFICATION="pref_notification";

    //SubLollipop
    private TextView titleView;
    private TextView summaryView;

    private ImageView imageView;
    private View imageFrame;

    private int iconResId;
    private Drawable icon;

    public NotificationPreference(Context context) {
        super(context);
    }
    public NotificationPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
    public NotificationPreference(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NotificationPreference(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        super(context, attributeSet, defStyleAttr, defStyleRes);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onClick() {
        super.onClick();

        if(isChecked()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String alarmString = prefs.getString(NotificationPreference.PREF_NOTIFICATION_TIME, "00:00");
            int alarmHour = TimePreference.getHour(alarmString);
            int alarmMinute = TimePreference.getMinute(alarmString);
            AlarmReceiver.create(getContext(), alarmHour, alarmMinute);
            BootReceiver.create(getContext());
        } else {
            AlarmReceiver.cancel(getContext());
            BootReceiver.cancel(getContext());
        }
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
            titleView.setVisibility(!TextUtils.isEmpty(title.toString()) ? View.VISIBLE : View.GONE);

            CharSequence summary = getSummary();
            summaryView = (TextView) view.findViewById(R.id.summary);
            summaryView.setText(summary);
            summaryView.setVisibility(!TextUtils.isEmpty(summary.toString()) ? View.VISIBLE : View.GONE);

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
