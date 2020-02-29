package com.example.fitx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import android.view.View;
import android.widget.Toast;

//implements BottomNavigationView.OnNavigationItemSelectedListener
public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    ViewPager2 viewPager;
    PageAdapter adapter;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(new MarginPageTransformer(1500));


        authStateListener = firebaseAuth -> {
            FirebaseUser user = fAuth.getCurrentUser();
            if (user == null) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        };



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

    public void buttonClick(View v) {
        EditText editTextHeight = (EditText) findViewById(R.id.user_height);
        EditText editTextWeight = (EditText) findViewById(R.id.user_weight);
        TextView textViewResult = (TextView) findViewById(R.id.userBMI);

        double height = Double.parseDouble(editTextHeight.getText().toString());
        double weight = Double.parseDouble(editTextWeight.getText().toString());

        double BMI = weight / (height * height);

        textViewResult.setText(Double.toString(BMI));
    }




}

