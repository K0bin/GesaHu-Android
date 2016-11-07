package rhedox.gesahuvertretungsplan.model.database

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import com.pawegio.kandroid.i
import org.joda.time.DateTime
import rhedox.gesahuvertretungsplan.model.database.SqLiteHelper
import rhedox.gesahuvertretungsplan.model.database.tables.AnnouncementAdapter
import rhedox.gesahuvertretungsplan.model.database.tables.AnnouncementsContract
import rhedox.gesahuvertretungsplan.model.database.tables.SubstitutesContract

/**
 * Created by robin on 19.10.2016.
 */
class SubstitutesContentProvider : ContentProvider() {

    private lateinit var database: SqLiteHelper;

    companion object {
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        const val authority = "rhedox.gesahuvertretungsplan.substitutes"
        private const val substitutes = 1;
        private const val substitutesByDate = 10;
        private const val substitutesById = 20;
        private const val announcements = 2;
        private const val announcementsById = 15;
        private const val announcementsByDate = 25;

        init {
            uriMatcher.addURI(authority, SubstitutesContract.path, substitutes)
            uriMatcher.addURI(authority, "${SubstitutesContract.path}/${SubstitutesContract.datePath}/#", substitutesByDate)
            uriMatcher.addURI(authority, "${SubstitutesContract.path}/#", substitutesById)
            uriMatcher.addURI(authority, AnnouncementsContract.path, announcements)
            uriMatcher.addURI(authority, "${AnnouncementsContract.path}/${AnnouncementsContract.datePath}/#", announcementsByDate)
            uriMatcher.addURI(authority, "${AnnouncementsContract.path}/#", announcementsById)
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val queryBuilder = SQLiteQueryBuilder();

        val uriType = uriMatcher.match(uri);
        checkColumns(projection, uriType);
        when (uriType) {
            substitutes -> queryBuilder.tables = SubstitutesContract.name;
            substitutesByDate -> {
                queryBuilder.tables = SubstitutesContract.name;
                queryBuilder.appendWhere("${SubstitutesContract.columnDate} = '${uri.lastPathSegment}'")
            }
            substitutesById -> {
                queryBuilder.tables = SubstitutesContract.name;
                queryBuilder.appendWhere("${SubstitutesContract.columnId} = '${uri.lastPathSegment}'")
            }

            announcements -> queryBuilder.tables = AnnouncementsContract.name
            announcementsByDate -> {
                queryBuilder.tables = AnnouncementsContract.name;
                queryBuilder.appendWhere("${AnnouncementsContract.columnDate} = '${uri.lastPathSegment}'")
            }
            announcementsById -> {
                queryBuilder.tables = AnnouncementsContract.name;
                queryBuilder.appendWhere("${AnnouncementsContract.columnId} = '${uri.lastPathSegment}'")
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri");
        }

        val db = database.readableDatabase;
        val cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        return cursor;
    }

    override fun onCreate(): Boolean {
        database = SqLiteHelper(context)
        return true;
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        throw NotImplementedError("Update is not supported")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = database.writableDatabase;

        val uriType = uriMatcher.match(uri);
        var rowsDeleted = 0;
        when (uriType) {
            substitutes -> rowsDeleted = db.delete(SubstitutesContract.name, selection ?: "1", null)
            substitutesByDate -> rowsDeleted = db.delete(SubstitutesContract.name, "${SubstitutesContract.columnDate} = '${uri.lastPathSegment}' and ${selection ?: ""}", null)
            substitutesById -> rowsDeleted = db.delete(SubstitutesContract.name, "${SubstitutesContract.columnId} = '${uri.lastPathSegment}'", null)
            announcements -> rowsDeleted = db.delete(AnnouncementsContract.name, selection ?: "", null)
            announcementsByDate -> rowsDeleted = db.delete(AnnouncementsContract.name, "${AnnouncementsContract.columnDate} = '${uri.lastPathSegment}' and ${selection ?: ""}", null)
            announcementsById -> rowsDeleted = db.delete(AnnouncementsContract.name, "${AnnouncementsContract.columnId} = '${uri.lastPathSegment}'", null)
            else -> throw IllegalArgumentException("Unknown URI: $uri");
        }
        return rowsDeleted;
    }

    override fun getType(uri: Uri): String? {
        return null;
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = uriMatcher.match(uri);

        val db = database.writableDatabase;
        val insertUri: Uri;
        val dateUri: Uri;
        when (uriType) {
            substitutes -> {
                val id = db.insert(SubstitutesContract.name, null, values);
                val seconds = values?.getAsInteger(SubstitutesContract.columnDate) ?: 0
                insertUri = SubstitutesContract.uriWithId(id)
                dateUri = SubstitutesContract.uriWithSeconds(seconds);
            }
            announcements -> {
                val id = db.insert(AnnouncementsContract.name, null, values);
                val seconds = values?.getAsInteger(AnnouncementsContract.columnDate) ?: 0
                insertUri = AnnouncementsContract.uriWithId(id)
                dateUri = AnnouncementsContract.uriWithSeconds(seconds);
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri");
        }
        context.contentResolver.notifyChange(insertUri, null, false);
        context.contentResolver.notifyChange(dateUri, null, false);
        return insertUri;
    }

    override fun bulkInsert(uri: Uri?, values: Array<ContentValues>?): Int {
        if(values == null || values.size == 0)
            return 0;

        val uriType = uriMatcher.match(uri);

        val db = database.writableDatabase;
        val dateUris = mutableListOf<Uri>();
        var changed = 0;

        when (uriType) {
            substitutes -> {
                for(value in values) {
                    val id = db.insert(SubstitutesContract.name, null, value);

                    //Notify Content Resolver
                    val insertUri = Uri.parse("content://$authority/${SubstitutesContract.path}/" + id.toString());
                    context.contentResolver.notifyChange(insertUri, null, false);

                    val seconds = value.getAsInteger(SubstitutesContract.columnDate) ?: 0
                    val dateUri = SubstitutesContract.uriWithSeconds(seconds)
                    if(!dateUris.contains(dateUri)) {
                        dateUris.add(dateUri);
                    }
                    changed++;
                }
            }
            announcements ->
                for(value in values) {
                    val id = db.insert(AnnouncementsContract.name, null, value);

                    //Notify Content Resolver
                    val insertUri = Uri.parse("content://$authority/${AnnouncementsContract.path}/" + id.toString());
                    context.contentResolver.notifyChange(insertUri, null, false);

                    val seconds = value.getAsInteger(AnnouncementsContract.columnDate) ?: 0
                    val dateUri = AnnouncementsContract.uriWithSeconds(seconds)
                    if (!dateUris.contains(dateUri)) {
                        dateUris.add(dateUri);
                    }
                    changed++;
                }

            else -> throw IllegalArgumentException("Unknown URI: $uri");
        }
        for(dateUri in dateUris)
            context.contentResolver.notifyChange(dateUri, null, false);

        return changed;
    }

    private fun checkColumns(projection: Array<String>?, uriType: Int) {
        if(projection != null) {
            val requestedColumns = projection.toSet()
            if(uriType == substitutes || uriType == substitutesByDate || uriType == substitutesById) {
                if (!SubstitutesContract.columns.containsAll(requestedColumns)) {
                    throw IllegalArgumentException("Unknown columns in projection");
                }
            } else if(uriType == announcements || uriType == announcementsByDate || uriType == announcementsById) {
                if (!AnnouncementsContract.columns.containsAll(requestedColumns)) {
                    throw IllegalArgumentException("Unknown columns in projection");
                }
            }
        }
    }
}