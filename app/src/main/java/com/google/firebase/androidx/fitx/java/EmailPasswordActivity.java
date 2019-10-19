package com.google.firebase.androidx.fitx.java;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.androidx.fitx.R;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class EmailPasswordActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "emailpassword";

    private EditText emailField;
    private EditText passwordField;
    private TextView status_TextView;
    private TextView detail_TextView;

    private FirebaseAuth fAuth;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);

        //views
        emailField = findViewById(R.id.fieldEmail);
        passwordField = findViewById(R.id.fieldPassword);
        status_TextView = findViewById(R.id.status);
        detail_TextView = findViewById(R.id.detail);

        //buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.emailRegisterAccountButton).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.verifyEmailButton).setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onStart(){
        super.onStart();
        //check if user is signed in and updates ui
        FirebaseUser currUser = fAuth.getCurrentUser();
        updateUI(currUser);
    }

    private void registerAccount(String email, String password){
        Log.d(TAG, "Create Account: " + email);
        if(!validateForm()){
            return;
        }

        showProgressDialog();

        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //sign in success, updates ui with user info
                            Log.d(TAG, "Create User with Email:success");
                            FirebaseUser currUser = fAuth.getCurrentUser();
                            updateUI(currUser);
                        }else{
                            //sign in fail, displays message to user
                            Log.w(TAG, "Create User with Email:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void signIn(String email, String password){
        Log.d(TAG, "Sign In: " + email);
        if(!validateForm()){
            return;
        }
        showProgressDialog();

        fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //sign in successful, updates ui with signed in users info
                            Log.d(TAG,"Sign In with Email:success");
                            FirebaseUser currUser = fAuth.getCurrentUser();
                            updateUI(currUser);
                        }else{
                            //sign in fails, displays a message to the user
                            Log.w(TAG, "Sign In With Email:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        if(!task.isSuccessful()){
                            status_TextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signOut(){
        fAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification(){
        //disable button
        findViewById(R.id.verifyEmailButton).setEnabled(false);

        //send email verification
        final FirebaseUser currUser = fAuth.getCurrentUser();
        currUser.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //re enable button
                        findViewById(R.id.verifyEmailButton).setEnabled(true);

                        if(task.isSuccessful()){
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Verification email sent to " + currUser.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Log.e(TAG, "Send Email Verification", task.getException());
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private boolean validateForm() {
        boolean valid = true;
        String email = emailField.getText().toString();
        if(TextUtils.isEmpty(email)){
            emailField.setError("Required.");
            valid = false;
        }else{
            emailField.setError(null);
        }
        return valid;
    }

    private void updateUI(FirebaseUser currUser) {
        hideProgressDialog();
        if(currUser != null){
            status_TextView.setText(getString(R.string.emailpassword_status_fmt,
                    currUser.getEmail(), currUser.isEmailVerified()));
            detail_TextView.setText(getString(R.string.firebase_status_fmt, currUser.getUid()));

            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.verifyEmailButton).setEnabled(!currUser.isEmailVerified());
        }else{
            status_TextView.setText(R.string.signed_out);
            detail_TextView.setText(null);

            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
            findViewById(R.id.signedInButtons).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v){
        int i = v.getId();
        if(i == R.id.emailRegisterAccountButton){
            registerAccount(emailField.getText().toString(), passwordField.getText().toString());
        }else if(i == R.id.emailSignInButton){
            signIn(emailField.getText().toString(), passwordField.getText().toString());
        }else if(i == R.id.signOutButton){
            signOut();
        }else if(i == R.id.verifyEmailButton){
            sendEmailVerification();
        }
    }
}
