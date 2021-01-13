package com.intan.datapenduduk.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intan.datapenduduk.R;
import com.intan.datapenduduk.api.LoginResponse;
import com.intan.datapenduduk.api.RetrofitClient;
import com.intan.datapenduduk.controller.Utils;
import com.intan.datapenduduk.store.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class LoginFragment extends Fragment implements View.OnClickListener{


    private EditText etEmail,etPassword;
    private TextView tvCreateAccount;
    private Button btnLogin;

    private LoginFragmentListener mListener;

    public interface LoginFragmentListener{
        void onLoginSuccess(String message);
        void onLoginFailed(String message);
        void onCreateNewAccountButtonClicked();
    }

    @SuppressLint("ValidFragment")
    public LoginFragment(LoginFragmentListener listener){
        mListener = listener;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragmentListener){
            mListener = (LoginFragmentListener) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof LoginFragmentListener){
            mListener = (LoginFragmentListener) activity;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEmail = (EditText) view.findViewById(R.id.et_email_fragment_login);
        etPassword = (EditText) view.findViewById(R.id.et_password_fragment_login);
        btnLogin = (Button) view.findViewById(R.id.btn_login_fragment_login);
        tvCreateAccount = (TextView) view.findViewById(R.id.tv_create_account_fragment_login);

        tvCreateAccount.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == btnLogin.getId()){
            onLogin();
        }else if (id == tvCreateAccount.getId()){
            if (mListener!=null){
                mListener.onCreateNewAccountButtonClicked();
            }
        }
    }

    private void onLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()){
            etEmail.setError(getResources().getString(R.string.err_email_required));
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError(getResources().getString(R.string.err_valid_email));
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            etPassword.setError(getResources().getString(R.string.err_password_length));
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6){
            etPassword.setError(getResources().getString(R.string.err_password_length));
            etPassword.requestFocus();
            return;
        }


        if (Utils.isNetworkAvailable(getContext())){
            Call<LoginResponse> call = RetrofitClient.getInstanse().getApi().userLogin(email,password);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()){
                        if (!response.body().isError()){

                            LoginResponse loginResponse = response.body();
                            if (mListener!=null)
                            {
                                SharedPrefManager.getInstance(getContext()).saveUser(loginResponse.getUser());
                                mListener.onLoginSuccess(response.body().getMessage());
                            }else {
                                mListener.onLoginFailed(response.body().getMessage());

                            }
                        }
                    }else {
                        if (mListener != null)
                            mListener.onLoginFailed(getResources().getString(R.string.err_start_local_server));
                    }

                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    if (mListener != null)
                        mListener.onLoginFailed(t.getMessage()+" "+getResources().getString(R.string.err_device_not_connected));
                }
            });
        }else {
            if (mListener != null)
                mListener.onLoginFailed(getResources().getString(R.string.err_net_connection));
        }

    }
}
