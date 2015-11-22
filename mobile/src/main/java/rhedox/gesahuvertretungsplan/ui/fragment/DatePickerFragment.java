package rhedox.gesahuvertretungsplan.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class DatePickerFragment extends DialogFragment {
    private DatePickerDialog.OnDateSetListener listener;

    public static final String TAG ="DatePickerFragment";
    public static final String ARGUMENT_DATE = "ArgumentDate";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        LocalDate date;
        if(getArguments() != null && getArguments().containsKey(ARGUMENT_DATE))
            date = new DateTime(getArguments().getLong(ARGUMENT_DATE)).toLocalDate();
        else
            date = LocalDate.now();

        return new DatePickerDialog(getActivity(), listener, date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth());
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    public static DatePickerFragment newInstance(LocalDate date, DatePickerDialog.OnDateSetListener listener)
    {
        Bundle bundle = new Bundle();
        bundle.putLong(ARGUMENT_DATE, date.toDateTimeAtCurrentTime().getMillis());

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(bundle);
        fragment.setListener(listener);

        return fragment;
    }
}

