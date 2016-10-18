package rhedox.gesahuvertretungsplan.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import rhedox.gesahuvertretungsplan.model.SyncAdapter

/**
 * Created by robin on 18.10.2016.
 */
class SyncService : Service() {

    companion object {
        private var syncAdapter: SyncAdapter? = null;
    }

    override fun onCreate() {
        super.onCreate()

        synchronized(Companion) {
            if (syncAdapter == null)
                syncAdapter = SyncAdapter(applicationContext, true)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return syncAdapter!!.syncAdapterBinder;
    }
}