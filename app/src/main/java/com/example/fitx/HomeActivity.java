package com.example.fitx;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    TextView textV;
    Button logoutButton;
    FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    //Menu BottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textV = findViewById(R.id.textView);
        logoutButton = findViewById(R.id.logoutButton);
        fAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        Toast.makeText(HomeActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_share:
                        Toast.makeText(HomeActivity.this, "Share", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_profile:
                        Toast.makeText(HomeActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        };

        final FirebaseUser user = fAuth.getCurrentUser();
        textV.setText(R.string.welcome);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fAuth.addAuthStateListener(authStateListener);
    }

    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            fAuth.removeAuthStateListener(authStateListener);
        }
    }
}
