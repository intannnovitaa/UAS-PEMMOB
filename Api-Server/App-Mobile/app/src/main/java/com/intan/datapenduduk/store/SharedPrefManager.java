package com.intan.datapenduduk.store;

import android.content.Context;
import android.content.SharedPreferences;

import com.intan.datapenduduk.model.User;

public class SharedPrefManager {

    private static SharedPrefManager mInstance;
    private static final String SHARED_PREF_NAME = "my_shared_pref";
    private Context mContext;

    private SharedPrefManager(Context context){
        this.mContext = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context){

        if (mInstance == null){
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void saveUser(User user){

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("id",user.getId());
        editor.putString("name",user.getName());
        editor.putString("email",user.getEmail());
        editor.putString("phone",user.getPhone());
        editor.putString("image",user.getImage());
        editor.apply();

    }

    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getInt("id",-1) != -1;
    }

    public User getUser(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return
                new User(
                        sharedPreferences.getInt("id",-1),
                        sharedPreferences.getString("name",null),
                        sharedPreferences.getString("email",null),
                        sharedPreferences.getString("phone",null),
                        sharedPreferences.getString("image",null)
                );
    }

    public void clear(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
