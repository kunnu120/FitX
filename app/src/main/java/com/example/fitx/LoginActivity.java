package com.example.fitx;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "";
    private static final int RC_SIGN_IN = 9001;
    EditText emailField, passwordField;
    Button loginButton, registerButton, forgotPasswordButton;
    ProgressBar progressBar;
    GoogleSignInClient gsiClient;
    private FirebaseAuth fAuth;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference saltRef;
    //private DatabaseReference emailRef;
    //private DatabaseReference passwordRef;
    //private DatabaseReference UIDRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressbar);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);


        fAuth = FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsiClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(v -> GoogleSignIn());

        loginButton.setOnClickListener(v -> signIn(emailField.getText().toString(), passwordField.getText().toString()));

        registerButton.setOnClickListener(v -> createAccount(emailField.getText().toString(), passwordField.getText().toString()));

        findViewById(R.id.signOutButton).setOnClickListener(v -> signOut());


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "Sign In: " + email);
        if (validateForm()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //sign up new users
        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = fAuth.getCurrentUser();
                        if (user != null) {
                            //saves email and password in database after signup
                            String userid = Objects.requireNonNull(user.getUid());
                            //emailRef = db.getReference("Users").child(userid).child("Email");
                            //passwordRef = db.getReference("Users").child(userid).child("Password");
                            //UIDRef = db.getReference("Users").child(userid).child("UID");
                            //emailRef.setValue(email);
                            //passwordRef.setValue(password);
                            //UIDRef.setValue(userid);
                            saltRef = db.getReference("Users").child(userid).child("PrivateSalt");
                            byte[] newSalt = Security.generateRandomSalt();
                            saltRef.setValue(Security.encB64(newSalt));
                            Security.generateKey(newSalt,password);
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            updateUI(user);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }


                    progressBar.setVisibility(View.GONE);
                });
    }


    private void signIn(String email, String password) {
        Log.i(TAG, "Sign In: " + email);
        if (validateForm()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //sign in existing users
        fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = fAuth.getCurrentUser();
                        if (user != null) {
                            String userid = Objects.requireNonNull(user.getUid());
                            saltRef = db.getReference("Users").child(userid).child("PrivateSalt");
                            saltRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    byte[] salt = Security.decB64(dataSnapshot.getValue(String.class));
                                    Security.generateKey(salt,password);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            updateUI(user);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                    if (!task.isSuccessful()) emailField.setText(R.string.auth_failure);
                    progressBar.setVisibility(View.GONE);
                });
    }


    //if(fAuth.getCurrentUser() != null){
    //    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    //}

    private void signOut() {
        fAuth.signOut();
        updateUI(null);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }
        return !valid;
    }


    //Google sign in
    private void GoogleSignIn() {
        Intent intent = gsiClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    //good, for google sign in
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    authThruGoogle(account);
                }
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }


    private void authThruGoogle(GoogleSignInAccount acct) {
        Log.i(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = fAuth.getCurrentUser();
                        if (user != null) {
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            //} else {
                            // updateUI(null);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        //Toast.makeText( "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }


                });
    }


    private void updateUI(FirebaseUser account) {
        progressBar.setVisibility(View.GONE);
        if (account != null) {
            emailField.setText(getString(R.string.google_status_fmt, account.getEmail()));
            passwordField.setText(getString(R.string.firebase_status_fmt, account.getUid()));

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.signOutButton).setVisibility(View.VISIBLE);
        } else {
            emailField.setText("");
            passwordField.setText(null);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

}