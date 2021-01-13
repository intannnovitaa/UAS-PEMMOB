package com.intan.datapenduduk.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intan.datapenduduk.R;
import com.intan.datapenduduk.api.LoginResponse;
import com.intan.datapenduduk.api.MyDefaultResponse;
import com.intan.datapenduduk.api.RetrofitClient;
import com.intan.datapenduduk.controller.Utils;
import com.intan.datapenduduk.model.User;
import com.intan.datapenduduk.store.SharedPrefManager;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements View.OnClickListener {


    private CircleImageView ivPhoto;
    private TextView tvName,tvEmail,tvPhone,tvUpdateProfile,tvChangePassword;
    private Button btnLogout;
    private LinearLayout mLayoutUpdateProfile,mLayoutChangePass;
    private Button btnSaveProfile,btnChangePass,btnDeleteAccount;
    private EditText etEmail,etName,etMobile,etCurrentPass,etNewPass;

    private ProfileFragmentListener mListener;

    public interface ProfileFragmentListener{
        void onUserLogOut();
        void onProfileUpdatedSuccess(String message);
        void onProfileUpdatedFailed(String message);
        void onPasswordChangedSuccess(String message);
        void onPasswordChangedFailed(String message);
        void onAccountDeleteSuccessFull(String message);
        void onAccountDeleteFailed(String message);
        void onProfileFragmentResponseError(String message);
    }

    public ProfileFragment(ProfileFragmentListener listener){
        mListener = listener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFragmentListener){
            mListener = (ProfileFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivPhoto = (CircleImageView) view.findViewById(R.id.iv_photo_fragment_profile);
        tvName = (TextView) view.findViewById(R.id.tv_name_fragment_profile);
        tvEmail = (TextView) view.findViewById(R.id.tv_email_fragment_profile);
        tvPhone = (TextView) view.findViewById(R.id.tv_mobile_fragment_profile);
        btnLogout = (Button) view.findViewById(R.id.btn_log_out_fragment_profile);

        tvUpdateProfile = (TextView) view.findViewById(R.id.tv_update_profile_fragment_profile);
        mLayoutUpdateProfile = (LinearLayout) view.findViewById(R.id.layout_update_fragment_profile);
        btnSaveProfile = (Button) view.findViewById(R.id.btn_save_fragment_profile);
        tvChangePassword = (TextView) view.findViewById(R.id.tv_change_pass_profile_fragment_profile);
        mLayoutChangePass = (LinearLayout) view.findViewById(R.id.layout_change_pass_fragment_profile);
        btnChangePass = (Button) view.findViewById(R.id.btn_change_pass_fragment_profile);

        etName = (EditText) view.findViewById(R.id.et_name_fragment_profile);
        etEmail = (EditText) view.findViewById(R.id.et_email_fragment_profile);
        etMobile = (EditText) view.findViewById(R.id.et_phone_fragment_profile);
        etCurrentPass = (EditText) view.findViewById(R.id.et_current_pass_fragment_profile);
        etNewPass = (EditText) view.findViewById(R.id.et_new_pass_fragment_profile);
        btnDeleteAccount = (Button) view.findViewById(R.id.btn_delete_profile_fragment_profile);


        User user = SharedPrefManager.getInstance(getContext()).getUser();
        Picasso.with(getContext()).load(user.getImage()).into(ivPhoto);
        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());
        tvPhone.setText(user.getPhone());


        Picasso.with(getContext()).load(RetrofitClient.BASE_URL + "uploads/"+user.getImage()).into(ivPhoto);


        btnLogout.setOnClickListener(this);
        tvUpdateProfile.setOnClickListener(this);
        btnSaveProfile.setOnClickListener(this);
        tvChangePassword.setOnClickListener(this);
        btnChangePass.setOnClickListener(this);
        btnDeleteAccount.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if ( id == btnLogout.getId()){
            userLogOut();
        }else if (id == tvUpdateProfile.getId()){
            showProfileEditMode();
        }else if (id == btnSaveProfile.getId()){
            updateProfile();

        }else if (id == tvChangePassword.getId()){
            showPasswordChangeMode();
        }else if (id == btnChangePass.getId()){
            changePassword();
        }else if (id == btnDeleteAccount.getId()){
            deleteAccount();
        }
    }

    private void deleteAccount() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Are you sure?");
        builder.setMessage("this action is irreversible....");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                User  user = SharedPrefManager.getInstance(getContext()).getUser();

                if (Utils.isNetworkAvailable(getContext())){
                    Call<MyDefaultResponse> call = RetrofitClient.getInstanse().getApi().deleteAccount(user.getId());
                    call.enqueue(new Callback<MyDefaultResponse>() {
                        @Override
                        public void onResponse(Call<MyDefaultResponse> call, Response<MyDefaultResponse> response) {

                            if (response.isSuccessful()){
                                if (!response.body().isError()){
                                    SharedPrefManager.getInstance(getContext()).clear();
                                    if (mListener != null)
                                    {
                                        mListener.onAccountDeleteSuccessFull(response.body().getMessage());
                                    }
                                }else {
                                    if (mListener != null)
                                        mListener.onAccountDeleteFailed(response.body().getMessage());
                                }
                            }else {
                                if (mListener != null)
                                    mListener.onProfileFragmentResponseError(getResources().getString(R.string.err_start_local_server));
                            }

                        }

                        @Override
                        public void onFailure(Call<MyDefaultResponse> call, Throwable t) {
                            if (mListener !=null){
                                mListener.onProfileFragmentResponseError(t.getMessage()+" "+getResources().getString(R.string.err_device_not_connected));
                            }
                        }
                    });
                }else {
                    if (mListener != null)
                        mListener.onProfileFragmentResponseError(getResources().getString(R.string.err_net_connection));
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    private void showProfileEditMode() {

        etName.setText(tvName.getText());
        etEmail.setText(tvEmail.getText());
        etMobile.setText(tvPhone.getText());

        if (mLayoutUpdateProfile.getVisibility() == View.GONE){
            mLayoutUpdateProfile.setVisibility(View.VISIBLE);
            Drawable img = getContext().getResources().getDrawable( R.drawable.ic_arrow_up_green);
            tvUpdateProfile.setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);
        }else {
            mLayoutUpdateProfile.setVisibility(View.GONE);
            Drawable img = getContext().getResources().getDrawable( R.drawable.ic_arrow_down_green);
            tvUpdateProfile.setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);
        }
    }

    private void updateProfile() {



        String email = etEmail.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();

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

        if (name.isEmpty()){
            etName.setError(getResources().getString(R.string.err_name_required));
            etName.requestFocus();
            return;
        }

        if (mobile.isEmpty()){
            etMobile.setError("Number Required.");
            etMobile.requestFocus();
            return;
        }

        User user = SharedPrefManager.getInstance(getContext()).getUser();

        if (Utils.isNetworkAvailable(getContext())){
            Call<LoginResponse> call = RetrofitClient.getInstanse().getApi().updateProfile(user.getId(),email,name,mobile);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                    if (response.isSuccessful()){
                        if (!response.body().isError()){
                            SharedPrefManager.getInstance(getContext()).saveUser(response.body().getUser());
                            if (mListener !=null){
                                mListener.onProfileUpdatedSuccess(response.body().getMessage());
                                etName.setText("");
                                etMobile.setText("");
                                etEmail.setText("");

                            }
                        }else {
                            if (mListener !=null){
                                mListener.onProfileUpdatedFailed(response.body().getMessage());
                            }
                        }
                    }else {
                        if (mListener !=null){
                            mListener.onProfileFragmentResponseError(getResources().getString(R.string.err_start_local_server));
                        }
                    }

                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    if (mListener !=null){
                        mListener.onProfileFragmentResponseError(t.getMessage()+" "+getResources().getString(R.string.err_device_not_connected));
                    }
                }
            });
        }else {
            if (mListener != null)
                mListener.onProfileFragmentResponseError(getResources().getString(R.string.err_net_connection));
        }

    }

    private void showPasswordChangeMode() {
        if (mLayoutChangePass.getVisibility() == View.GONE){
            mLayoutChangePass.setVisibility(View.VISIBLE);
            Drawable img = getContext().getResources().getDrawable( R.drawable.ic_arrow_up_green);
            tvChangePassword.setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);
        }else {
            mLayoutChangePass.setVisibility(View.GONE);
            Drawable img = getContext().getResources().getDrawable( R.drawable.ic_arrow_down_green);
            tvChangePassword.setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);
        }
    }

    private void changePassword() {

        String currentPassword = etCurrentPass.getText().toString().trim();
        String newPassword = etNewPass.getText().toString().trim();

        if (currentPassword.isEmpty()){
            etCurrentPass.setError(getResources().getString(R.string.err_password_length));
            etCurrentPass.requestFocus();
            return;
        }

        if (currentPassword.length() < 6){
            etCurrentPass.setError(getResources().getString(R.string.err_password_length));
            etCurrentPass.requestFocus();
            return;
        }

        if (newPassword.isEmpty()){
            etNewPass.setError(getResources().getString(R.string.err_password_length));
            etNewPass.requestFocus();
            return;
        }

        if (newPassword.length() < 6){
            etNewPass.setError(getResources().getString(R.string.err_password_length));
            etNewPass.requestFocus();
            return;
        }

        User user = SharedPrefManager.getInstance(getContext()).getUser();
        if (Utils.isNetworkAvailable(getContext())){
            Call<MyDefaultResponse> call = RetrofitClient.getInstanse().getApi().updatePassword(currentPassword,newPassword,user.getEmail());
            call.enqueue(new Callback<MyDefaultResponse>() {
                @Override
                public void onResponse(Call<MyDefaultResponse> call, Response<MyDefaultResponse> response) {
                    if (response.isSuccessful()){
                        if (!response.body().isError()){
                            if (mListener != null){
                                mListener.onPasswordChangedSuccess(response.body().getMessage());
                                etNewPass.setText("");
                                etCurrentPass.setText("");
                            }
                        }else {
                            if (mListener != null)
                                mListener.onPasswordChangedFailed(response.body().getMessage());
                        }
                    }else {
                        if (mListener !=null){
                            mListener.onProfileFragmentResponseError(getResources().getString(R.string.err_start_local_server));
                        }
                    }

                }

                @Override
                public void onFailure(Call<MyDefaultResponse> call, Throwable t) {
                    if (mListener !=null){
                        mListener.onProfileFragmentResponseError(t.getMessage()+" "+getResources().getString(R.string.err_device_not_connected));
                    }
                }
            });
        }else {
            if (mListener != null)
                mListener.onProfileFragmentResponseError(getResources().getString(R.string.err_net_connection));
        }

    }

    private void userLogOut() {
        if (mListener!=null){
            mListener.onUserLogOut();
        }
    }
}
