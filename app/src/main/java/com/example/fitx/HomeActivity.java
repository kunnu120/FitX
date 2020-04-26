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

//implements BottomNavigationView.OnNavigationItemSelectedListener
public class HomeActivity extends AppCompatActivity {

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


   /* private ChildEventListener BMIListener = new ChildEventListener() {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            int index = 0;
            for(DataSnapshot ds : dataSnapshot.getChildren()) {

                if(index == 0) {
                    editTextAge.setText(ds.getValue().toString());
                } else if(index == 1) {
                    textViewResult.setText(ds.getValue().toString());
                } else if(index == 2) {
                    if(ds.getValue().toString().equals("Male")) {
                        dataField4.setSelection(0);
                    } else if(ds.getValue().toString().equals("Female")) {
                        dataField4.setSelection(1);
                    } else {
                        dataField4.setSelection(2);
                    }

                } else if(index == 3) {
                    editTextHeight.setText(ds.getValue().toString());
                } else if(index == 4) {
                    editTextWeight.setText(ds.getValue().toString());
                }
                index++;
                System.out.println("code " + ds.getValue().toString());
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            int index = 0;
            for(DataSnapshot ds : dataSnapshot.getChildren()) {

                if(index == 0) {
                    editTextAge.setText(ds.getValue().toString());
                } else if(index == 1) {
                    textViewResult.setText(ds.getValue().toString());
                } else if(index == 2) {
                    if(ds.getValue().toString().equals("Male")) {
                        dataField4.setSelection(0);
                    } else if(ds.getValue().toString().equals("Female")) {
                        dataField4.setSelection(1);
                    } else {
                        dataField4.setSelection(2);
                    }

                } else if(index == 3) {
                    editTextHeight.setText(ds.getValue().toString());
                } else if(index == 4) {
                    editTextWeight.setText(ds.getValue().toString());
                }
                index++;
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int index = 0;
            for(DataSnapshot ds : dataSnapshot.getChildren()) {

                if(index == 0) {
                    editTextAge.setText(ds.getValue().toString());
                } else if(index == 1) {
                    textViewResult.setText(ds.getValue().toString());
                } else if(index == 2) {
                    if(ds.getValue().toString().equals("Male")) {
                        dataField4.setSelection(0);
                    } else if(ds.getValue().toString().equals("Female")) {
                        dataField4.setSelection(1);
                    } else {
                        dataField4.setSelection(2);
                    }

                } else if(index == 3) {
                    editTextHeight.setText(ds.getValue().toString());
                } else if(index == 4) {
                    editTextWeight.setText(ds.getValue().toString());
                }
                index++;
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int index = 0;
            for(DataSnapshot ds : dataSnapshot.getChildren()) {

                if(index == 0) {
                    editTextAge.setText(ds.getValue().toString());
                } else if(index == 1) {
                    textViewResult.setText(ds.getValue().toString());
                } else if(index == 2) {
                    if(ds.getValue().toString().equals("Male")) {
                        dataField4.setSelection(0);
                    } else if(ds.getValue().toString().equals("Female")) {
                        dataField4.setSelection(1);
                    } else {
                        dataField4.setSelection(2);
                    }

                } else if(index == 3) {
                    editTextHeight.setText(ds.getValue().toString());
                } else if(index == 4) {
                    editTextWeight.setText(ds.getValue().toString());
                }
                index++;
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }

    }; */

    public void buttonClick2(View v) {
        a = (EditText) findViewById(R.id.userHeight);
        b = (EditText) findViewById(R.id.userWeight);
        c = (EditText) findViewById(R.id.user_age);
        //d = (EditText) findViewById(R.id.);
        btn = (Button) findViewById(R.id.button3);
        reff = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BMI");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String val = dataSnapshot.child("userHeight").getValue().toString();
                String val2 = dataSnapshot.child("userWeight").getValue().toString();
                String val3 = dataSnapshot.child("age").getValue().toString();
                //    String val4 = dataSnapshot.child("bmi").getValue().toString();
                a.setText(val + " inches");
                b.setText(val2 + " lbs");
                c.setText(val3 + " years");
                // d.setText(val4 + " bmi");
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

        //databaseReference = db.getReference("Users");
        //initializing view pager
        viewPager = findViewById(R.id.viewpager);

        //retrieving bmi values
        editTextHeight = (EditText) findViewById(R.id.userHeight);

        TextView textViewResult = (TextView) findViewById(R.id.userBMI);

      //  a = (TextView) findViewById(R.id.heighttext);
     //   btn = (Button) findViewById(R.id.button3);

     /*   btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reff = FirebaseDatabase.getInstance().getReference().child("Users").child("BMI");
                reff.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String he = dataSnapshot.child("userHeight").getValue().toString();
                        a.setText(he);
                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        }); */


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

    public void buttonClick(View v) {


        editTextHeight = (EditText) findViewById(R.id.userHeight);
        editTextWeight = (EditText) findViewById(R.id.userWeight);
        textViewResult = (TextView) findViewById(R.id.userBMI);
        editTextAge = (EditText) findViewById(R.id.user_age);
        double height = 0.0;
        double weight = 0.0;
       if(!editTextHeight.getText().toString().equals("") && editTextHeight.getText().toString().length() > 0 )
        {
            // Get String
            height = Double.parseDouble(editTextHeight.getText().toString());
        }

        if(!editTextWeight.getText().toString().equals("") && editTextWeight.getText().toString().length() > 0 )
        {
            // Get String
            weight = Double.parseDouble(editTextWeight.getText().toString());
        }

        //double weight = Double.parseDouble(editTextWeight.getText().toString());
        double BMI = (weight * 703 )/ (height * height);

        dataField = findViewById(R.id.userHeight);
        dataField2 = findViewById(R.id.userWeight);
        EditText dataField3 = findViewById(R.id.user_age);
        dataField4 = findViewById(R.id.spinner1);
        String bmitext = Double.toString(BMI);
        String dataFieldText = dataField.getText().toString();
        String dataFieldText2 = dataField2.getText().toString();
        String dataFieldText3 = dataField3.getText().toString();
        String dataFieldText4 = dataField4.getSelectedItem().toString();



        //String dataFieldText5 = toString();
        String BMI2 = String.valueOf(BMI);
        BMI2 = String.format("%.2f", BMI);
      //  String dataFieldText4 = dataField4.toString();
        //String heighttext = Double.toString(height);
      //  String weighttext = Double.toString(weight);
        //String bmitext = Double.toString(BMI);
        //String agetext = editTextAge.getText().toString();

    //    String id = databaseReference.push().getKey();

        if(!TextUtils.isEmpty(dataFieldText)) {
            Data data = new Data(dataFieldText,dataFieldText2,dataFieldText3,dataFieldText4,BMI2);
            DatabaseReference currentFirebaseUser = db.getReference().child("Users");
            String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference currentUser = currentFirebaseUser.child(uID);
            DatabaseReference currentUserBMI = currentUser.child("BMI");
        //    currentUserBMI.addChildEventListener(BMIListener);
            currentUserBMI.setValue(data);




        } else {
            Toast.makeText(HomeActivity.this, "Please enter the data", Toast.LENGTH_SHORT).show();
        }
     //   textViewResult.setText(Double.toString(BMI));

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

        //String BMI2 = String.valueOf(BMI);
       // BMI2 = String.format("%.2f", BMI);
        calculation = BMI2 + "\n" + BMIResult;
        textViewResult.setText(calculation);



        //resulttext.setText(calculation);

    }





}

