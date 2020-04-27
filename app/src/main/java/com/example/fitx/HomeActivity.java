package com.example.fitx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import android.view.View;
import android.widget.Toast;

import static android.app.ProgressDialog.show;

//Creaing class homeactivity that extends appcompatacitivity
public class HomeActivity extends AppCompatActivity {

    //Initializing global variables that we need
    DatabaseReference reff;
    EditText dataField;
    EditText editTextHeight;
    EditText editTextWeight;
    EditText editTextAge;
    TextView textViewResult;
    EditText dataField2;
    Spinner dataField4;
    BottomNavigationView bottomNav;
    ViewPager2 viewPager;
    PageAdapter adapter;
    private DatabaseReference heig;
    TextView a,b,c,d,f;
    Button btn;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private FirebaseAuth.AuthStateListener authStateListener;
    String BMIResult, calculation;


    //When clicked on the showInfo  button on profile page, this function will be called
    public void buttonClick2(View v) {

        //getting info from the edittext content - height, weight and age
        a = (EditText) findViewById(R.id.userHeight);
        b = (EditText) findViewById(R.id.userWeight);
        c = (EditText) findViewById(R.id.user_age);

        btn = (Button) findViewById(R.id.button3);

        //getting info from the database firebase
        reff = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BMI");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //saving the info retrieved from the database and storing them in the strings
                String val = dataSnapshot.child("userHeight").getValue().toString();
                String val2 = dataSnapshot.child("userWeight").getValue().toString();
                String val3 = dataSnapshot.child("age").getValue().toString();
                    String val4 = dataSnapshot.child("gender").getValue().toString();

                //Printing out the information when clicked on show info to editText
                if(!val.contains("inches")) {
                    a.setText(val + " inches");
                } else
                    a.setText(val);

                if(!val2.contains("lbs")) {
                    b.setText(val2 + " lbs");
                } else
                    b.setText(val2);

                if(!val3.contains("years")) {
                    c.setText(val3 + " years");
                } else
                    c.setText(val3);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //a.setText("hi");

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //initializing view pager
        viewPager = findViewById(R.id.viewpager);

        //retrieving bmi values
        editTextHeight = (EditText) findViewById(R.id.userHeight);
        TextView textViewResult = (TextView) findViewById(R.id.userBMI);


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
        viewPager.setOffscreenPageLimit(4);

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

    @Override
    public void onBackPressed() {}

    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            fAuth.removeAuthStateListener(authStateListener);
        }
    }

    //When clicked on BMI button, it will call this function
    public void buttonClick(View v) {

        //getting info from the editext view and setting them to this editText variables
        editTextHeight = (EditText) findViewById(R.id.userHeight);
        editTextWeight = (EditText) findViewById(R.id.userWeight);
        textViewResult = (TextView) findViewById(R.id.userBMI);
        editTextAge = (EditText) findViewById(R.id.user_age);

        //Initializing height and weight as doubles
        double height = 0.0;
        double weight = 0.0;

       //if height has some values in it, we convert that value to a string
       if(!editTextHeight.getText().toString().equals("") && editTextHeight.getText().toString().length() > 0 )
        {
            // Get String
            String substrHeight = editTextHeight.getText().toString();
            String temp = "";

            //Going through the string to check when we see it has space, we break the loop so that we only get numbers values in our string 50 inches --> see a space and save the 50 to the string
            for(int i = 0;i<substrHeight.length();i++) {
                if(substrHeight.charAt(i) == ' ')
                    break;
                else
                    temp+=substrHeight.charAt(i);
            }
            height = Double.parseDouble(temp);
        }

        //if weight has some values in it, we convert that value to a string
        if(!editTextWeight.getText().toString().equals("") && editTextWeight.getText().toString().length() > 0 )
        {
            // Get String
            String substrWeight = editTextWeight.getText().toString();
            String temp2 = "";

            //Going through the string to check when we see it has space, we break the loop so that we only get numbers values in our string 110 lbs --> see a space and save the 50 to the string
            for(int i = 0;i<substrWeight.length();i++) {
                if(substrWeight.charAt(i) == ' ')
                    break;
                else
                    temp2+=substrWeight.charAt(i);
            }

            weight = Double.parseDouble(temp2);
        }

        //Calculate the BMI
        double BMI = (weight * 703 )/ (height * height);
        dataField = findViewById(R.id.userHeight);
        dataField2 = findViewById(R.id.userWeight);
        EditText dataField3 = findViewById(R.id.user_age);
        dataField4 = findViewById(R.id.spinner1);
        String bmitext = Double.toString(BMI);

        //Converting the datafield to strings and saving to newly created string variables
        String dataFieldText = dataField.getText().toString();
        String dataFieldText2 = dataField2.getText().toString();
        String dataFieldText3 = dataField3.getText().toString();
        String dataFieldText4 = dataField4.getSelectedItem().toString();
        String BMI2 = String.valueOf(BMI);

        //Converting the BMI double to string so we can show it to the user in textview
        BMI2 = String.format("%.2f", BMI);

        //Saving the user's info(height, weight, age, gender and BMI to the firebase!
        if(!TextUtils.isEmpty(dataFieldText)) {
            Data data = new Data(dataFieldText,dataFieldText2,dataFieldText3,dataFieldText4,BMI2);
            DatabaseReference currentFirebaseUser = db.getReference().child("Users");
            String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference currentUser = currentFirebaseUser.child(uID);
            DatabaseReference currentUserBMI = currentUser.child("BMI");
            currentUserBMI.setValue(data);
        } else {
            Toast.makeText(HomeActivity.this, "Please enter the data", Toast.LENGTH_SHORT).show();
        }

        //What category the user's falls into based on the information provided.
        if(Double.isNaN(BMI)) {
            BMIResult = "Can't determine";
        }
        else if(BMI < 16) {
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

        //Saving the result in String calculation
        calculation = BMI2 + "\n" + BMIResult;
        //Viewing the resulted string to the textview
        textViewResult.setText(calculation);



    }





}

