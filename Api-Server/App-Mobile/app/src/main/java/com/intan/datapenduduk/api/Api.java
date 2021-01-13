package com.intan.datapenduduk.api;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by bhavin on 8/13/2018.
 */

public interface Api {



    @Multipart
    @POST("Api.php?apicall=register")
    Call<MyDefaultResponse> createUser(@Part("name") RequestBody name,
                                       @Part("email") RequestBody email,
                                       @Part("pass") RequestBody pass,
                                       @Part("phone") RequestBody phone,
                                       @Part("image\"; filename=\"myfile.jpg\" ")RequestBody file);

    @FormUrlEncoded
    @POST("Api.php?apicall=userLogin")
    Call<LoginResponse> userLogin(
            @Field("email") String email,
            @Field("pass")String password
    );

    @FormUrlEncoded
    @POST("Api.php?apicall=updateProfile")
    Call<LoginResponse> updateProfile(
            @Field("id") int id,
            @Field("name") String name,
            @Field("email") String email,
            @Field("phone") String phone
    );

    @FormUrlEncoded
    @POST("Api.php?apicall=updatePassword")
    Call<MyDefaultResponse> updatePassword(
            @Field("currentpass") String currentpas,
            @Field("newpass") String newpass,
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("Api.php?apicall=deleteAccount")
    Call<MyDefaultResponse> deleteAccount(
            @Field("id") int id
    );

}
