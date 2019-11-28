package com.adjiekurniawan.loginscreenminimalis.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adjiekurniawan.loginscreenminimalis.BaseActivity;
import com.adjiekurniawan.loginscreenminimalis.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private ImageView userPhoto;
    private TextView userName,userEmail;
    private Button userBtnLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();
        initInitialize();
        
    }

    private void initInitialize() {

        userBtnLogout.setOnClickListener(this);

        getDataUser();
    }

    private void initView() {
        userName = findViewById(R.id.displayName);
        userEmail = findViewById(R.id.displayEmail);
        userPhoto = findViewById(R.id.userPhoto);
        userBtnLogout = findViewById(R.id.sign_out_button);
    }

    private void getDataUser(){
        mAuth=FirebaseAuth.getInstance();
        userName.setText(mAuth.getCurrentUser().getDisplayName());
        userEmail.setText(mAuth.getCurrentUser().getEmail());
        Uri profilePicUrl = mAuth.getCurrentUser().getPhotoUrl();
        if (profilePicUrl != null) {
            Glide.with(this).load(profilePicUrl)
                    .into(userPhoto);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == userBtnLogout){
           logout();
        }
    }

    private void logout(){
        mAuth.signOut();
        StartActivity(new SignInActivity());
    }
}
