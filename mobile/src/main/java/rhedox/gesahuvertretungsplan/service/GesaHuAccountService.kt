package rhedox.gesahuvertretungsplan.service

import android.accounts.*
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import org.jetbrains.anko.accountManager
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.ui.activity.AuthActivity

/**
 * Created by robin on 11.10.2016.
 */


class GesaHuAccountService : Service() {
    companion object {
        private var authenticator: GesaHuAuthenticator? = null;
    }

    override fun onCreate() {
        super.onCreate()

        synchronized(Companion) {
            if (authenticator == null) {
                authenticator = GesaHuAuthenticator(this)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return authenticator!!.iBinder;
    }

    //Authenticator stub because the authenticator api is designed for OAuth
    class GesaHuAuthenticator(private val context: Context) : AbstractAccountAuthenticator(context) {

        companion object {
            @JvmField
            val accountType = "rhedox.gesahuvertretungsplan.gesaHuAccount";
        }

        object Feature {
            @JvmField
            val supervisionSubstitutes = "aufsichtsvertretung";
            @JvmField
            val syncTimetable = "stundenplan";
            @JvmField
            val originalUserpicture = "originalbild";
        }

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

        override fun hasFeatures(response: AccountAuthenticatorResponse, account: Account, features: Array<String>): Bundle {
            val isTeacher = account.name.startsWith("l", true);
            val isStudent = account.name.startsWith("s", true);

            var allSupported = true;
            for(feature in features) {
                if(feature == Feature.supervisionSubstitutes && !isTeacher)
                    allSupported = false;

                if(feature == Feature.originalUserpicture && !isStudent)
                    allSupported = false;
            }

            val bundle = Bundle();
            bundle.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, allSupported)
            return bundle;
        }

        override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?): Bundle {
            throw UnsupportedOperationException("not implemented")
        }

        override fun addAccount(response: AccountAuthenticatorResponse, accountType: String, authTokenType: String?, requiredFeatures: Array<String>?, options: Bundle?): Bundle {
            val bundle = Bundle()

            val accounts = context.accountManager.getAccountsByType(accountType)
            if (accounts != null && accounts.isNotEmpty()) {
                bundle.putInt(AccountManager.KEY_ERROR_CODE, 1);
                bundle.putString(AccountManager.KEY_ERROR_MESSAGE, context.getString(R.string.login_account_exists));
                return bundle;
            }
            val intent = Intent(context, AuthActivity::class.java)
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
            intent.putExtra(AuthActivity.argIsNewAccount, true)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            bundle.putParcelable(AccountManager.KEY_INTENT, intent)
            return bundle;
        }

    }
}