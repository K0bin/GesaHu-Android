package rhedox.gesahuvertretungsplan.service

import android.accounts.*
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import org.jetbrains.anko.accountManager
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.notificationManager
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.ui.activity.AuthActivity

/**
 * Created by robin on 11.10.2016.
 */


class GesaHuAccountService : Service() {
    private lateinit var authenticator: GesaHuAuthenticator

    override fun onCreate() {
        super.onCreate()

        authenticator = GesaHuAuthenticator(this)
    }

    override fun onBind(intent: Intent?): IBinder {
        return authenticator!!.iBinder;
    }

    //Authenticator
    class GesaHuAuthenticator(private val context: Context) : AbstractAccountAuthenticator(context) {

        companion object {
            const val accountType = "rhedox.gesahuvertretungsplan.gesaHuAccount";

            const val notificationChannel = "otherChannel";
            const val requestCode = 10;

            fun askForLogin(context: Context) {
                val intent = context.intentFor<AuthActivity>(AuthActivity.argIsNewAccount to false)
                val notificationManager = context.notificationManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (notificationManager.getNotificationChannel(SubstitutesNotifierService.substitutesChannel) == null) {
                        val channel = NotificationChannel(notificationChannel, context.getString(R.string.notification_channel_other), NotificationManager.IMPORTANCE_DEFAULT)
                        notificationManager.createNotificationChannel(channel)
                    }
                }

                val notification = NotificationCompat.Builder(context, notificationChannel)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(context.getString(R.string.notification_ask_for_login_title))
                        .setContentText(context.getString(R.string.notification_ask_for_login_body))
                        .setContentIntent(PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .build()

                context.notificationManager.notify(accountType.hashCode(), notification)
            }
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