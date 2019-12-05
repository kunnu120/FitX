package com.example.fitx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import androidx.viewpager.widget.ViewPager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

//implements BottomNavigationView.OnNavigationItemSelectedListener
public class HomeActivity extends AppCompatActivity {


    BottomNavigationView bottomNav;
    ViewPager2 viewPager;
    PageAdapter adapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    Button logoutButton;
    FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logoutButton = findViewById(R.id.logoutButton);


        //initializing view pager
        viewPager = findViewById(R.id.viewpager);

        //initializing bottom navigation view
        bottomNav = findViewById(R.id.navigation);
        bottomNav.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            viewPager.setCurrentItem(0);
                            break;
                        case R.id.navigation_exercises:
                            viewPager.setCurrentItem(1);
                            break;
                        case R.id.navigation_programs:
                            viewPager.setCurrentItem(2);
                            break;
                        case R.id.navigation_profile:
                            viewPager.setCurrentItem(3);
                            break;
                        case R.id.navigation_social:
                            viewPager.setCurrentItem(4);
                            break;
                    }
                    return false;
                });

        fragments.add(new HomeFragment());
        fragments.add(new ExercisesFragment());
        fragments.add(new ProgramsFragment());
        fragments.add(new ProfileFragment());
        fragments.add(new SocialFragment());
        adapter = new PageAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setOrientation(viewPager.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(new MarginPageTransformer(1500));



        fAuth = FirebaseAuth.getInstance();

        authStateListener = firebaseAuth -> {
            FirebaseUser user = fAuth.getCurrentUser();
            if (user == null) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        };


        logoutButton.setOnClickListener(v -> {
            fAuth.signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
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

