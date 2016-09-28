package rhedox.gesahuvertretungsplan.ui.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    private LocalDate date;
	private MainActivity mainActivity;
	private boolean isPickerDone = false;

    public static final String TAG ="DatePickerFragment";
    public static final String KEY_DATE ="date";
    public static final String ARGUMENT_DATE = "ArgumentDate";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    if(savedInstanceState != null && savedInstanceState.containsKey(KEY_DATE))
		    date = new DateTime(savedInstanceState.getLong(KEY_DATE)).toLocalDate();
	    else if(getArguments() != null && getArguments().containsKey(ARGUMENT_DATE))
            date = new DateTime(getArguments().getLong(ARGUMENT_DATE)).toLocalDate();
        else
            date = LocalDate.now();

	    isPickerDone = false;
        return new DatePickerDialog(getActivity(), this, date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

	    if(context instanceof MainActivity)
		    mainActivity = (MainActivity)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

	    mainActivity = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
	    if(date != null)
            outState.putLong(KEY_DATE, date.toDateTimeAtStartOfDay().getMillis());
    }

	@Override
	public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
		if(!isPickerDone) {
			date = new LocalDate(year, month + 1, dayOfMonth);

			if(mainActivity != null)
				mainActivity.showDate(date);

			isPickerDone = true;
		}

		dismiss();
	}

	public static DatePickerFragment newInstance(LocalDate date)
	{
		Bundle bundle = new Bundle();
		bundle.putLong(ARGUMENT_DATE, date.toDateTimeAtCurrentTime().getMillis());

		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(bundle);

		return fragment;
	}
}

