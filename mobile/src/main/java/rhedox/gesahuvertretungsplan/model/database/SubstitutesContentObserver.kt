package rhedox.gesahuvertretungsplan.model.database

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.util.Log
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime

/**
 * Created by robin on 19.10.2016.
 */
class SubstitutesContentObserver(handler: Handler, private val callback: (date: LocalDate) -> Unit) : ContentObserver(handler) {

    override fun onChange(selfChange: Boolean) {
        onChange(selfChange, null);
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        Log.i("SubstitutesObserver", "onChange: $uri");

        if(uri != null) {
            if(uri.pathSegments.size > 1 && uri.pathSegments[1] == "date" && (uri.pathSegments[0] == SubstitutesContentProvider.substitutesPath || uri.pathSegments[0]== SubstitutesContentProvider.announcementsPath))
                callback.invoke(DateTime(uri.lastPathSegment.toLong()).toLocalDate());
        }
    }
}