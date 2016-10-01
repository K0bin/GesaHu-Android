package rhedox.gesahuvertretungsplan.ui.fragment

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.github.paolorotolo.appintro.ISlidePolicy
import com.pawegio.kandroid.textWatcher
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.fragment_login.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.Boards
import rhedox.gesahuvertretungsplan.model.GesaHuiApi
import rhedox.gesahuvertretungsplan.util.Md5Util
import rhedox.gesahuvertretungsplan.util.TextUtils
import rhedox.gesahuvertretungsplan.util.bindView

/**
 * Created by robin on 01.10.2016.
 */
class LoginFragment : Fragment(), ISlidePolicy, Callback<Boards> {

    private val usernameLayout: TextInputLayout by bindView(R.id.username_layout);
    private val username: TextInputEditText by bindView(R.id.username);
    private val passwordLayout: TextInputLayout by bindView(R.id.password_layout);
    private val password: TextInputEditText by bindView(R.id.password);
    private val login: AppCompatButton by bindView(R.id.login);

    private lateinit var gesaHui: GesaHuiApi;
    private lateinit var call: Call<Boards>;
    private var isLoginSuccessful = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = false;

        val builder = OkHttpClient.Builder();
        if(BuildConfig.DEBUG)
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));

        val client = builder.build();

        val moshi = Moshi.Builder()
            .add(Boards.Adapter())
            .add(Boards.Board.Adapter())
            .build();

        val retrofit = Retrofit.Builder()
                .baseUrl("http://gesahui.de")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(client)
                .build();

        gesaHui = retrofit.create(GesaHuiApi::class.java);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username.textWatcher {
            afterTextChanged { isLoginSuccessful = false; }
        }
        password.textWatcher {
            afterTextChanged { isLoginSuccessful = false; }
        }
        login.setOnClickListener {
            var areFieldsEmpty = false
            if (password.text == null || TextUtils.isEmpty(password.text.toString())) {
                passwordLayout.error = getString(R.string.login_password_empty)
                passwordLayout.isErrorEnabled = true
                areFieldsEmpty = true
            }
            if (username.text == null || TextUtils.isEmpty(username.text.toString())) {
                usernameLayout.error = getString(R.string.login_username_empty)
                usernameLayout.isErrorEnabled = true
                areFieldsEmpty = true
            }
            if (!areFieldsEmpty) {
                val passwordMD5 = Md5Util.Md5(password.editableText.toString())
                call = gesaHui.boards(username.text.toString(), passwordMD5)
                call.enqueue(this)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView();
    }

    override fun isPolicyRespected(): Boolean {
        //return isLoginSuccessful;
        return true;
    }

    override fun onUserIllegallyRequestedNextPage() {

    }

    override fun onResponse(call: Call<Boards>?, response: Response<Boards>?) {
        isLoginSuccessful = true;
    }

    override fun onFailure(call: Call<Boards>?, t: Throwable?) {

    }

    companion object {
        @JvmStatic
        fun newInstance(): LoginFragment {
            return LoginFragment();
        }
    }
}