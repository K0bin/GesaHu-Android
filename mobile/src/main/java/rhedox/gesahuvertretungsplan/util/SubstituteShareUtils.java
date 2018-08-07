package rhedox.gesahuvertretungsplan.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.joda.time.LocalDate;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.SubstituteFormatter;
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute;

/**
 * Created by Robin on 09.10.2015.
 */
public final class SubstituteShareUtils {
    public static final int REQUEST_CODE = 2;

    private SubstituteShareUtils() {}

    public static Intent makeShareIntent(@NonNull Context context, @Nullable LocalDate date, @NonNull Substitute substitute) {
        final SubstituteFormatter formatter = new SubstituteFormatter(context);

        String text = formatter.makeShareText(date, substitute);
        
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
