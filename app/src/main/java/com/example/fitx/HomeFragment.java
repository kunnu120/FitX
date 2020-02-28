package com.example.fitx;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {

    //database references
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private DatabaseReference currentProgram;
    private DatabaseReference exerciseList;
    private DatabaseReference selectProgram;


    private ArrayList<String> programs;
    private ArrayList<String> exercises;
    private ArrayAdapter<String> programsAdapter;
    private ArrayAdapter<String> exercisesAdapter;
    private ListView programView;
    private ListView exerciseView;

    private TextView sets;
    private TextView reps;
    private TextView weight;

    private int exerciseInfoIndex = 0;


    private ValueEventListener programListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (programsAdapter != null) {
                programsAdapter.clear();
            }
            try {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    programsAdapter.addAll(ds.getKey());
                }


            } catch (NullPointerException e) {

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener exerciseListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            exercises = new ArrayList<>();
            if (exercisesAdapter != null) {
                exercisesAdapter.clear();
            }

            int index = 0;
            try {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (index == 0) {
                        exercises.add(Objects.requireNonNull(ds.getValue()).toString());
                    }
                    index++;
                    if (index == 5) {
                        index = 0;
                    }
                }

                exercisesAdapter.addAll(exercises);

                setExerciseViews(dataSnapshot, exerciseInfoIndex);

            } catch (NullPointerException e) {

            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };



    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, null);
        Button logoutButton = v.findViewById(R.id.logoutButton);
        Button logButton = v.findViewById(R.id.log);
        programView = v.findViewById(R.id.programList);
        exerciseView = v.findViewById(R.id.currentList);
        sets = v.findViewById(R.id.sets);
        reps = v.findViewById(R.id.reps);
        weight = v.findViewById(R.id.weight);
        ImageView calculator_view = v.findViewById(R.id.calculator_view);
        TextView calculator_total = v.findViewById(R.id.plate_total);

        String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();



        calculator_view.setOnClickListener(v1 -> {
            LayoutInflater li = LayoutInflater.from(getContext());
            View plateCalc = li.inflate(R.layout.plate_calculator_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
            builder.setTitle("Plate Calculator");

            EditText num45 = plateCalc.findViewById(R.id.num_45_plates);
            EditText num25 = plateCalc.findViewById(R.id.num_25_plates);
            EditText num10 = plateCalc.findViewById(R.id.num_10_plates);
            EditText num5 = plateCalc.findViewById(R.id.num_5_plates);
            EditText num2half = plateCalc.findViewById(R.id.num_2_5_plates);

            builder.setView(plateCalc);
            builder.setPositiveButton("Done", (d,w)->{
                double totalplateweight = 0;
                if(num45.getText().toString().equals("")) {
                    int plates45 = 0;
                }else{
                    int plates45 = Integer.parseInt(num45.getText().toString());
                    totalplateweight += (45.0*plates45);
                }

                if(num25.getText().toString().equals("")){
                    int plates25 = 0;
                }else{
                    int plates25 = Integer.parseInt(num25.getText().toString());
                    totalplateweight += (25.0*plates25);
                }

                if(num10.getText().toString().equals("")){
                    int plates10 = 0;
                }else{
                    int plates10 = Integer.parseInt(num10.getText().toString());
                    totalplateweight += (10.0*plates10);
                }

                if(num5.getText().toString().equals("")){
                    int plates5 = 0;
                }else{
                    int plates5 = Integer.parseInt(num5.getText().toString());
                    totalplateweight += (5.0*plates5);
                }

                if(num2half.getText().toString().equals("")){
                    int plates2_5 = 0;
                }else{
                    int plates2_5 = Integer.parseInt(num2half.getText().toString());
                    totalplateweight += (2.5*plates2_5);
                }


                String plateResult = "Total: " + totalplateweight;
                calculator_total.setText(plateResult);
            });
            builder.setNegativeButton("Cancel", (d,w)->{
                d.cancel();
            });
            builder.show();


        });



        programs = new ArrayList<>();
        currentProgram = db.getReference("Users").child(userid).child("Programs");
        currentProgram.addValueEventListener(programListener);
        programsAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_list_item_1, programs);
        programView.setAdapter(programsAdapter);
        programView.setOnItemClickListener((p, view, pos, id) -> {
            exercises = new ArrayList<>();

            selectProgram = currentProgram.child(Objects.requireNonNull(programsAdapter.getItem(pos)));
            exerciseList = selectProgram.child("Exercises");

            exercisesAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_list_item_1, exercises);
            exerciseList.addValueEventListener(exerciseListener);

            exerciseView.setAdapter(exercisesAdapter);
        });


        exerciseView.setOnItemClickListener((p, view, pos, id) -> {
            exerciseInfoIndex = pos * 5;
            //selectProgram = currentProgram.child(Objects.requireNonNull(programsAdapter.getItem(pos)));
            exerciseList = selectProgram.child("Exercises");

            exercisesAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_list_item_1, exercises);
            exerciseList.addValueEventListener(exerciseListener);

            exerciseView.setSelection(pos);
            exerciseView.setAdapter(exercisesAdapter);
            exerciseView.setSelection(pos);

        });





        logoutButton.setOnClickListener(v1 -> {
            fAuth.signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
        });
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        return v;

    }


    public static String dummyFunction() {
        return "This is a useful test.";
    }


    private void setExerciseViews(DataSnapshot ds, int index) {
        int i = 0;
        String s1 = "Sets: ";
        String s2 = "Reps: ";
        String s3 = "Weight: ";
        for (DataSnapshot dss : ds.getChildren()) {
            if (i == (index + 1)) {
                s1 = s1 + (dss.getValue().toString());
                sets.setText(s1);
            } else if (i == (index + 2)) {
                s2 = s2 + (dss.getValue().toString());
                reps.setText(s2);
            } else if (i == (index + 3)) {
                s3 = s3 + (dss.getValue().toString());
                weight.setText(s3);
            }
            i++;

        }
    }

}
