package rhedox.gesahuvertretungsplan.ui.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.lang.ref.WeakReference;

import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;

public class DatePickerFragment extends DialogFragment {
    private DatePickerDialog.OnDateSetListener listener;

    public static final String TAG ="DatePickerFragment";
    public static final String ARGUMENT_DATE = "ArgumentDate";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LocalDate date;
        if(getArguments() != null && getArguments().containsKey(ARGUMENT_DATE))
            date = new DateTime(getArguments().getLong(ARGUMENT_DATE)).toLocalDate();
        else
            date = LocalDate.now();

        listener = new Listener(getActivity());

        return new DatePickerDialog(getActivity(), listener, date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth());
    }

    public static DatePickerFragment newInstance(LocalDate date)
    {
        Bundle bundle = new Bundle();
        bundle.putLong(ARGUMENT_DATE, date.toDateTimeAtCurrentTime().getMillis());

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public static class Listener implements DatePickerDialog.OnDateSetListener {
        private boolean isPickerDone;
        private WeakReference<MainActivity> activity;

        public Listener(Activity activity) {
            if(activity instanceof MainActivity)
                this.activity = new WeakReference<MainActivity>((MainActivity)activity);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if(!isPickerDone) {
                LocalDate _date = SchoolWeek.nextDate(new LocalDate(year, monthOfYear + 1, dayOfMonth));
                if(activity != null && activity.get() != null)
                    activity.get().showDate(_date);

                isPickerDone = true;
            }
        }
    }
}

