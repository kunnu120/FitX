package com.example.fitx;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.androidx.fitx.R;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import javax.annotation.Nullable;

public class RegisterActivity extends AppCompatActivity {

    EditText emailField, passwordField;
    Button registerButton, loginButton;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailField = (EditText) findViewById(R.id.email);
        passwordField = (EditText) findViewById(R.id.password);
        registerButton = (Button) findViewById(R.id.registerButton);
        loginButton = (Button) findViewById(R.id.loginButton);

        fAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Please fill in the required fields.", Toast.LENGTH_SHORT ).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Please fill in the required fields.", Toast.LENGTH_SHORT ).show();
                }

                if(password.length() < 6){
                    Toast.makeText(getApplicationContext(), "Password must be at least 6 characters.", Toast.LENGTH_SHORT ).show();
                }

                fAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class ));
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "Email or password is wrong, try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        loginButton.setOnClickListener(v1 -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));

        if(fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }
}
