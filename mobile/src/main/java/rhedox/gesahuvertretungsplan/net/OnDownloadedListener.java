package rhedox.gesahuvertretungsplan.net;

import java.util.List;

import rhedox.gesahuvertretungsplan.model.Substitute;

/**
 * Created by Robin on 02.03.2015.
 */
public interface OnDownloadedListener {
    void onDownloaded(List<Substitute> substitutes);
    void onDownloadFailed(int error);
}
