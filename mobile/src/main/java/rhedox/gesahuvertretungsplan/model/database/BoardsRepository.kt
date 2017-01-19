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
import rhedox.gesahuvertretungsplan.model.database.tables.BoardAdapter
import rhedox.gesahuvertretungsplan.model.database.tables.BoardsContract
import rhedox.gesahuvertretungsplan.util.Open
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

    private var subscribedToBoards = false
    var boardsCallback: ((boards: List<Board>) -> Unit)? = null
        get() = field
        set(value) {
            if(value != null && !subscribedToBoards) {
                context.contentResolver.registerContentObserver(BoardsContract.uri, true, observer);
            }
            field = value
        }

    init {
        observer = Observer {
            loadBoards()
        }
    }

    fun destroy() {
        boardsCallback = null;
        context.contentResolver.unregisterContentObserver(observer)

        for ((key, value) in futures) {
            value.cancel(true)
        }
        futures.clear()
    }

    fun loadBoards() {
        var addedToList = false
        var key = 0
        while (futures[key] != null) {
            key++;
        }

        val future = doAsync {
            val cursor = contentResolver.query(BoardsContract.uri, BoardsContract.Table.columns.toTypedArray(), null, null, null)
            val boards = BoardAdapter.boardsFromCursor(cursor)
            cursor.close()

            uiThread {
                boardsCallback?.invoke(boards)
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