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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.paolorotolo.appintro.ISlidePolicy
import com.pawegio.kandroid.accountManager
import com.pawegio.kandroid.textWatcher
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.api.GesaHuApi
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.util.Md5Util
import rhedox.gesahuvertretungsplan.util.bindView

/**
 * Created by robin on 01.10.2016.
 */
class LoginFragment : Fragment(), ISlidePolicy, Callback<List<Board>>, View.OnClickListener {
    private val usernameLayout: TextInputLayout by bindView(R.id.username_layout);
    private val usernameEdit: TextInputEditText by bindView(R.id.usernameEdit);
    private val passwordLayout: TextInputLayout by bindView(R.id.password_layout);
    private val passwordEdit: TextInputEditText by bindView(R.id.passwordEdit);
    private val login: AppCompatButton by bindView(R.id.login);

    private var username: String = "";
    private var password: String = "";

    private lateinit var gesaHu: GesaHuApi;
    private var call: Call<List<Board>>? = null;
    private var isUserLoggedIn = false;

    private lateinit var dialog: AlertDialog;
    private lateinit var snackbar: Snackbar;
    private lateinit var toast: Toast;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = false;

        gesaHu = GesaHuApi.create(context);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toast = Toast.makeText(context, getString(R.string.login_required), Toast.LENGTH_LONG);
        snackbar = Snackbar.make(usernameLayout, R.string.login_failed, Snackbar.LENGTH_SHORT);
        snackbar.setAction(R.string.retry, this);

        usernameEdit.textWatcher {
            afterTextChanged { isUserLoggedIn = false; }
        }
        passwordEdit.textWatcher {
            afterTextChanged { isUserLoggedIn = false; }
        }
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
    }

    override fun onClick(v: View?) {
        call?.enqueue(this);
    }

    override fun onDestroyView() {
        super.onDestroyView();
        call?.cancel()
    }

    override fun isPolicyRespected(): Boolean {
        return isUserLoggedIn;
    }

    override fun onUserIllegallyRequestedNextPage() {
        toast.show();
    }

    override fun onResponse(call: Call<List<Board>>, response: Response<List<Board>>) {
        isUserLoggedIn = response.isSuccessful;

        if(isUserLoggedIn) {
            val account = Account(username, App.ACCOUNT_TYPE);
            context.accountManager?.addAccountExplicitly(account, password, Bundle());

            ContentResolver.setIsSyncable(account, SubstitutesContentProvider.authority, 1);
            ContentResolver.setSyncAutomatically(account, SubstitutesContentProvider.authority, true);
            ContentResolver.addPeriodicSync(account, SubstitutesContentProvider.authority, Bundle.EMPTY, 2 * 60 * 60)

            usernameEdit.isFocusable = false;
            usernameEdit.isFocusableInTouchMode = false;
            passwordEdit.isFocusable = false;
            passwordEdit.isFocusableInTouchMode = false;
            usernameEdit.isEnabled = false;
            passwordEdit.isEnabled = false;

            login.isEnabled = false;
        }
        else {
            usernameLayout.error = getString(R.string.login_403);
            usernameLayout.isErrorEnabled = true;
            passwordLayout.error = getString(R.string.login_403);
            passwordLayout.isErrorEnabled = true;
        }
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