package rhedox.gesahuvertretungsplan.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;

/**
 * Created by Robin on 09.10.2015.
 */
public final class SubstituteShareHelper {
    public static final int REQUEST_CODE = 2;

    private SubstituteShareHelper() {}

    public static Intent makeShareIntent(@NonNull Context context, @Nullable LocalDate date, @NonNull Substitute substitute) {
        String text = SubstituteFormatter.makeShareText(context, date, substitute);
        
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, text);
        share.setType("text/plain");
        return Intent.createChooser(share, context.getString(R.string.share));
    }

    public static PendingIntent makePendingShareIntent(Context context, @Nullable LocalDate date, Substitute substitute) {
        return PendingIntent.getActivity(context.getApplicationContext(), REQUEST_CODE, makeShareIntent(context, date, substitute), PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
