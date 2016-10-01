package rhedox.gesahuvertretungsplan.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.squareup.moshi.Moshi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import rhedox.gesahuvertretungsplan.BuildConfig;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Boards;
import rhedox.gesahuvertretungsplan.model.GesaHuiApi_old;
import rhedox.gesahuvertretungsplan.util.Md5Util;
import rhedox.gesahuvertretungsplan.util.TextUtils;

/**
 * Created by robin on 30.08.2016.
 */
public class LoginFragment extends Fragment implements ISlidePolicy, Callback<Boards> {

	public static final String TAG = "WelcomePreferenceFragment";

	@BindView(R.id.username_layout)
	TextInputLayout usernameLayout;

	@BindView(R.id.username)
	TextInputEditText username;

	@BindView(R.id.password_layout)
	TextInputLayout passwordLayout;

	@BindView(R.id.password)
	TextInputEditText password;

	private Unbinder unbinder;

	private boolean isLoginSuccessful = false;
	private GesaHuiApi_old gesaHui;
	private Call<Boards> call;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Init Retrofit
		OkHttpClient.Builder builder = new OkHttpClient.Builder();

		if(BuildConfig.DEBUG)
			builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));

		OkHttpClient client = builder.build();

		Moshi moshi = new Moshi.Builder()
				.add(new Boards.Board.Adapter())
				.add(new Boards.Adapter())
				.build();

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://gesahui.de")
				.addConverterFactory(MoshiConverterFactory.create(moshi))
				.client(client)
				.build();

		gesaHui = retrofit.create(GesaHuiApi_old.class);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login, container, false);
		unbinder = ButterKnife.bind(this, view);
		return view;
	}

	@OnTextChanged(R.id.password)
	public void onPasswordChanged(CharSequence text) {
		isLoginSuccessful = false;
	}

	@OnTextChanged(R.id.username)
	public void onUsernameChanged(CharSequence text) {
		isLoginSuccessful = false;
	}

	@OnClick(R.id.login)
	public void onLogin() {
		boolean areFieldsEmpty = false;
		if(password.getText() == null || TextUtils.isEmpty(password.getText().toString())) {
			passwordLayout.setError(getString(R.string.login_password_empty));
			passwordLayout.setErrorEnabled(true);
			areFieldsEmpty = true;
		}
		if(username.getText() == null || TextUtils.isEmpty(username.getText().toString())) {
			usernameLayout.setError(getString(R.string.login_username_empty));
			usernameLayout.setErrorEnabled(true);
			areFieldsEmpty = true;
		}
		if(areFieldsEmpty)
			return;

		String passwordMD5 = Md5Util.Md5(password.getEditableText().toString());
		call = gesaHui.boards(username.getText().toString(), passwordMD5);
		call.enqueue(this);
	}

	@Override
	public boolean isPolicyRespected() {
		//return isLoginSuccessful;
		return true;
	}

	@Override
	public void onUserIllegallyRequestedNextPage() {

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if(unbinder != null)
			unbinder.unbind();
	}

	public static LoginFragment newInstance() {
		return new LoginFragment();
	}

	@Override
	public void onResponse(Call<Boards> call, Response<Boards> response) {
		isLoginSuccessful = true;
	}

	@Override
	public void onFailure(Call<Boards> call, Throwable t) {

	}
}
