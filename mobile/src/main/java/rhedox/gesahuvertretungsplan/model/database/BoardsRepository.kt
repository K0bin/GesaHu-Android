package rhedox.gesahuvertretungsplan.model.database

import android.content.Context
import android.content.Loader
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.util.Log
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.database.tables.*
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesAdapter
import rhedox.gesahuvertretungsplan.util.Open

/**
 * Created by robin on 29.10.2016.
 */
@Open
class BoardsRepository(context: Context) : android.support.v4.content.Loader.OnLoadCompleteListener<Cursor> {
    private val context = context.applicationContext

    private val observer: Observer;

    private var loader: CursorLoader? = null

    var callback: ((boards: List<Board>) -> Unit)? = null;

    init {
        observer = Observer {
            loadBoards()
        }
        context.contentResolver.registerContentObserver(BoardsContract.uri, true, observer);
    }

    fun destroy() {
        context.contentResolver.unregisterContentObserver(observer)

        if(loader == null) {
            loader!!.unregisterListener(this)
            if (loader!!.isStarted) {
                loader!!.cancelLoad()
                loader!!.stopLoading()
                loader!!.reset()
            }
            loader = null;
        }
    }

    fun loadBoards() {
        if(loader == null) {
            loader = CursorLoader(context.applicationContext, BoardsContract.uri, BoardsContract.columns.toTypedArray(), null, null, null);
            loader!!.registerListener(0, this)
        } else {
            loader!!.reset()
        }
        loader!!.startLoading();
    }

    override fun onLoadComplete(loader: android.support.v4.content.Loader<Cursor>?, data: Cursor?) {
        if(loader == null || data == null)
            return;

        callback?.invoke(BoardAdapter.listFromCursor(data))
        data.close()
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