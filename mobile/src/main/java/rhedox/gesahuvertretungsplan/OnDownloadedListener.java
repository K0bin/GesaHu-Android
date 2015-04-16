package rhedox.gesahuvertretungsplan;

import android.content.Context;

import java.util.List;

/**
 * Created by Robin on 02.03.2015.
 */
public interface OnDownloadedListener {
    public void onDownloaded(Context context, List<Replacement> replacements);
}
