package com.example.fitx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.design.widget.BottomNavigationView
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.fitx.R;

public class HomeActivity extends AppCompatActivity {
    TextView textV;
    Button logoutButton;
    FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textV = (TextView) findViewById(R.id.textView);
        logoutButton = (Button) findViewById(R.id.logoutButton);

        fAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        };

        final FirebaseUser user = fAuth.getCurrentUser();
        textV.setText(R.string.welcome);

        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        fAuth.addAuthStateListener(authStateListener);
    }

    protected void onStop(){
        super.onStop();
        if(authStateListener != null){
            fAuth.removeAuthStateListener(authStateListener);
        }
    }
}
