package rhedox.gesahuvertretungsplan.model.database

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import org.joda.time.LocalDate
import org.joda.time.LocalTime

/**
 * Created by robin on 19.10.2016.
 */
class SubstitutesContentObserver(handler: Handler, date: LocalDate, private val helper: SubstitutesLoaderHelper) : ContentObserver(handler) {
    private val dateMillis = date.toDateTime(LocalTime(0)).millis;

    override fun onChange(selfChange: Boolean) {
        onChange(selfChange, null);
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        if(uri != null) {
            if(uri.pathSegments.size > 0 && (uri.pathSegments[0]== SubstitutesContentProvider.substitutesPath || uri.pathSegments[0]== SubstitutesContentProvider.announcementsPath))
                helper.load()
        }
    }
}