package rhedox.gesahuvertretungsplan.ui.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import org.joda.time.LocalTime;

public class TimePreference extends android.support.v7.preference.DialogPreference {
    private LocalTime time;

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        time = new LocalTime();
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        time = new LocalTime();
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        time = new LocalTime();
    }

    public TimePreference(Context context) {
        super(context);
        time = new LocalTime();
    }

    public void setTime(LocalTime time) {
        final boolean wasBlocking = shouldDisableDependents();

        this.time = time;

        persistInt(time.getMillisOfDay());

        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking)
            notifyDependencyChange(isBlocking);
    }

    public LocalTime getTime() {
        return time;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return LocalTime.fromMillisOfDay(a.getInt(index, 0));
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setTime(restorePersistedValue ? LocalTime.fromMillisOfDay(getPersistedInt(time.getMillisOfDay())) : (LocalTime) defaultValue);
    }


}