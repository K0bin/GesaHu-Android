package rhedox.gesahuvertretungsplan.model.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import rhedox.gesahuvertretungsplan.model.database.tables.*

/**
 * Created by robin on 30.10.2016.
 */
class BoardsContentProvider : ContentProvider() {
    private lateinit var database: SQLiteOpenHelper

    companion object {
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        const val authority = "rhedox.gesahuvertretungsplan.boards"

        private const val boards = 1
        private const val boardById = 2
        private const val marksByBoardId = 6
        private const val marksById = 8
        private const val marks = 10
        private const val lessonsByBoardId = 7
        private const val lessonsById = 9
        private const val lessons = 11

        init {
            uriMatcher.addURI(authority, null, boards)
            uriMatcher.addURI(authority, "#", boardById)

            uriMatcher.addURI(authority, MarksContract.path, marks)
            uriMatcher.addURI(authority, "#/${MarksContract.path}", marksByBoardId)
            uriMatcher.addURI(authority, "${MarksContract.path}/#", marksById)

            uriMatcher.addURI(authority, LessonsContract.path, lessons)
            uriMatcher.addURI(authority, "#/${LessonsContract.path}", lessonsByBoardId)
            uriMatcher.addURI(authority, "${LessonsContract.path}/#", lessonsById)
        }
    }

    override fun onCreate(): Boolean {
        database = SqLiteHelper(context)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if(values == null) {
            return null;
        }

        val uriType = uriMatcher.match(uri);
        val db = database.writableDatabase
        val insertUri: Uri?;
        val boardsUri: Uri?;
        when (uriType) {
            boards -> {
                val id = db.insert(BoardsContract.Table.name, null, values)
                insertUri = BoardsContract.uriWithId(id)
                boardsUri = null
            }
            lessons -> {
                val id = db.insert(LessonsContract.Table.name, null, values)
                insertUri = LessonsContract.uriWithId(id)

                val boardId = values.get(LessonsContract.Table.columnBoardId);
                if(boardId != null) {
                    boardsUri = LessonsContract.uriWithBoard(boardId as Long)
                } else {
                    boardsUri = null
                }
            }
            marks -> {
                val id = db.insert(MarksContract.Table.name, null, values)
                insertUri = MarksContract.uriWithId(id);
                val boardId = values.get(MarksContract.Table.columnBoardId);
                if(boardId != null) {
                    boardsUri = MarksContract.uriWithBoard(boardId as Long)
                } else {
                    boardsUri = null
                }
            }

            else -> {
                insertUri = null;
                boardsUri = null;
            }
        }
        if(insertUri != null) {
            context.contentResolver.notifyChange(insertUri, null, false)
        }
        if(boardsUri != null) {
            context.contentResolver.notifyChange(insertUri, null, false)
        }
        db.close();
        return insertUri
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val uriType = uriMatcher.match(uri)
        checkColumns(projection, uriType)

        val queryBuilder = SQLiteQueryBuilder()
        when (uriType) {
            boards -> queryBuilder.tables = BoardsContract.Table.name;
            boardById -> {
                queryBuilder.tables = BoardsContract.Table.name
                queryBuilder.appendWhere("${BoardsContract.Table.columnId} = ${uri.lastPathSegment}")
            }

            lessons -> queryBuilder.tables = LessonsContract.Table.name
            lessonsByBoardId -> {
                queryBuilder.tables = LessonsContract.Table.name
                queryBuilder.appendWhere("${LessonsContract.Table.columnBoardId} = ${uri.pathSegments.first()}")
            }
            lessonsById -> {
                queryBuilder.tables = LessonsContract.Table.name
                queryBuilder.appendWhere("${LessonsContract.Table.columnId} = ${uri.lastPathSegment}")
            }

            marks -> queryBuilder.tables = MarksContract.Table.name
            marksByBoardId -> {
                queryBuilder.tables = MarksContract.Table.name
                queryBuilder.appendWhere("${MarksContract.Table.columnBoardId} = ${uri.pathSegments.first()}")
            }
            marksById -> {
                queryBuilder.tables = MarksContract.Table.name
                queryBuilder.appendWhere("${MarksContract.Table.columnId} = ${uri.lastPathSegment}")
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
            boards -> rowsDeleted = db.delete(BoardsContract.Table.name, selection ?: "1", null)
            boardById -> rowsDeleted = db.delete(BoardsContract.Table.name, "${BoardsContract.Table.columnId} = '${uri.lastPathSegment}'", null)
            lessons -> rowsDeleted = db.delete(LessonsContract.Table.name, "1", null)
            lessonsById -> rowsDeleted = db.delete(LessonsContract.Table.name, "${LessonsContract.Table.columnId} = '${uri.lastPathSegment}'", null)
            lessonsByBoardId -> rowsDeleted = db.delete(LessonsContract.Table.name, "${LessonsContract.Table.columnBoardId} = '${uri.lastPathSegment}'", null)

            marks -> rowsDeleted = db.delete(MarksContract.Table.name, "1", null)
            marksById -> rowsDeleted = db.delete(MarksContract.Table.name, "${MarksContract.Table.columnId} = '${uri.lastPathSegment}'", null)
            marksByBoardId -> rowsDeleted = db.delete(MarksContract.Table.name, "${MarksContract.Table.columnBoardId} = '${uri.lastPathSegment}'", null)
        }
        db.close()
        return rowsDeleted
    }

    override fun getType(uri: Uri?): String? {
        return null
    }

    private fun checkColumns(projection: Array<String>?, uriType: Int) {
        if(projection != null) {
            val requestedColumns = projection.toSet()
            when (uriType) {
                boards, boardById ->
                    if (!BoardsContract.Table.columns.containsAll(requestedColumns)) {
                        throw IllegalArgumentException("Unknown columns in projection");
                    }

                lessons, lessonsById, lessonsByBoardId ->
                    if (!LessonsContract.Table.columns.containsAll(requestedColumns)) {
                        throw IllegalArgumentException("Unknown columns in projection");
                    }

                marks, marksById, marksByBoardId->
                if (!MarksContract.Table.columns.containsAll(requestedColumns)) {
                    throw IllegalArgumentException("Unknown columns in projection");
                }
            }
        }
    }
}