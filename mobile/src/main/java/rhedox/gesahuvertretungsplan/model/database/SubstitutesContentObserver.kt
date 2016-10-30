package rhedox.gesahuvertretungsplan.model.database

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.support.annotation.IntDef
import android.util.Log
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime

/**
 * Created by robin on 19.10.2016.
 */
class SubstitutesContentObserver(handler: Handler, private val callback: (date: LocalDate, kind: Long) -> Unit) : ContentObserver(handler) {

    companion object {

        //#enumsmatter
        const val TABLE_SUBSTITUTES = 0L
        const val TABLE_ANNOUNCEMENTS = 1L
    }

    @IntDef(TABLE_SUBSTITUTES, TABLE_ANNOUNCEMENTS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ChangedTable

    override fun onChange(selfChange: Boolean) {
        onChange(selfChange, null);
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        Log.i("SubstitutesObserver", "onChange: $uri");

        if(uri != null) {
            if(uri.pathSegments.size > 1 && uri.pathSegments[1] == "date") {
                if(uri.pathSegments[0] == SubstitutesContentProvider.substitutesPath)
                    callback.invoke(DateTime(uri.lastPathSegment.toLong()).toLocalDate(), TABLE_SUBSTITUTES);
                else if(uri.pathSegments[0]== SubstitutesContentProvider.announcementsPath)
                    callback.invoke(DateTime(uri.lastPathSegment.toLong()).toLocalDate(), TABLE_ANNOUNCEMENTS);
            }
        }
    }
}