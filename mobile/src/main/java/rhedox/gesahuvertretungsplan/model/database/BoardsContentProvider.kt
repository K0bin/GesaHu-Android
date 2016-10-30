package rhedox.gesahuvertretungsplan.model.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import rhedox.gesahuvertretungsplan.model.database.tables.Announcements
import rhedox.gesahuvertretungsplan.model.database.tables.Boards
import rhedox.gesahuvertretungsplan.model.database.tables.Substitutes

/**
 * Created by robin on 30.10.2016.
 */
class BoardsContentProvider : ContentProvider() {
    private lateinit var database: SQLiteOpenHelper

    companion object {
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        const val authority = "rhedox.gesahuvertretungsplan.boards"

        private const val allBoards = 1
        private const val boardById = 2
        init {
            uriMatcher.addURI(authority, null, allBoards)
            uriMatcher.addURI(authority, "#", boardById)
        }
    }

    override fun onCreate(): Boolean {
        database = SqLiteHelper(context)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = uriMatcher.match(uri);

        val db = database.writableDatabase
        if(uriType != allBoards) {
            throw IllegalArgumentException("Unknown URI: $uri")
        }

        val id = db.insert(Boards.name, null, values)
        val insertUri = Uri.parse("content://$authority/"+id.toString());
        context.contentResolver.notifyChange(insertUri, null, false)
        return insertUri
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val queryBuilder = SQLiteQueryBuilder()
        val uriType = uriMatcher.match(uri)
        checkColumns(projection, uriType)
        when (uriType) {
            allBoards -> queryBuilder.tables = Boards.name;
            boardById -> {
                queryBuilder.tables = Boards.name
                queryBuilder.appendWhere("${Boards.columnId} = ${uri.lastPathSegment}")
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        val db = database.readableDatabase
        val cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        return cursor
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = database.writableDatabase

        val uriType = uriMatcher.match(uri)
        var rowsDeleted = 0;
        when (uriType) {
            allBoards -> rowsDeleted = db.delete(Boards.name, selection ?: "1", null)
            boardById -> rowsDeleted = db.delete(Boards.name, "${Boards.columnId} = ''", null)
        }
        return rowsDeleted
    }

    override fun getType(uri: Uri?): String? {
        return null
    }

    private fun checkColumns(projection: Array<String>?, uriType: Int) {
        if(projection != null) {
            val requestedColumns = projection.toSet()
            if(uriType == allBoards || uriType == boardById) {
                if (!Boards.availableColumns.containsAll(requestedColumns)) {
                    throw IllegalArgumentException("Unknown columns in projection");
                }
            }
        }
    }
}