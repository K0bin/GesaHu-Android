package rhedox.gesahuvertretungsplan.ui.activity

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.activity_auth.*
import org.jetbrains.anko.accountManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.api.GesaHu
import rhedox.gesahuvertretungsplan.model.api.BoardName
import rhedox.gesahuvertretungsplan.model.database.BoardsContentProvider
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.service.BoardsSyncService
import rhedox.gesahuvertretungsplan.service.CalendarSyncService
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import rhedox.gesahuvertretungsplan.service.SubstitutesSyncService
import rhedox.gesahuvertretungsplan.util.Md5Util

/**
 * Created by robin on 31.10.2016.
 */
class AuthActivity : AccountAuthenticatorAppCompatActivity(), View.OnClickListener, Callback<List<BoardName>> {

    companion object {
        const val stateAccount = "account";
        const val argIsNewAccount ="isNewAccout"
        const val launchedByApp ="wasLaunchedByApp"
    }

    private var account: Account? = null;
    private var username: String = "";
    private var password: String = "";
    private var wasLaunchedByApp = false;

    private lateinit var gesaHu: GesaHu;
    private var call: Call<List<BoardName>>? = null;

    private lateinit var snackbar: Snackbar;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_auth)

        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK != Configuration.UI_MODE_NIGHT_YES && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = Color.parseColor("#ffe0e0e0");
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK;
        }

        if(savedInstanceState != null) {
            account = savedInstanceState.getParcelable<Account>(stateAccount);
        }
        wasLaunchedByApp = intent.getBooleanExtra(launchedByApp, false)

        if(account == null && !intent.getBooleanExtra(argIsNewAccount, true)) {
            val accounts = accountManager.getAccountsByType(GesaHuAccountService.GesaHuAuthenticator.accountType) ?: arrayOf<Account>()
            if (accounts.isNotEmpty()) {
                account = accounts[0]
                username = account!!.name;
                password = accountManager.getPassword(account) ?: ""
            }
        }
        if(account != null) {
            usernameEdit.setText(username)
            if(password != "")
                passwordEdit.setText(password)
        }
        gesaHu = GesaHu(this);

        passwordEdit.setOnEditorActionListener { textView, actionId, keyEvent ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                login();
                true
            }
            false
        }
        loginButton.setOnClickListener(this)

        snackbar = Snackbar.make(usernameLayout, getString(R.string.login_required), Snackbar.LENGTH_SHORT)
    }

    fun login() {
        var areFieldsEmpty = false
        if (passwordEdit.text == null || passwordEdit.text.toString().isNullOrBlank()) {
            if(password.isNullOrBlank()) {
                passwordLayout.error = getString(R.string.login_password_empty)
                passwordLayout.isErrorEnabled = true
                areFieldsEmpty = true;
            }
        } else {
            username = usernameEdit.text.toString()
            if(username.length > 2) {
                username = username.substring(0, 2).toUpperCase() + username.substring(2, username.length)
            }
        }

        if (usernameEdit.text == null || usernameEdit.text.toString().isNullOrBlank()) {
            if(username.isNullOrBlank()) {
                usernameLayout.error = getString(R.string.login_username_empty)
                usernameLayout.isErrorEnabled = true
                areFieldsEmpty = true;
            }
        } else {
            password = Md5Util.Md5(passwordEdit.text.toString())
        }

        if (!areFieldsEmpty && call == null) {
            call = gesaHu.boardNames(username, password)
            call?.enqueue(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        if(account != null && outState != null)
            outState.putParcelable(stateAccount, account)
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
            passwordLayout.error = getString(R.string.login_403);
            passwordLayout.isErrorEnabled = true;
        }
    }

    fun finishLogin() {
        if(account == null) {
            account = Account(username, GesaHuAccountService.GesaHuAuthenticator.accountType);
            accountManager.addAccountExplicitly(account, password, Bundle());
        } else {
            accountManager.setPassword(account, password);
        }

        usernameEdit.isFocusable = false;
        usernameEdit.isFocusableInTouchMode = false;
        usernameEdit.isEnabled = false;
        passwordEdit.isFocusable = false;
        passwordEdit.isFocusableInTouchMode = false;
        passwordEdit.isEnabled = false;

        SubstitutesSyncService.setIsSyncEnabled(account!!, true)
        BoardsSyncService.setIsSyncEnabled(account!!, true)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            CalendarSyncService.setIsSyncEnabled(account!!, true)
        }

        val res = Intent()
        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, username)
        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, GesaHuAccountService.GesaHuAuthenticator.accountType);

        setResult(Activity.RESULT_OK, res)
        finish();

        if(wasLaunchedByApp) {
            /*val intent = Intent(this, SubstitutesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)*/
        }
    }

    override fun onFailure(call: Call<List<BoardName>>?, t: Throwable?) {
        this.call = null;
        snackbar.show();
    }
}