package com.google.firebase.androidx.fitx.java;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.TextView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.androidx.fitx.R;



public class OurAuthActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "OurAuthActivity";

    private FirebaseAuth fAuth;
    private String fToken;
    private TokenBroadcastReceiver fTokenReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ourauth);

        //button click listeners
        findViewById(R.id.signInButton).setOnClickListener(this);

        //creates token receiver
        fTokenReceiver = new TokenBroadcastReceiver() {
            @Override
            public void newToken(String token) {
                Log.d(TAG, "newToken:" + token);
                setToken(token);
            }

        };

        fAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart(){
        super.onStart();
        //check if user is signed in and updates ui
        FirebaseUser user = fAuth.getCurrentUser();
        updateUI(user);
    }

    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(fTokenReceiver, TokenBroadcastReceiver.getFilter());
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(fTokenReceiver);
    }

    private void doSignIn(){
        //initiate sign in with token
        fAuth.signInWithCustomToken(fToken)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //sign in success, updates ui with the signed in users info
                            Log.d(TAG, "signInWithCustomToken:success");
                            FirebaseUser user = fAuth.getCurrentUser();
                            updateUI(user);
                        }else{
                            //sign in failed, displays message to the user
                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(OurAuthActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            ((TextView) findViewById(R.id.signInStatus)).setText("User ID: " + user.getUid());
        }else{
            ((TextView) findViewById(R.id.signInStatus)).setText("Error: sign in failed.");
        }
    }

    private void setToken(String token) {
        fToken = token;
        String status;
        if(fToken != null){
            status = "Token: " + fToken;
        }else{
            status = "Token: null"
        }

        findViewById(R.id.signInButton).setEnabled((fToken!=null));
        ((TextView) findViewById(R.id.tokenStatus)).setText(status);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.signInButton){
            doSignIn();
        }
    }
}
