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
import rhedox.gesahuvertretungsplan.model.database.tables.Announcements
import rhedox.gesahuvertretungsplan.model.database.tables.Substitutes

/**
 * Created by robin on 19.10.2016.
 */
class SubstitutesContentProvider : ContentProvider() {

    private lateinit var database: SqLiteHelper;

    companion object {
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        const val authority = "rhedox.gesahuvertretungsplan.substitutes"
        const val substitutesPath = "substitutes";
        const val announcementsPath = "announcements";
        private const val substitutes = 1;
        private const val substitutesByDate = 10;
        private const val substitutesById = 20;
        private const val announcements = 2;
        private const val announcementsById = 15;
        private const val announcementsByDate = 25;

        init {
            uriMatcher.addURI(authority, substitutesPath, substitutes)
            uriMatcher.addURI(authority, "$substitutesPath/date/#", substitutesByDate)
            uriMatcher.addURI(authority, "$substitutesPath/#", substitutesById)
            uriMatcher.addURI(authority, announcementsPath, announcements)
            uriMatcher.addURI(authority, "$announcementsPath/date/#", announcementsByDate)
            uriMatcher.addURI(authority, "$announcementsPath/#", announcementsById)
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val queryBuilder = SQLiteQueryBuilder();

        val uriType = uriMatcher.match(uri);
        checkColumns(projection, uriType);
        when (uriType) {
            substitutes -> queryBuilder.tables = Substitutes.name;
            substitutesByDate -> {
                queryBuilder.tables = Substitutes.name;
                queryBuilder.appendWhere(Substitutes.columnDate + "='" + uri.lastPathSegment + "'")
            }
            substitutesById -> {
                queryBuilder.tables = Substitutes.name;
                queryBuilder.appendWhere(Substitutes.columnId + "='" + uri.lastPathSegment + "'")
            }

            announcements -> queryBuilder.tables = Announcements.name
            announcementsByDate -> {
                queryBuilder.tables = Announcements.name;
                queryBuilder.appendWhere(Announcements.columnDate + "='" + uri.lastPathSegment + "'")
            }
            announcementsById -> {
                queryBuilder.tables = Announcements.name;
                queryBuilder.appendWhere(Announcements.columnId + "='" + uri.lastPathSegment + "'")
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
        var rowsDeleted: Int = 0;
        when (uriType) {
            substitutes -> rowsDeleted = db.delete(Substitutes.name, selection ?: "", null)
            substitutesByDate -> rowsDeleted = db.delete(Substitutes.name, "${Substitutes.columnDate} = '${uri.lastPathSegment}' and ${selection ?: ""}", null)
            substitutesById -> rowsDeleted = db.delete(Substitutes.name, "${Substitutes.columnId} = '${uri.lastPathSegment}'", null)
            announcements -> rowsDeleted = db.delete(Announcements.name, selection ?: "", null)
            announcementsByDate -> rowsDeleted = db.delete(Announcements.name, "${Announcements.columnDate} = '${uri.lastPathSegment}' and ${selection ?: ""}", null)
            announcementsById -> rowsDeleted = db.delete(Announcements.name, "${Announcements.columnId} = '${uri.lastPathSegment}'", null)
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
        var insertUri: Uri;
        var dateUri: Uri;
        when (uriType) {
            substitutes -> {
                val id = db.insert(Substitutes.name, null, values);
                val millis = values?.get(Substitutes.columnDate) ?: 0
                insertUri = Uri.parse("$substitutesPath/"+id.toString());
                dateUri = Uri.parse("$substitutesPath/date/"+millis.toString());
            }
            announcements -> {
                val id = db.insert(Announcements.name, null, values);
                val millis = values?.get(Announcements.columnDate) ?: 0
                insertUri = Uri.parse("$announcementsPath/"+id.toString());
                dateUri = Uri.parse("$announcementsPath/date/"+millis.toString());
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
                    val id = db.insert(Substitutes.name, null, value);

                    //Notify Content Resolver
                    val insertUri = Uri.parse("$substitutesPath/" + id.toString());
                    context.contentResolver.notifyChange(insertUri, null, false);

                    val millis = value.get(Substitutes.columnDate) ?: 0
                    val dateUri = Uri.parse("$substitutesPath/date/" + millis.toString());
                    if(!dateUris.contains(dateUri)) {
                        dateUris.add(dateUri);
                    }
                    changed++;
                }
            }
            announcements ->
                for(value in values) {
                    val id = db.insert(Announcements.name, null, value);

                    //Notify Content Resolver
                    val insertUri = Uri.parse("$announcementsPath/" + id.toString());
                    context.contentResolver.notifyChange(insertUri, null, false);

                    val millis = value.get(Substitutes.columnDate) ?: 0
                    val dateUri = Uri.parse("$announcementsPath/date/" + millis.toString())
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
                if (!Substitutes.availableColumns.containsAll(requestedColumns)) {
                    throw IllegalArgumentException("Unknown columns in projection");
                }
            } else if(uriType == announcements || uriType == announcementsByDate || uriType == announcementsById) {
                if (!Announcements.availableColumns.containsAll(requestedColumns)) {
                    throw IllegalArgumentException("Unknown columns in projection");
                }
            }
        }
    }
}