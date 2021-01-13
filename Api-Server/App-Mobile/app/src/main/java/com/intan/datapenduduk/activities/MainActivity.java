package com.intan.datapenduduk.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.intan.datapenduduk.R;
import com.intan.datapenduduk.bottomnavigation.BottomNavigationBehavior;
import com.intan.datapenduduk.controller.Utils;
import com.intan.datapenduduk.fragment.HomeFragment;
import com.intan.datapenduduk.fragment.ProfileFragment;
import com.intan.datapenduduk.store.SharedPrefManager;

public class MainActivity extends AppCompatActivity implements
        ProfileFragment.ProfileFragmentListener{


    private BottomNavigationView navigationView;
    private HomeFragment mHomeFragment;
    private ProfileFragment mProfileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        onLoadHomeFragment();
    }

    private void initViews() {
        navigationView = (BottomNavigationView) findViewById(R.id.bottom_nav_profile_activity);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_home:
                   onLoadHomeFragment();
                    break;
                case R.id.menu_account:
                   onLoadProfileFragment();
                   break;
            }
            return false;
        }
    };
    private void onLoadHomeFragment(){
        if (mHomeFragment == null)
        {
            mHomeFragment = new HomeFragment();
        }
        onFragmentLoad(mHomeFragment);
    }

    private void onLoadProfileFragment(){
        if (mProfileFragment == null)
        {
            mProfileFragment = new ProfileFragment(this);
        }
        onFragmentLoad(mProfileFragment);
    }

    private void onFragmentLoad(Fragment fragment){

        String TAG = fragment.getClass().getSimpleName();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.container_activity_main, fragment,TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public void onUserLogOut() {
        onLoadLoginActivity();
    }

    @Override
    public void onProfileUpdatedSuccess(String message) {
        Utils.showToast(this,message);
    }

    @Override
    public void onProfileUpdatedFailed(String message) {
        Utils.showToast(this,message);
    }

    @Override
    public void onPasswordChangedSuccess(String message) {
        Utils.showToast(this,message);
    }

    @Override
    public void onPasswordChangedFailed(String message) {
        Utils.showToast(this,message);
    }

    @Override
    public void onAccountDeleteSuccessFull(String message) {
        Utils.showToast(this,message);
        onLoadLoginActivity();
    }

    @Override
    public void onAccountDeleteFailed(String message) {
        Utils.showToast(this,message);
    }

    @Override
    public void onProfileFragmentResponseError(String message) {
        Utils.showToast(this,message);
    }
    private void onLoadLoginActivity() {

        SharedPrefManager.getInstance(this).clear();
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

        if (mHomeFragment.isVisible()){
            finish();
        }else {
            super.onBackPressed();
        }
    }
}
