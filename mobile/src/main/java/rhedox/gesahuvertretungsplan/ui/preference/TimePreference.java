package rhedox.gesahuvertretungsplan.ui.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import org.joda.time.LocalTime;

public class TimePreference extends android.support.v7.preference.DialogPreference {
    private LocalTime time = new LocalTime();

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimePreference(Context context) {
        super(context);
    }

    public void setTime(LocalTime time) {
        final boolean wasBlocking = shouldDisableDependents();

        this.time = time;

        persistLong(time.getMillisOfDay());

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
        LocalTime localTime = null;
        if(defaultValue != null && defaultValue instanceof LocalTime)
            localTime = (LocalTime)defaultValue;

        setTime(restorePersistedValue ? LocalTime.fromMillisOfDay(getPersistedInt(localTime != null? localTime.getMillisOfDay() : 0)) : localTime);
    }


}