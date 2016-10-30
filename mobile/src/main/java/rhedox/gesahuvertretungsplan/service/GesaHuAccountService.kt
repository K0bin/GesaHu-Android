package rhedox.gesahuvertretungsplan.service

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import com.pawegio.kandroid.accountManager
import rhedox.gesahuvertretungsplan.App

/**
 * Created by robin on 11.10.2016.
 */


class GesaHuAccountService : Service() {
    private lateinit var authenticator: GesaHuAuthenticator;
    override fun onCreate() {
        super.onCreate()
        authenticator = GesaHuAuthenticator(this)
    }

    override fun onBind(intent: Intent?): IBinder {
        return authenticator.iBinder;
    }

    //Authenticator stub because the authenticator api is designed for OAuth
    class GesaHuAuthenticator(private val context: Context) : AbstractAccountAuthenticator(context) {

        override fun getAuthTokenLabel(authTokenType: String?): String {
            throw UnsupportedOperationException("not implemented")
        }

        override fun confirmCredentials(response: AccountAuthenticatorResponse, account: Account, options: Bundle?): Bundle {
            throw UnsupportedOperationException("not implemented")
        }

        override fun updateCredentials(response: AccountAuthenticatorResponse?, account: Account?, authTokenType: String?, options: Bundle?): Bundle {
            throw UnsupportedOperationException("not implemented")
        }

        override fun getAuthToken(response: AccountAuthenticatorResponse?, account: Account?, authTokenType: String?, options: Bundle?): Bundle {
            throw UnsupportedOperationException("not implemented")
        }

        override fun hasFeatures(response: AccountAuthenticatorResponse?, account: Account?, features: Array<out String>?): Bundle {
            throw UnsupportedOperationException("not implemented")
        }

        override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?): Bundle {
            throw UnsupportedOperationException("not implemented")
        }

        override fun addAccount(response: AccountAuthenticatorResponse?, accountType: String?, authTokenType: String?, requiredFeatures: Array<out String>?, options: Bundle?): Bundle {
            val accounts = context.accountManager?.getAccountsByType(App.ACCOUNT_TYPE)
            if (accounts != null && accounts.size > 0) {
                return Bundle();
            }
            return Bundle();
        }

    }
}