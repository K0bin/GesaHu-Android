package rhedox.gesahuvertretungsplan.model.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import rhedox.gesahuvertretungsplan.broadcastReceiver.SubstitutesWidgetProvider
import rhedox.gesahuvertretungsplan.model.database.tables.AnnouncementsContract
import rhedox.gesahuvertretungsplan.model.database.tables.SubstitutesContract

/**
 * Created by robin on 19.10.2016.
 */
class SubstitutesContentProvider : ContentProvider() {

    private lateinit var database: SubstitutesOpenHelper;

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
        val uriType = uriMatcher.match(uri);
        checkColumns(projection, uriType);

        val queryBuilder = SQLiteQueryBuilder();
        when (uriType) {
            substitutes -> queryBuilder.tables = SubstitutesContract.Table.name;
            substitutesByDate -> {
                queryBuilder.tables = SubstitutesContract.Table.name;
                queryBuilder.appendWhere("${SubstitutesContract.Table.columnDate} = '${uri.lastPathSegment}'")
            }
            substitutesById -> {
                queryBuilder.tables = SubstitutesContract.Table.name;
                queryBuilder.appendWhere("${SubstitutesContract.Table.columnId} = '${uri.lastPathSegment}'")
            }

            announcements -> queryBuilder.tables = AnnouncementsContract.Table.name
            announcementsByDate -> {
                queryBuilder.tables = AnnouncementsContract.Table.name;
                queryBuilder.appendWhere("${AnnouncementsContract.Table.columnDate} = '${uri.lastPathSegment}'")
            }
            announcementsById -> {
                queryBuilder.tables = AnnouncementsContract.Table.name;
                queryBuilder.appendWhere("${AnnouncementsContract.Table.columnId} = '${uri.lastPathSegment}'")
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri");
        }

        val db = database.readableDatabase;
        val cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        return cursor;
    }

    override fun onCreate(): Boolean {
        database = SubstitutesOpenHelper(context)
        return true;
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        throw NotImplementedError("Update is not supported")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val uriType = uriMatcher.match(uri);
        val db = database.writableDatabase;

        var rowsDeleted = 0;
        when (uriType) {
            substitutes -> rowsDeleted = db.delete(SubstitutesContract.Table.name, selection ?: "1", null)
            substitutesByDate -> rowsDeleted = db.delete(SubstitutesContract.Table.name, "${SubstitutesContract.Table.columnDate} = '${uri.lastPathSegment}' and ${selection ?: ""}", null)
            substitutesById -> rowsDeleted = db.delete(SubstitutesContract.Table.name, "${SubstitutesContract.Table.columnId} = '${uri.lastPathSegment}'", null)
            announcements -> rowsDeleted = db.delete(AnnouncementsContract.Table.name, selection ?: "", null)
            announcementsByDate -> rowsDeleted = db.delete(AnnouncementsContract.Table.name, "${AnnouncementsContract.Table.columnDate} = '${uri.lastPathSegment}' and ${selection ?: ""}", null)
            announcementsById -> rowsDeleted = db.delete(AnnouncementsContract.Table.name, "${AnnouncementsContract.Table.columnId} = '${uri.lastPathSegment}'", null)
            else -> throw IllegalArgumentException("Unknown URI: $uri");
        }

        val intent = SubstitutesWidgetProvider.getRefreshBroadcastIntent(context)
        context.sendBroadcast(intent)
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
                val id = db.insert(SubstitutesContract.Table.name, null, values);
                val seconds = values?.getAsInteger(SubstitutesContract.Table.columnDate) ?: 0
                insertUri = SubstitutesContract.uriWithId(id)
                dateUri = SubstitutesContract.uriWithSeconds(seconds);
            }
            announcements -> {
                val id = db.insert(AnnouncementsContract.Table.name, null, values);
                val seconds = values?.getAsInteger(AnnouncementsContract.Table.columnDate) ?: 0
                insertUri = AnnouncementsContract.uriWithId(id)
                dateUri = AnnouncementsContract.uriWithSeconds(seconds);
            }

            else -> {
                throw IllegalArgumentException("Unknown URI: $uri");
            }
        }
        context.contentResolver.notifyChange(insertUri, null, false);
        context.contentResolver.notifyChange(dateUri, null, false);

        val intent = SubstitutesWidgetProvider.getRefreshBroadcastIntent(context)
        context.sendBroadcast(intent)
        return insertUri;
    }

    override fun bulkInsert(uri: Uri?, values: Array<ContentValues>?): Int {
        if(values == null || values.isEmpty())
            return 0;

        val uriType = uriMatcher.match(uri);

        val db = database.writableDatabase;
        db.beginTransaction()
        val dateUris = mutableListOf<Uri>();
        var changed = 0;

        when (uriType) {
            substitutes -> {
                for(value in values) {
                    val id = db.insert(SubstitutesContract.Table.name, null, value);

                    //Notify Content Resolver
                    val insertUri = Uri.parse("content://$authority/${SubstitutesContract.path}/" + id.toString());
                    context.contentResolver.notifyChange(insertUri, null, false);

                    val seconds = value.getAsInteger(SubstitutesContract.Table.columnDate) ?: 0
                    val dateUri = SubstitutesContract.uriWithSeconds(seconds)
                    if(!dateUris.contains(dateUri)) {
                        dateUris.add(dateUri);
                    }
                    changed++;
                }
            }
            announcements ->
                for(value in values) {
                    val id = db.insert(AnnouncementsContract.Table.name, null, value);

                    //Notify Content Resolver
                    val insertUri = Uri.parse("content://$authority/${AnnouncementsContract.path}/" + id.toString());
                    context.contentResolver.notifyChange(insertUri, null, false);

                    val seconds = value.getAsInteger(AnnouncementsContract.Table.columnDate) ?: 0
                    val dateUri = AnnouncementsContract.uriWithSeconds(seconds)
                    if (!dateUris.contains(dateUri)) {
                        dateUris.add(dateUri);
                    }
                    changed++;
                }

            else -> {
                throw IllegalArgumentException("Unknown URI: $uri");
            }
        }
        db.setTransactionSuccessful()
        db.endTransaction()

        for(dateUri in dateUris)
            context.contentResolver.notifyChange(dateUri, null, false);

        val intent = SubstitutesWidgetProvider.getRefreshBroadcastIntent(context)
        context.sendBroadcast(intent)
        return changed;
    }

    private fun checkColumns(projection: Array<String>?, uriType: Int) {
        if(projection != null) {
            val requestedColumns = projection.toSet()
            if(uriType == substitutes || uriType == substitutesByDate || uriType == substitutesById) {
                if (!SubstitutesContract.Table.columns.containsAll(requestedColumns)) {
                    throw IllegalArgumentException("Unknown columns in projection");
                }
            } else if(uriType == announcements || uriType == announcementsByDate || uriType == announcementsById) {
                if (!AnnouncementsContract.Table.columns.containsAll(requestedColumns)) {
                    throw IllegalArgumentException("Unknown columns in projection");
                }
            }
        }
    }
}