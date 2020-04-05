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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    String BMIResult, calculation;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        databaseReference = db.getReference();
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

        EditText editTextHeight = (EditText) findViewById(R.id.userHeight);
        EditText editTextWeight = (EditText) findViewById(R.id.userWeight);
        TextView textViewResult = (TextView) findViewById(R.id.userBMI);

        double height = Double.parseDouble(editTextHeight.getText().toString());
        double weight = Double.parseDouble(editTextWeight.getText().toString());
        double BMI = (weight * 703 )/ (height * height);

     //   textViewResult.setText(Double.toString(BMI));

        if(BMI < 16) {
            BMIResult = "Severely Under Weight";
        } else if(BMI < 19.5) {
            BMIResult = "Under Weight";
        } else if(BMI >= 19.5 && BMI <= 24.9) {
            BMIResult = "Normal Weight";
        } else if(BMI >= 25 && BMI <=29.9) {
            BMIResult = "Over Weight";
        } else {
            BMIResult = "Obese";
        }

        String BMI2 = String.valueOf(BMI);
        BMI2 = String.format("%.2f", BMI);
        calculation = BMI2 + "\n" + BMIResult;
        textViewResult.setText(calculation);
        //resulttext.setText(calculation);
    }




}

