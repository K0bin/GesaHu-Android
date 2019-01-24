package rhedox.gesahuvertretungsplan.model

import android.content.ContentResolver
import android.util.Log
import rhedox.gesahuvertretungsplan.util.Open

/**
 * Created by robin on 25.12.2016.
 */
@Open
class SyncObserver {
    var callback: (() -> Unit)? = null

    private var handle: Any? = null

    init {
        handle = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE) {
            Log.d("SyncObserver", "Observed change in $it")
            callback?.invoke()
        }
    }

    fun destroy() {
        callback = null
        if(handle != null) {
            ContentResolver.removeStatusChangeListener(handle)
        }
    }
}