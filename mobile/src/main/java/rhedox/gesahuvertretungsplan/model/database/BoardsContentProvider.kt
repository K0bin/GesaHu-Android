package rhedox.gesahuvertretungsplan.model.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import rhedox.gesahuvertretungsplan.model.database.tables.AnnouncementsContract
import rhedox.gesahuvertretungsplan.model.database.tables.BoardsContract
import rhedox.gesahuvertretungsplan.model.database.tables.SubstitutesContract

/**
 * Created by robin on 30.10.2016.
 */
class BoardsContentProvider : ContentProvider() {
    private lateinit var database: SQLiteOpenHelper

    companion object {
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        const val authority = "rhedox.gesahuvertretungsplan.boardNamess"

        private const val allBoards = 1
        private const val boardById = 2
        private const val marksByBoardName = 3
        private const val lessonssByBoardName = 4
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

        val id = db.insert(BoardsContract.Table.name, null, values)
        val insertUri = Uri.parse("content://$authority/"+id.toString());
        context.contentResolver.notifyChange(insertUri, null, false)
        return insertUri
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val queryBuilder = SQLiteQueryBuilder()
        val uriType = uriMatcher.match(uri)
        checkColumns(projection, uriType)
        when (uriType) {
            allBoards -> queryBuilder.tables = BoardsContract.Table.name;
            boardById -> {
                queryBuilder.tables = BoardsContract.Table.name
                queryBuilder.appendWhere("${BoardsContract.Table.columnId} = ${uri.lastPathSegment}")
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        val db = database.readableDatabase
        val cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        return cursor
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("not implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = database.writableDatabase

        val uriType = uriMatcher.match(uri)
        var rowsDeleted = 0;
        when (uriType) {
            allBoards -> rowsDeleted = db.delete(BoardsContract.Table.name, selection ?: "1", null)
            boardById -> rowsDeleted = db.delete(BoardsContract.Table.name, "${BoardsContract.Table.columnId} = ''", null)
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
                if (!BoardsContract.Table.columns.containsAll(requestedColumns)) {
                    throw IllegalArgumentException("Unknown columns in projection");
                }
            }
        }
    }
}