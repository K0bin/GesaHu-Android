package rhedox.gesahuvertretungsplan.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import rhedox.gesahuvertretungsplan.R;

/**
 * Created by robin on 30.08.2016.
 */
public class WelcomePreferenceFragment extends Fragment {

	public static final String TAG = "WelcomePreferenceFragment";

	private Spinner yearSpinner;
	private Spinner classSpinner;

	private String[] yearValues;
	private String[] classValues;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		yearValues = getResources().getStringArray(R.array.years_array_values);
		classValues = getResources().getStringArray(R.array.classes_array_values);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_welcome_preferences, container, false);
		yearSpinner = (Spinner)view.findViewById(R.id.school_year_spinner);
		classSpinner =  (Spinner)view.findViewById(R.id.school_class_spinner);
		ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.years_array, android.R.layout.simple_spinner_item);
		yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		yearSpinner.setAdapter(yearAdapter);
		ArrayAdapter<CharSequence> classAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.classes_array, android.R.layout.simple_spinner_item);
		classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		classSpinner.setAdapter(classAdapter);
		return view;
	}

	@Override
	public void onDestroyView() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		SharedPreferences.Editor editor = prefs.edit();

		if(yearSpinner != null) {
			int index = yearSpinner.getSelectedItemPosition();
			if(index > 0 && index < yearValues.length) {
				String value = yearValues[index];
				editor.putString(PreferenceFragment.PREF_YEAR, value);
			}
		}
		if(classSpinner != null) {
			int index = classSpinner.getSelectedItemPosition();
			if(index > 0 && index < classValues.length) {
				String value = classValues[index];
				editor.putString(PreferenceFragment.PREF_CLASS, value);
			}
		}
		editor.apply();

		yearSpinner = null;
		classSpinner = null;
		super.onDestroyView();
	}

	public static WelcomePreferenceFragment newInstance() {
		return new WelcomePreferenceFragment();
	}
}
