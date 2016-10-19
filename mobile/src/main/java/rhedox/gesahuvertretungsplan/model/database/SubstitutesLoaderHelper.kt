package rhedox.gesahuvertretungsplan.model.database

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
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

class SubstitutesLoaderHelper(loaderManager: LoaderManager, private val context: Context, private val date: LocalDate, private val callback: Callback) : LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        const val substitutesType = 1;
        const val announcementType = 2;
    }

    private var substitutes = listOf<Substitute>()
    private var announcement = "";
    private var areSubstitutesLoaded = false;
    private var isAnnouncementLoaded = false;

    init {
        loaderManager.initLoader(substitutesType, Bundle.EMPTY, this)
        loaderManager.initLoader(announcementType, Bundle.EMPTY, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor>? {

        when (id) {
            substitutesType -> {
                val substitutesUri = Uri.Builder()
                        .scheme("content")
                        .authority(SubstitutesContentProvider.authority)
                        .path(SubstitutesContentProvider.substitutesPath)
                        .appendPath("date")
                        .appendPath(date.toDateTime(LocalTime(0)).millis.toString())
                        .build()

                areSubstitutesLoaded = false

                return CursorLoader(context, substitutesUri, Substitutes.availableColumns.toTypedArray(), null, null, null);
            }
            announcementType -> {
                val announcementsUri = Uri.Builder()
                        .scheme("content")
                        .authority(SubstitutesContentProvider.authority)
                        .path(SubstitutesContentProvider.announcementsPath)
                        .appendPath("date")
                        .appendPath(date.toDateTime(LocalTime(0)).millis.toString())
                        .build()

                isAnnouncementLoaded = false

                return CursorLoader(context, announcementsUri, Announcements.availableColumns.toTypedArray(), null, null, null);
            }
            else -> return null;
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if(data == null)
            return

        when (loader.id) {
            substitutesType -> {
                substitutes = SubstituteAdapter.listFromCursor(data)
                areSubstitutesLoaded = true;
            }
            announcementType -> {
                announcement = AnnouncementAdapter.fromCursor(data)
                isAnnouncementLoaded = true
            }
        }

        if(areSubstitutesLoaded && isAnnouncementLoaded) {
            callback.onSubstitutesLoaded(SubstitutesList(announcement, substitutes, date));
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        loader?.reset();
    }

    interface Callback {
        fun onSubstitutesLoaded(substitutesList: SubstitutesList)
    }

}