package rhedox.gesahuvertretungsplan;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment {
    private Date date;
    private boolean picked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if(!picked) {
                    MainActivity.MainFragment activity = (MainActivity.MainFragment)getParentFragment();
                    Date date = SchoolWeek.nextDay(Date.fromJavaDate(dayOfMonth, monthOfYear, year));
                    activity.load(date);
                    picked=true;
                }
            }
        }, date.getYear(), date.getJavaMonth(), date.getDay());
    }

    public void show(Date date, FragmentManager fragmentManager, String tag) {
        picked = false;
        this.date = date;
        show(fragmentManager, tag);
    }

    public static Fragment newInstance()
    {
        return new DatePickerFragment();
    }
}

