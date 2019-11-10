package com.example.fitx;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class ForgotPasswordActivity extends AppCompatActivity {
    EditText emailField;
    Button newPasswordButton;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        emailField = findViewById(R.id.emailField);
        newPasswordButton = findViewById(R.id.newPasswordLink);

        fAuth = FirebaseAuth.getInstance();

        newPasswordButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Please fill the Email field.", Toast.LENGTH_SHORT).show();
                return;
            }

            fAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Password reset link was sent to your email address.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Email sending error.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
