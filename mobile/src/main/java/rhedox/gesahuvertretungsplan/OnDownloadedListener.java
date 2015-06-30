package rhedox.gesahuvertretungsplan;

import android.content.Context;

import java.util.List;

/**
 * Created by Robin on 02.03.2015.
 */
public interface OnDownloadedListener {
    void onDownloaded(List<Replacement> replacements);
    void onDownloadFailed(int error);
}
