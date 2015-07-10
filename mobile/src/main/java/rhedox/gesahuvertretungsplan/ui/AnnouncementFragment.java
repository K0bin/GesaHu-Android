package rhedox.gesahuvertretungsplan.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

/**
 * Created by Robin on 10.07.2015.
 */
public class AnnouncementFragment extends DialogFragment {
    public static final String TAG ="AnnouncementDialogFragment";
    public static final String ARGUMENTS_ANNOUNCEMENT ="Argument_Announcement";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String announcement;
        if(getArguments() != null && (announcement = getArguments().getString(ARGUMENTS_ANNOUNCEMENT)) != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Ankündigung");
            builder.setMessage(announcement);

            return builder.create();
        } else
            return super.onCreateDialog(savedInstanceState);
    }

    public static AnnouncementFragment newInstance(String announcement)
    {
        Bundle args = new Bundle();
        args.putString(AnnouncementFragment.ARGUMENTS_ANNOUNCEMENT, announcement);

        AnnouncementFragment fragment = new AnnouncementFragment();
        fragment.setArguments(args);

        return fragment;
    }
}
