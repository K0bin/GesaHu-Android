package rhedox.gesahuvertretungsplan.ui.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.net.toUri
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.CredentialRequest
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsClient
import com.google.android.gms.common.api.ResolvableApiException
import kotlinx.android.synthetic.main.activity_auth.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.api.BoardName
import rhedox.gesahuvertretungsplan.model.api.GesaHu
import rhedox.gesahuvertretungsplan.service.BoardsSyncService
import rhedox.gesahuvertretungsplan.service.CalendarSyncService
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import rhedox.gesahuvertretungsplan.service.SubstitutesSyncService
import rhedox.gesahuvertretungsplan.util.Md5Util
import rhedox.gesahuvertretungsplan.util.accountManager
import rhedox.gesahuvertretungsplan.util.notificationManager
import javax.inject.Inject

/**
 * Created by robin on 31.10.2016.
 */
class AuthActivity : AccountAuthenticatorAppCompatActivity(), View.OnClickListener, Callback<List<BoardName>> {
    companion object {
        const val stateAccount = "account";
        const val argIsNewAccount ="isNewAccount"
        const val launchedByApp ="wasLaunchedByApp"
    }

    private var account: Account? = null;
    private var username: String = "";
    private var passwordMd5: String = "";
    private var password: String = "";

    @Inject internal lateinit var gesaHu: GesaHu;
    private var call: Call<List<BoardName>>? = null;

    private lateinit var snackbar: Snackbar;
    private lateinit var client: CredentialsClient

    private var autoLockSignInSuccessful = false;

    object SignInRequestCodes {
        const val save = 1;
        const val read = 2;
        const val hint = 3;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).appComponent.inject(this)

        setContentView(R.layout.activity_auth)

        client = Credentials.getClient(this)
        val request = CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build()

        client.request(request).addOnCompleteListener {
            if (it.isSuccessful) {
                usernameEdit.setText(it.result.credential.id)
                passwordEdit.setText(it.result.credential.password)
                autoLockSignInSuccessful = true;
                login()
                return@addOnCompleteListener
            }
            val exception = it.exception
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this, SignInRequestCodes.read);
                } catch (e: IntentSender.SendIntentException ) {}
            }
        }

        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK != Configuration.UI_MODE_NIGHT_YES && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = Color.parseColor("#ffe0e0e0");
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK;
        }

        if(savedInstanceState != null) {
            account = savedInstanceState.getParcelable(stateAccount);
        }

        if(account == null && !intent.getBooleanExtra(argIsNewAccount, true)) {
            val accounts = accountManager.getAccountsByType(GesaHuAccountService.GesaHuAuthenticator.accountType) ?: arrayOf<Account>()
            if (accounts.isNotEmpty()) {
                account = accounts[0]
                username = account!!.name;
                usernameEdit.setText(username)
            }
        }

        passwordEdit.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                login();
                return@setOnEditorActionListener true
            }
            false
        }
        loginButton.setOnClickListener(this)

        snackbar = Snackbar.make(usernameLayout, getString(R.string.login_required), Snackbar.LENGTH_SHORT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data ?: return;

        if (requestCode == SignInRequestCodes.read) {
            if (resultCode == RESULT_OK) {
                val credential = data.getParcelableExtra<Credential>(Credential.EXTRA_KEY);
                passwordEdit.setText(credential.password)
                usernameEdit.setText(credential.id)
                autoLockSignInSuccessful = true;
                login()
            }
        } else if (requestCode == SignInRequestCodes.save) {
            finishActivity()
        }
    }

    private fun login() {
        var areFieldsEmpty = false
        if (passwordEdit.text?.toString().isNullOrBlank()) {
            passwordLayout.error = getString(R.string.login_password_empty)
            passwordLayout.isErrorEnabled = true
            areFieldsEmpty = true;
        } else {
            passwordLayout.isErrorEnabled = false
            password = passwordEdit.text.toString()
            passwordMd5 = Md5Util.Md5(password)
        }

        if (usernameEdit.text?.toString().isNullOrBlank()) {
            usernameLayout.error = getString(R.string.login_username_empty)
            usernameLayout.isErrorEnabled = true
            areFieldsEmpty = true;
        } else {
            usernameLayout.isErrorEnabled = false
            username = usernameEdit.text.toString()
            if(username.length > 2) {
                username = username.substring(0, 2).toUpperCase() + username.substring(2, username.length)
            }
        }

        if (!areFieldsEmpty && call == null) {
            usernameEdit.isFocusable = false;
            usernameEdit.isFocusableInTouchMode = false;
            usernameEdit.isEnabled = false;
            passwordEdit.isFocusable = false;
            passwordEdit.isFocusableInTouchMode = false;
            passwordEdit.isEnabled = false;

            call = gesaHu.boardNames(username, passwordMd5)
            call?.enqueue(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        if(account != null && outState != null) {
            outState.putParcelable(stateAccount, account)
        }
    }

    override fun onClick(v: View?) {
        login();
    }

    override fun onResponse(call: Call<List<BoardName>>, response: Response<List<BoardName>>) {
        this.call = null;
        if(response.isSuccessful) {
            finishLogin()
        } else {
            usernameLayout.error = getString(R.string.login_403);
            usernameLayout.isErrorEnabled = true;
            usernameLayout.isFocusable = true;
            usernameLayout.isFocusableInTouchMode = true;
            usernameLayout.isEnabled = true;
            passwordLayout.error = getString(R.string.login_403);
            passwordLayout.isErrorEnabled = true;
            passwordEdit.isFocusable = true;
            passwordEdit.isFocusableInTouchMode = true;
            passwordEdit.isEnabled = true;
        }
    }

    private fun finishLogin() {
        if(account == null) {
            account = Account(username, GesaHuAccountService.GesaHuAuthenticator.accountType);
            accountManager.addAccountExplicitly(account, passwordMd5, Bundle());
        } else {
            accountManager.setPassword(account, passwordMd5);
        }

        notificationManager.cancel(GesaHuAccountService.GesaHuAuthenticator.accountType.hashCode())

        val credential = Credential.Builder(username)
                .setPassword(passwordEdit.text.toString())
                .setProfilePictureUri(BoardsSyncService.getPhotoUrl(username).toUri())
                .build()

        if (!autoLockSignInSuccessful) {
            client.save(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    // Credentials were saved
                    finishActivity()
                } else {
                    val exception = it.exception
                    if (exception is ResolvableApiException) {
                        // Try to resolve the save request. This will prompt the user if
                        // the credential is new.
                        try {
                            exception.startResolutionForResult(this, SignInRequestCodes.save);
                        } catch (e: IntentSender.SendIntentException) {
                            finishActivity()
                        }
                    } else {
                        finishActivity()
                    }
                }
            }
        } else {
            finishActivity()
        }
    }

    private fun finishActivity() {
        SubstitutesSyncService.setIsSyncEnabled(account!!, true)
        BoardsSyncService.setIsSyncEnabled(account!!, true)
        CalendarSyncService.updateIsSyncable(account!!, applicationContext)

        val res = Intent()
        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, username)
        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, GesaHuAccountService.GesaHuAuthenticator.accountType);

        setAccountAuthenticatorResult(res.extras)
        setResult(Activity.RESULT_OK, res)

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish();
    }

    override fun onFailure(call: Call<List<BoardName>>?, t: Throwable?) {
        this.call = null;
        snackbar.show();
        autoLockSignInSuccessful = false;
        usernameEdit.isFocusable = true;
        usernameEdit.isFocusableInTouchMode = true;
        usernameEdit.isEnabled = true;
        passwordEdit.isFocusable = true;
        passwordEdit.isFocusableInTouchMode = true;
        passwordEdit.isEnabled = true;
    }
}