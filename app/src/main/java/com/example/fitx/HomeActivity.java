package com.example.fitx;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.fitx.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    TextView textV;
    Button logoutButton;
    FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textV = findViewById(R.id.textView);
        logoutButton = findViewById(R.id.logoutButton);

        //bottom navigation code
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        //NavigationUI.setupWithNavController(bottomNav, navController);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
        
        fAuth = FirebaseAuth.getInstance();

        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        };

        final FirebaseUser user = fAuth.getCurrentUser();
        textV.setText(R.string.welcome);

        logoutButton.setOnClickListener(v -> {
            fAuth.signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.navigation_social:
                        selectedFragment = new SocialFragment();
                        break;
                    case R.id.navigation_profile:
                        selectedFragment = new UserProfileFragment();
                        break;

                    case R.id.navigation_home:
                        selectedFragment = new UserHomeFragment();
                        break;

                    case R.id.navigation_exerciseprogram:
                        selectedFragment = new ExerciseProgramFragment();
                        break;

                    case R.id.navigation_exercisedatabase:
                        selectedFragment = new ExerciseDatabaseFragment();
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }

                return true;
            };

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
