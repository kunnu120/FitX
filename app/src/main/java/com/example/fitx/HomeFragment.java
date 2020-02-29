package com.example.fitx;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
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

    private String loggingSets;
    private String loggingReps;
    private String loggingWeight;

    private int exerciseInfoIndex = 0;
    private boolean wasCalled;


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


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
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
        TextView percent = v.findViewById(R.id.percent);
        TextView progressLabel = v.findViewById(R.id.progressLabel);

        ProgressBar exerciseProgress = v.findViewById(R.id.progressBar2);


        String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        logButton.setOnClickListener(v1 -> {
            LayoutInflater li = LayoutInflater.from(getContext());
            View log = li.inflate(R.layout.log_layout, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
            builder.setTitle("Log Your Set");

            EditText numReps = log.findViewById(R.id.num_reps);
            EditText numWeight = log.findViewById(R.id.num_weight);

            builder.setView(log);


            builder.setPositiveButton("Log", (d, w) -> {
                double totalLogWeight = 0;
                double totalProgWeight = 0;
                double activityProgress = 0;
                double oldProgress = 0;
                double newProgress = 0;
                int logReps = 0;
                int logWeight = 0;

                int progSets = Integer.parseInt(String.valueOf(loggingSets));

                int progReps = Integer.parseInt(String.valueOf(loggingReps));

                int progWeight = Integer.parseInt(String.valueOf(loggingWeight));

                totalProgWeight = progSets * progReps * progWeight;
                ((logInfo) this.getActivity().getApplication()).setTotalProgWeight(totalProgWeight);
                totalProgWeight = ((logInfo) this.getActivity().getApplication()).getTotalProgWeight();
                System.out.println("Total Prog Weight: " + totalProgWeight);


                logReps = Integer.parseInt(numReps.getText().toString());
                logWeight = Integer.parseInt(numWeight.getText().toString());

                oldProgress = logReps * logWeight;
                if (((logInfo) this.getActivity().getApplication()).getTotalLogWeight() == 0) {
                    ((logInfo) this.getActivity().getApplication()).setTotalLogWeight(oldProgress);
                    totalLogWeight = ((logInfo) this.getActivity().getApplication()).getTotalLogWeight();
                    System.out.println("Total Log Weight: " + totalLogWeight);
                } else {
                    newProgress = ((logInfo) this.getActivity().getApplication()).getTotalLogWeight();
                    totalLogWeight = newProgress + oldProgress;
                    ((logInfo) this.getActivity().getApplication()).setTotalLogWeight(totalLogWeight);
                    totalLogWeight = ((logInfo) this.getActivity().getApplication()).getTotalLogWeight();
                    System.out.println("Total Log Weight: " + totalLogWeight);
                }

                //return totalLogWeight;

                DecimalFormat df = new DecimalFormat("#.##");
                activityProgress = totalLogWeight / totalProgWeight;
                activityProgress = Double.parseDouble(df.format(activityProgress));
                ((logInfo) this.getActivity().getApplication()).setActivityProgress((activityProgress));
                activityProgress = ((logInfo) this.getActivity().getApplication()).getActivityProgress();
                System.out.println("Activity Progress: " + activityProgress);


                if (wasCalled = true) {
                    double zero = 0;
                    double hundred = 100.0;
                    double progressBar = hundred * activityProgress;
                    int progressPercent = (int) progressBar;
                    percent.setText(progressPercent + "%");


                    System.out.println("Progress bar percentage: " + progressPercent);
                    if (progressBar <= 100) {
                        exerciseProgress.setProgress(progressPercent);
                        if (progressBar >= 100) {
                            wasCalled = false;
                            ((logInfo) this.getActivity().getApplication()).setTotalLogWeight(zero);
                        }
                    }
                }
            });


            builder.setNegativeButton("Cancel", (d, w) -> {
                d.cancel();
            });
            builder.show();


        });


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

            wasCalled = true;
            ((logInfo) this.getActivity().getApplication()).setTotalLogWeight(0);
            exerciseProgress.setProgress(0);
            percent.setText("0%");
            progressLabel.setText(exercisesAdapter.getItem(pos) + " Progress");

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
        loggingSets = "";
        loggingReps = "";
        loggingWeight = "";
        for (DataSnapshot dss : ds.getChildren()) {
            if (i == (index + 1)) {
                s1 = s1 + (dss.getValue().toString());
                loggingSets = dss.getValue().toString();
                sets.setText(s1);
            } else if (i == (index + 2)) {
                s2 = s2 + (dss.getValue().toString());
                loggingReps = dss.getValue().toString();
                reps.setText(s2);
            } else if (i == (index + 3)) {
                s3 = s3 + (dss.getValue().toString());
                loggingWeight = dss.getValue().toString();
                weight.setText(s3);
            }
            i++;

        }

    }

}
