package com.adjiekurniawan.loginscreenminimalis.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.adjiekurniawan.loginscreenminimalis.BaseActivity;
import com.adjiekurniawan.loginscreenminimalis.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private ProgressDialog pDialog;
    private GoogleSignInClient mGoogleSignInClient;

    private TextView mSignUptext;
    private ImageButton mFacebookButton,mGoogleButton;
    private Button mLoginButton;
    private EditText mEmail,mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        initView();
        initInitialize();
    }

    private void initView(){
        mSignUptext = findViewById(R.id.text_no_have_account);
        mFacebookButton = findViewById(R.id.ib_sign_in_fb);
        mGoogleButton = findViewById(R.id.ib_sign_in_google);
        mLoginButton = findViewById(R.id.btn_sign_in);
        mEmail = findViewById(R.id.et_email_field);
        mPassword = findViewById(R.id.et_password_field);
    }

    private void initInitialize(){

        pDialog = new ProgressDialog(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        mSignUptext.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mFacebookButton.setOnClickListener(this);
        mGoogleButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_no_have_account:
                StartActivity(new SignUpAcitivty());
                break;
            case R.id.ib_sign_in_fb:
                ShowNotification(getString(R.string.msg_fb_btn_click));
                break;
            case R.id.ib_sign_in_google:
                signIn();
                break;
            case R.id.btn_sign_in:
                validation();
                break;
        }
    }


    private void validation(){
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
        if (TextUtils.isEmpty(mEmail.getText()) && TextUtils.isEmpty(mPassword.getText())){
            ShowNotification(getString(R.string.msg_is_empty));
            mEmail.setHintTextColor(getResources().getColor(R.color.colorRed));
            mPassword.setHintTextColor(getResources().getColor(R.color.colorRed));
            mPassword.startAnimation(shake);
            mEmail.startAnimation(shake);
        }else if (TextUtils.isEmpty(mEmail.getText())){
            ShowNotification(getString(R.string.msg_email_empty));
            mEmail.setHintTextColor(getResources().getColor(R.color.colorRed));
            mEmail.startAnimation(shake);
        }else if (TextUtils.isEmpty(mPassword.getText())){
            ShowNotification(getString(R.string.msg_pwd_empty));
            mPassword.setHintTextColor(getResources().getColor(R.color.colorRed));
            mPassword.startAnimation(shake);
        }else{
            ShowNotification(getString(R.string.msg_login_success));
        }
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        displayProgressDialog();
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            updateUI();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Login Failed: ", Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void hideProgressDialog() {
        pDialog.dismiss();
    }

    private void displayProgressDialog() {
        pDialog.setMessage("Mencoba Login...\n Harap Tunggu...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void updateUI(){
        Intent activity = new Intent(this,HomeActivity.class);
        activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(activity);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()!=null){
            updateUI();
        }
    }


}
