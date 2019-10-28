package com.google.firebase.androidx.fitx.java;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.androidx.fitx.R;



public class GoogleSignInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "GoogleSignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth fAuth;     //declare authentication var

    private GoogleSignInClient fGoogleSignInClient;
    private TextView fStatusTextView;
    private TextView fDetailsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlesignin);

        //views
        fStatusTextView = findViewById(R.id.status);
        fDetailsTextView = findViewById(R.id.details);

        //button listeners
        findViewById(R.id.googleSignInButton).setOnClickListener(this);
        findViewById(R.id.googleSignOutButton).setOnClickListener(this);
        findViewById(R.id.googleDisconnectButton).setOnClickListener(this);


        //configuring google sign in options
        GoogleSignInOptions gsio = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //configuration end

        fGoogleSignInClient = GoogleSignIn.getClient(this, gsio);

        //start authentication initialization
        fAuth = FirebaseAuth.getInstance();

    }


    @Override
    public void onStart(){
        super.onStart();
        //checks if user is signed in and updates ui
        FirebaseUser currUser = fAuth.getCurrentUser();
        updateUI(currUser);
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data){
        super.onActivityResult(reqCode, resCode, data);
        //result returned from launching the intent
        if(reqCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                //google sign in was successful, authenticate with firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                FBAuthwGoogle(account);
            }catch(ApiException e){
                //google sign in failed, updates ui
                Log.w(TAG, "Google sign in failed.", e);
                updateUI(null);
            }
        }
    }

    private void FBAuthwGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "Firebase authentication with Google: " + account.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //sign in success, updates ui with the signed in users info
                            Log.d(TAG, "SignInWithCredential:success");
                            FirebaseUser user = fAuth.getCurrentUser();
                            updateUI(user);
                        }else{
                            //sign in fail, display message to user
                            Log.w(TAG, "SignInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void showProgressDialog() {
    }

    private void signIn(){
        Intent signInIntent = fGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut(){
        fAuth.signOut();
        fGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void removeAccess(){
        fAuth.signOut();
        fGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser currUser) {
        hideProgressDialog();
        if(currUser != null){
            fStatusTextView.setText(getString(R.string.google_status_fmt, currUser.getEmail()));
            fDetailsTextView.setText(getString(R.string.firebase_status_fmt, currUser.getUid()));
            findViewById(R.id.googleSignInButton).setVisibility(View.GONE);
            findViewById(R.id.googleSignOutandDisconnect).setVisibility(View.VISIBLE);
        }else{
            fStatusTextView.setText(R.string.signed_out);
            fDetailsTextView.setText(null);
            findViewById(R.id.googleSignInButton).setVisibility(View.VISIBLE);
            findViewById(R.id.googleSignOutandDisconnect).setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v){
        int i = v.getId();
        if(i == R.id.googleSignInButton){
            signIn();
        }else if(i == R.id.googleSignOutButton){
            signOut();
        }else if(i == R.id.googleDisconnectButton){
            removeAccess();
        }
    }
}
