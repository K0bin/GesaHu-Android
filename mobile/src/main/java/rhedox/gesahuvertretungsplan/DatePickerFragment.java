package rhedox.gesahuvertretungsplan;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public int Day, Month, Year;
    public boolean Picked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), this, Year, Month-1, Day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(!Picked) {
            MainActivity activity = (MainActivity)getActivity();
            Calendar calendar = SchoolWeek.next(dayOfMonth, monthOfYear+1, year);
            dayOfMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
            monthOfYear = calendar.get(GregorianCalendar.MONTH)+1;
            year = calendar.get(GregorianCalendar.YEAR);
            activity.load(dayOfMonth,monthOfYear,year);
            Picked=true;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}

