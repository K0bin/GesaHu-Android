package rhedox.gesahuvertretungsplan.model

import android.accounts.*
import android.content.Context
import android.os.Bundle
import com.pawegio.kandroid.accountManager
import rhedox.gesahuvertretungsplan.model.GesaHuiApi

/**
 * Created by robin on 12.10.2016.
 */

//Authenticator stub because the authenticator api is designed for OAuth
class GesaHuiAuthenticator(context: Context) : AbstractAccountAuthenticator(context) {
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
        throw UnsupportedOperationException("not implemented")
    }

}