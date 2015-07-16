package rhedox.gesahuvertretungsplan.ui.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import org.joda.time.LocalTime;

import java.sql.Time;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.util.AlarmReceiver;

public class TimePreference extends DialogPreference {
    private LocalTime time;

    private LocalTime previousTime;

    private TimePicker picker=null;

    public static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

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
}