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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.fitx.R;


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
        findViewById(R.)
    }

}
