package rhedox.gesahuvertretungsplan.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import rhedox.gesahuvertretungsplan.model.GesaHuiAuthenticator

/**
 * Created by robin on 11.10.2016.
 */


class GesaHuAccountService : Service() {
    private lateinit var authenticator: GesaHuiAuthenticator;
    override fun onCreate() {
        super.onCreate()
        authenticator = GesaHuiAuthenticator(this)
    }

    override fun onBind(intent: Intent?): IBinder {
        return authenticator.iBinder;
    }
}