package rhedox.gesahuvertretungsplan.ui.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.media.tv.TvView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.EditTextPreferenceDialogFragmentCompat;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.widget.TimePicker;

import org.joda.time.LocalTime;

import rhedox.gesahuvertretungsplan.ui.preference.TimePreference;

/**
 * Created by Robin on 14.09.2015.
 */
public class TimePreferenceDialogFragment extends PreferenceDialogFragmentCompat implements TimePickerDialog.OnTimeSetListener {
    //Double set workaround
    private boolean isAlreadyPicked = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        isAlreadyPicked = false;
        LocalTime time = getTimePreference().getTime();

        return new TimePickerDialog(getContext(), this, time.getHourOfDay(), time.getMinuteOfHour(), true);
    }

    @Override
    public void onDialogClosed(boolean b) {
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        if(!isAlreadyPicked) {
            TimePreference preference = getTimePreference();

            LocalTime value = new LocalTime(i, i1);

            if (preference.callChangeListener(value))
                preference.setTime(value);

            this.dismiss();
        }
        isAlreadyPicked = true;
    }

    private TimePreference getTimePreference() {
        return (TimePreference) getPreference();
    }

    public static TimePreferenceDialogFragment newInstance(String key) {
        final TimePreferenceDialogFragment fragment = new TimePreferenceDialogFragment();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }


}
