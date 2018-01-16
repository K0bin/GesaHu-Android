package rhedox.gesahuvertretungsplan.model.database

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.support.annotation.IntDef
import android.support.v4.content.CursorLoader
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.database.tables.*
import rhedox.gesahuvertretungsplan.util.Open
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import java.util.concurrent.Future

/**
 * Created by robin on 29.10.2016.
 */
@Open
class BoardsRepository(context: Context) {
    private val context = context.applicationContext
    private val contentResolver = context.contentResolver

    private val observer: Observer;

    private val futures = mutableMapOf<Int, Future<Unit>>()

    var boardsCallback: ((boards: List<Board>) -> Unit)? = null
    var marksCallback: ((boardId: Long, marks: List<Mark>) -> Unit)? = null
    var lessonsCallback: ((boardId: Long, lessons: List<Lesson>) -> Unit)? = null

    init {
        observer = Observer {
            if(it.pathSegments.size > 0 && it.pathSegments[0] != MarksContract.path && it.pathSegments[0] != LessonsContract.path) {
                //First path segment has to be a board id
                if(it.pathSegments.size > 1) {
                    //There's a second path segment -> a table other than Boards
                    when (it.lastPathSegment) {
                        MarksContract.path -> loadMarks(it.pathSegments[0].toLong())
                        LessonsContract.path -> loadLessons(it.pathSegments[0].toLong())
                    }
                } else {
                    //There's no second path segment but an id -> a board got inserted
                    loadBoards()
                }
            }
        }
        context.contentResolver.registerContentObserver(BoardsContract.uri, true, observer);
    }

    fun destroy() {
        boardsCallback = null;
        context.contentResolver.unregisterContentObserver(observer)

        for ((_, value) in futures) {
            value.cancel(true)
        }
        futures.clear()
    }

    fun loadBoards() {
        load({
            val cursor = contentResolver.query(BoardsContract.uri, BoardsContract.Table.columns.toTypedArray(), null, null, null)
            val boards = BoardsAdapter.boardsFromCursor(cursor)
            cursor.close()
            return@load boards
        }, {
            boardsCallback?.invoke(it)
        })

    }

    fun loadMarks(boardId: Long) {
        load({
            val cursor = contentResolver.query(MarksContract.uriWithBoard(boardId), MarksContract.Table.columns.toTypedArray(), null, null, null)
            val marks = MarksAdapter.marksFromCursor(cursor)
            cursor.close()
            return@load marks;
        }, {
            marksCallback?.invoke(boardId, it)
        })
    }

    fun loadLessons(boardId: Long) {
        load({
            val cursor = contentResolver.query(LessonsContract.uriWithBoard(boardId), LessonsContract.Table.columns.toTypedArray(), null, null, null)
            val lessons = LessonsAdapter.lessonsFromCursor(cursor)
            cursor.close()
            return@load lessons;
        }, {
            lessonsCallback?.invoke(boardId, it)
        })
    }

    private fun <T>load(async: () -> T, uiThread: (data: T) -> Unit) {
        var addedToList = false
        var key = 0
        while (futures[key] != null) {
            key++;
        }

        val future = doAsync {
            val data = async();
            uiThread {
                uiThread(data)
                if(addedToList) {
                    futures.remove(key)
                }
            }
        }
        futures.put(key, future)
        addedToList = true;
    }

    class Observer(private val callback: (uri: Uri) -> Unit): ContentObserver(Handler()) {

        override fun onChange(selfChange: Boolean) {
            onChange(selfChange, null);
        }

        override fun onChange(selfChange: Boolean, uri: Uri?) {
            if(uri != null)
                callback(uri)
        }
    }
}