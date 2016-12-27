package rhedox.gesahuvertretungsplan.service

import android.accounts.Account
import android.app.Service
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.provider.CalendarContract
import org.jetbrains.anko.accountManager
import org.joda.time.DateTime
import org.joda.time.DurationFieldType
import org.joda.time.LocalTime
import rhedox.gesahuvertretungsplan.model.Event
import rhedox.gesahuvertretungsplan.model.api.GesaHu

/**
 * Created by robin on 27.12.2016.
 */
class CalendarSyncService : Service() {
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

    class SyncAdapter(context: Context, autoInitialize: Boolean): AbstractThreadedSyncAdapter(context, autoInitialize, false) {
        private val gesaHu = GesaHu(context)

        override fun onPerformSync(account: Account, extras: Bundle?, authority: String, provider: ContentProviderClient, syncResult: SyncResult?) {
            if(Thread.interrupted()) {
                return;
            }
            val start = DateTime.now()
            val end = start.withFieldAdded(DurationFieldType.days(), 60).withTime(23,59,59,999)

            val password = context.accountManager.getPassword(account) ?: "";
            val eventCall = gesaHu.events(account.name, password, start, end)
            val response = eventCall.execute()
            if(response != null && response.isSuccessful) {
                val events = response.body()

                events.forEach {

                }
            }
        }
    }
}