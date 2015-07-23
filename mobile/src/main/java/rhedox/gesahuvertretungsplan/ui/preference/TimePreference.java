package rhedox.gesahuvertretungsplan.ui.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.LocalTime;

import java.sql.Time;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.net.SubstituteRequest;
import rhedox.gesahuvertretungsplan.util.AlarmReceiver;
import rhedox.gesahuvertretungsplan.util.TextUtils;

public class TimePreference extends DialogPreference {
    private LocalTime time;

    private LocalTime previousTime;

    private TimePicker picker=null;

    //SubLollipop
    private TextView titleView;
    private TextView summaryView;

    private ImageView imageView;
    private View imageFrame;

    private int iconResId;
    private Drawable icon;

    public static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimePreference(Context context) {
        super(context);
        init();
    }
    public TimePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }
    public TimePreference(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init();
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimePreference(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        super(context, attributeSet, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setPositiveButtonText(getContext().getString(R.string.positive_button));
        setNegativeButtonText(getContext().getString(R.string.negative_button));
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(true);

        return picker;
    }

    @Override
    protected void onBindDialogView(@NonNull View v) {
        super.onBindDialogView(v);

        if(picker != null) {
            if(time == null)
                time = LocalTime.now();

            picker.setCurrentHour(time.getHourOfDay());
            picker.setCurrentMinute(time.getMinuteOfHour());
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            time = new LocalTime(picker.getCurrentHour(), picker.getCurrentMinute());

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            boolean notification = prefs.getBoolean("pref_notification", true);

            if(!time.equals(previousTime) && notification) {
                AlarmReceiver.create(this.getContext(), time.getHourOfDay(), time.getMinuteOfHour());
            }

            if (callChangeListener(time)) {
                persistString(time.toString());
            }

            previousTime = time;
        }
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String timeString;

        if (restoreValue) {
            if (defaultValue==null) {
                timeString=getPersistedString("00:00");
            }
            else {
                timeString=getPersistedString(defaultValue.toString());
            }
        }
        else {
            if(defaultValue == null)
                timeString="00:00";
            else
                timeString=defaultValue.toString();
        }

        previousTime = time;
        time = LocalTime.parse(timeString);
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