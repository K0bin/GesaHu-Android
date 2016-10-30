package rhedox.gesahuvertretungsplan.ui.fragment

import android.accounts.Account
import android.content.ContentResolver
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatButton
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.paolorotolo.appintro.ISlidePolicy
import com.pawegio.kandroid.accountManager
import com.pawegio.kandroid.textWatcher
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.api.GesaHuApi
import rhedox.gesahuvertretungsplan.model.database.BoardsContentProvider
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.util.Md5Util
import rhedox.gesahuvertretungsplan.util.bindView

/**
 * Created by robin on 01.10.2016.
 */
class LoginFragment : Fragment(), ISlidePolicy, Callback<List<Board>>, View.OnClickListener {
    private var username: String = "";
    private var password: String = "";

    private lateinit var gesaHu: GesaHuApi;
    private var call: Call<List<Board>>? = null;

    private lateinit var snackbar: Snackbar;
    private lateinit var toast: Toast;

    private var account: Account? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = false;

        val accounts = context.accountManager?.getAccountsByType(App.ACCOUNT_TYPE) ?: arrayOf<Account>()
        if (accounts.size > 0) {
            account = accounts[0]
        }

        gesaHu = GesaHuApi.create(context);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(view == null)
            return;

        toast = Toast.makeText(context, getString(R.string.login_required), Toast.LENGTH_LONG);
        snackbar = Snackbar.make(usernameLayout, R.string.login_failed, Snackbar.LENGTH_SHORT);
        snackbar.setAction(R.string.retry, this);

        login.setOnClickListener {
            var areFieldsEmpty = false
            if (passwordEdit.text == null || passwordEdit.text.toString().isNullOrBlank()) {
                passwordLayout.error = getString(R.string.login_password_empty)
                passwordLayout.isErrorEnabled = true
                areFieldsEmpty = true
            }
            if (usernameEdit.text == null || usernameEdit.text.toString().isNullOrBlank()) {
                usernameLayout.error = getString(R.string.login_username_empty)
                usernameLayout.isErrorEnabled = true
                areFieldsEmpty = true
            }
            if (!areFieldsEmpty) {
                username = usernameEdit.text.toString()
                password = Md5Util.Md5(passwordEdit.text.toString())

                call = gesaHu.boards(username, password)
                call?.enqueue(this)
            }
        }

        if(account != null) {
            usernameEdit.setText(account!!.name)
            usernameEdit.isFocusable = false;
            usernameEdit.isFocusableInTouchMode = false;
            usernameEdit.isEnabled = false;
        }
    }

    override fun onClick(v: View?) {
        call?.enqueue(this);
    }

    override fun onDestroyView() {
        super.onDestroyView();
        call?.cancel()
    }

    override fun isPolicyRespected(): Boolean {
        return account != null;
    }

    override fun onUserIllegallyRequestedNextPage() {
        toast.show();
    }

    override fun onResponse(call: Call<List<Board>>, response: Response<List<Board>>) {
        if(response.isSuccessful) {
            if(account == null) {
                account = Account(username, App.ACCOUNT_TYPE);
                context.accountManager?.addAccountExplicitly(account, password, Bundle());
            } else {
                context.accountManager?.setPassword(account, password);
            }
            finishLogin(account!!)
        }
        else {
            usernameLayout.error = getString(R.string.login_403);
            usernameLayout.isErrorEnabled = true;
            passwordLayout.error = getString(R.string.login_403);
            passwordLayout.isErrorEnabled = true;
        }
    }

    private fun finishLogin(account: Account) {
        usernameEdit.isFocusable = false;
        usernameEdit.isFocusableInTouchMode = false;
        usernameEdit.isEnabled = false;
        passwordEdit.isFocusable = false;
        passwordEdit.isFocusableInTouchMode = false;
        passwordEdit.isEnabled = false;

        login.isEnabled = false;

        ContentResolver.setIsSyncable(account, SubstitutesContentProvider.authority, 1);
        ContentResolver.setSyncAutomatically(account, SubstitutesContentProvider.authority, true);
        ContentResolver.addPeriodicSync(account, SubstitutesContentProvider.authority, Bundle.EMPTY, 2 * 60 * 60)
        ContentResolver.setIsSyncable(account, BoardsContentProvider.authority, 1);
        ContentResolver.setSyncAutomatically(account, BoardsContentProvider.authority, true);
        ContentResolver.addPeriodicSync(account, BoardsContentProvider.authority, Bundle.EMPTY, 24 * 60 * 60)
    }

    override fun onFailure(call: Call<List<Board>>?, t: Throwable?) {
        snackbar.show();
    }

    companion object {
        @JvmStatic
        fun newInstance(): LoginFragment {
            return LoginFragment();
        }
    }
}