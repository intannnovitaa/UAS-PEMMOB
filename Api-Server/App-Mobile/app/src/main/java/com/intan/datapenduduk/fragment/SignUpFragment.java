package com.intan.datapenduduk.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.intan.datapenduduk.R;
import com.intan.datapenduduk.api.MyDefaultResponse;
import com.intan.datapenduduk.api.RetrofitClient;
import com.intan.datapenduduk.controller.Utils;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

@SuppressWarnings("ConstantConditions")
public class SignUpFragment extends Fragment implements View.OnClickListener{

    private EditText etName,etEmail,etPassword,etPhone;
    private Button btnSignUp;
    private CircleImageView  ivPhoto;
    private Uri imageUri;
    static final Integer READ_EXST = 0x4;

    private SignUpFragmentListener mListener;

    public interface SignUpFragmentListener{

        void onAccountCreatedSuccess(String message);
        void onAccountCreatedFailed(String message);
        void onSignUpResponseError(String message);
    }


    public SignUpFragment(SignUpFragmentListener listener){
        mListener = listener;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignUpFragmentListener){
            mListener = (SignUpFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = (EditText) view.findViewById(R.id.et_name_fragment_sign_up);
        etEmail = (EditText) view.findViewById(R.id.et_email_fragment_sign_up);
        etPassword = (EditText) view.findViewById(R.id.et_password_fragment_sign_up);
        etPhone = (EditText) view.findViewById(R.id.et_phone_fragment_sign_up);
        btnSignUp = (Button) view.findViewById(R.id.btn_sign_fragment_sign_up);
        ivPhoto = (CircleImageView) view.findViewById(R.id.iv_photo_fragment_sign_up);

        btnSignUp.setOnClickListener(this);
        ivPhoto.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                ivPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnSignUp.getId()){
            createUser();
        }else if (view.getId() == ivPhoto.getId()){
            imageChooseIntent();
        }
    }

    private void createUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();


        if (email.isEmpty()){
            etEmail.setError(getResources().getString(R.string.err_email_required));
            etEmail.requestFocus();
            return;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError(getResources().getString(R.string.err_valid_email));
            etEmail.requestFocus();
            return;
        } else if (password.isEmpty()){
            etPassword.setError(getResources().getString(R.string.err_password_length));
            etPassword.requestFocus();
            return;
        } else if (password.length() < 6){
            etPassword.setError(getResources().getString(R.string.err_password_length));
            etPassword.requestFocus();
            return;
        } else if (name.isEmpty()){
            etName.setError(getResources().getString(R.string.err_name_required));
            etName.requestFocus();
            return;
        } else if (phone.isEmpty()){
            etPhone.setError(getResources().getString(R.string.err_number_required));
            etPhone.requestFocus();
            return;
        }else if (imageUri == null)
        {
            Utils.showToast(getContext(),"select image");
            return;
        }

        storeToServer(name,email,password,phone,imageUri);

    }

    private void storeToServer(String name, String email, String password, String phone, Uri imageUri) {

        File file = new File(getRealPathFromURI(imageUri));
        final RequestBody requestFile = RequestBody.create(MediaType.parse(getActivity().getContentResolver().getType(imageUri)), file);
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody pasBody = RequestBody.create(MediaType.parse("text/plain"), password);
        RequestBody phoneBody = RequestBody.create(MediaType.parse("text/plain"), phone);

        if (Utils.isNetworkAvailable(getContext())){
            Call<MyDefaultResponse> call = RetrofitClient.getInstanse().getApi().createUser(nameBody,emailBody,pasBody,phoneBody,requestFile);

            call.enqueue(new Callback<MyDefaultResponse>() {
                @Override
                public void onResponse(Call<MyDefaultResponse> call, Response<MyDefaultResponse> response) {

                    if (response.isSuccessful()){
                        if (!response.body().isError()){

                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                            if (mListener!=null)
                                mListener.onAccountCreatedSuccess(response.body().getMessage());
                        }else {
                            if (mListener != null)
                                mListener.onAccountCreatedFailed(response.body().getMessage());
                        }
                    }else {
                        if (mListener != null)
                            mListener.onSignUpResponseError(getResources().getString(R.string.err_start_local_server));
                    }

                }

                @Override
                public void onFailure(Call<MyDefaultResponse> call, Throwable t) {
                    if (mListener != null)
                        mListener.onSignUpResponseError(t.getMessage()+" "+getResources().getString(R.string.err_device_not_connected));
                }
            });
        }else {
            if (mListener != null)
                mListener.onSignUpResponseError(getResources().getString(R.string.err_net_connection));
        }
    }

    private String getRealPathFromURI(Uri imageUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getContext(), imageUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void imageChooseIntent() {
        boolean result = Utils.checkPermission(getContext());
        if (result){
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 100);
        }

    }

}
