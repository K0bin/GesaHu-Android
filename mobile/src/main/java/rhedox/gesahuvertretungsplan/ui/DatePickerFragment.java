package rhedox.gesahuvertretungsplan.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import org.joda.time.LocalDate;

public class DatePickerFragment extends DialogFragment {
    private LocalDate date;
    private DatePickerDialog.OnDateSetListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        if(date == null)
            date = LocalDate.now();

        return new DatePickerDialog(getActivity(), listener, date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth());
    }

    public void show(LocalDate date, FragmentManager fragmentManager, String tag, DatePickerDialog.OnDateSetListener listener) {
        this.date = date;
        this.listener = listener;
        show(fragmentManager, tag);
    }

    public static DatePickerFragment newInstance()
    {
        return new DatePickerFragment();
    }
}

