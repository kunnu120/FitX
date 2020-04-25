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
    private DatabaseReference emailRef;
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
        progressBar.setVisibility(View.GONE);
        FirebaseUser account = fAuth.getCurrentUser();
        GoogleSignInAccount currentUser = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            updateUI(account);
        }else if(currentUser != null){
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
                            String uid = Objects.requireNonNull(user.getUid());
                            emailRef = db.getReference("Users").child(uid).child("Email");
                            emailRef.setValue(email);
                            //passwordRef = db.getReference("Users").child(userid).child("Password");
                            //UIDRef = db.getReference("Users").child(userid).child("UID");
                            //passwordRef.setValue(password);
                            //UIDRef.setValue(userid);
                            saltRef = db.getReference("Users").child(uid).child("PrivateSalt");
                            saltRef.addListenerForSingleValueEvent(saltListener(password,uid));
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            updateUI(user);
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI((FirebaseUser) null);
                        progressBar.setVisibility(View.GONE);
                    }


                    progressBar.setVisibility(View.GONE);
                });
    }


    private void signIn(String email, String password) {
        Log.d(TAG, "Sign In: " + email);
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
                            String uid = Objects.requireNonNull(user.getUid());
                            emailRef = db.getReference("Users").child(uid).child("Email");
                            emailRef.setValue(email);
                            saltRef = db.getReference("Users").child(uid).child("PrivateSalt");
                            saltRef.addListenerForSingleValueEvent(saltListener(password,uid));
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            updateUI(user);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        updateUI((FirebaseUser) null);
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
        updateUI((FirebaseUser) null);
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
        Toast t1 = Toast.makeText(getApplicationContext(), "Google Sign In: No Password Required", Toast.LENGTH_SHORT);
        t1.show();
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = gsiClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    //good, for google sign in
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    updateUI(account);
                    authThruGoogle(account);
                }
            } catch (ApiException e) {
                Toast t = Toast.makeText(getApplicationContext(), "Google Sign In Failed", Toast.LENGTH_SHORT);
                t.show();
                Log.w(TAG, "Google sign in failed", e);
                //updateUI((GoogleSignInAccount) null);
            }
        }
    }


    private void authThruGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getEmail());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = fAuth.getCurrentUser();
                        if (user != null) {
                            String uid = fAuth.getCurrentUser().getUid();
                            emailRef = db.getReference("Users").child(uid).child("Email");
                            emailRef.setValue(acct.getEmail());
                            saltRef = db.getReference("Users").child(uid).child("PrivateSalt");
                            saltRef.addListenerForSingleValueEvent(saltListener(acct.getId(),uid));
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            updateUI(user);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        //Toast.makeText( "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        updateUI((GoogleSignInAccount) null);
                    }


                });
    }

    private void updateUI(GoogleSignInAccount account) {
        progressBar.setVisibility(View.GONE);
        if (account != null) {
            emailField.setText(account.getEmail());
            //passwordField.setText(getString(R.string.firebase_status_fmt, account.getUid()));
        } else {
            emailField.setText("");
            // passwordField.setText(null);
        }
    }

    private void updateUI(FirebaseUser account) {
        progressBar.setVisibility(View.GONE);
        if (account != null) {
            emailField.setText(account.getEmail());
            //passwordField.setText(getString(R.string.firebase_status_fmt, account.getUid()));

        } else {
            emailField.setText("");
           // passwordField.setText(null);

        }
    }

    private ValueEventListener saltListener(String key, String uid) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                byte[] salt;
                if (dataSnapshot.exists()) {
                    salt = Security.decB64(dataSnapshot.getValue(String.class));
                } else {
                    saltRef = db.getReference("Users").child(uid).child("PrivateSalt");
                    salt = Security.generateRandomSalt();
                    saltRef.setValue(Security.encB64(salt));
                }
                Security.generateKey(salt,key);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
    }

}