package rhedox.gesahuvertretungsplan.model.database

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.util.Log
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.SubstitutesList
import rhedox.gesahuvertretungsplan.model.database.tables.AnnouncementAdapter
import rhedox.gesahuvertretungsplan.model.database.tables.Announcements
import rhedox.gesahuvertretungsplan.model.database.tables.SubstituteAdapter
import rhedox.gesahuvertretungsplan.model.database.tables.Substitutes
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesAdapter

/**
 * Created by robin on 19.10.2016.
 */

class SubstitutesLoaderHelper(private val loaderManager: LoaderManager, private val context: Context, private val date: LocalDate) : LoaderManager.LoaderCallbacks<Cursor> {
    companion object {
        const val substitutesType = 1;
        const val announcementType = 2;
    }

    private var isLoadingSubstitutes = false;
    private var isLoadingAnnouncement = false;
    private val offset = (date.toDateTime(LocalTime()).millis - DateTime(2010,1,1,0,0).millis).toInt()

    var substitutesCallback: ((date: LocalDate, substitutes: List<Substitute>) -> Unit)? = null;
    var announcementCallback: ((date: LocalDate, text: String) -> Unit)? = null;

    fun loadSubstitutes() {
        if(!isLoadingSubstitutes) {
            Log.d("LoaderHelper", "Loading substitutes for $date");
            loaderManager.initLoader(offset + substitutesType, Bundle.EMPTY, this)
        } else {
            Log.d("LoaderHelper", "Substitutes for $date are already loading");
            loaderManager.restartLoader(offset + substitutesType, Bundle.EMPTY, this)
        }
        isLoadingSubstitutes = true;
    }

    fun loadAnnouncement() {
        if(!isLoadingAnnouncement) {
            Log.d("LoaderHelper", "Loading announcement for $date");
            loaderManager.initLoader(offset + announcementType, Bundle.EMPTY, this)
        } else {
            Log.d("LoaderHelper", "Announcement for $date is already loading");
            loaderManager.restartLoader(offset + announcementType, Bundle.EMPTY, this)
        }
        isLoadingAnnouncement = true;
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor>? {
        Log.d("LoaderHelper", "$date loaded, id: $id");

        when (id) {
            offset + substitutesType -> {
                val substitutesUri = Uri.Builder()
                        .scheme("content")
                        .authority(SubstitutesContentProvider.authority)
                        .path(SubstitutesContentProvider.substitutesPath)
                        .appendPath("date")
                        .appendPath(date.toDateTime(LocalTime(0)).millis.toString())
                        .build()

                return CursorLoader(context, substitutesUri, Substitutes.availableColumns.toTypedArray(), null, null, "${Substitutes.columnLessonBegin} ASC");
            }
            offset + announcementType -> {
                val announcementsUri = Uri.Builder()
                        .scheme("content")
                        .authority(SubstitutesContentProvider.authority)
                        .path(SubstitutesContentProvider.announcementsPath)
                        .appendPath("date")
                        .appendPath(date.toDateTime(LocalTime(0)).millis.toString())
                        .build()

                return CursorLoader(context, announcementsUri, Announcements.availableColumns.toTypedArray(), null, null, null);
            }
            else -> return null;
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if(data == null)
            return

        when (loader.id) {
            offset + substitutesType -> {
                substitutesCallback?.invoke(date, SubstituteAdapter.listFromCursor(data))
                isLoadingSubstitutes = false;
            }
            offset + announcementType -> {
                announcementCallback?.invoke(date, AnnouncementAdapter.fromCursor(data))
                isLoadingAnnouncement = false;
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        isLoadingSubstitutes = false;
        isLoadingAnnouncement = false;
    }
}