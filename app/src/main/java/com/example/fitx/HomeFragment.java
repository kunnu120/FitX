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
import android.widget.Toast;

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
import java.util.Vector;

//This class is used for the home page of FitX
public class HomeFragment extends Fragment {

    //database references
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private DatabaseReference currentProgram;
    private DatabaseReference exerciseList;
    private DatabaseReference selectProgram;

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
    private int progSets = 0;
    private int progReps = 0;
    private int progWeight = 0;
    private double totalProgWeight = 0.0;
    private int currentSets = 0;
    private int currentReps = 0;
    private int currentWeight = 0;
    private double totalLogWeight = 0.0;
    private int currentExerciseProgress = 0;
    private int currentProgramProgress = 0;
    private boolean switchedPrograms = false;

    //Gets data from firebase for user's programs every time the program data changes
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
                //Checks if there are no programs & assigns a default one
                if(programsAdapter.getCount() == 0) {
                    programsAdapter.add("No Programs");
                    programView.performItemClick(programView, 0, R.id.programList);
                    exerciseView.performItemClick(exerciseView, 0, R.id.currentList);
                }
                //performs a click on the program listview
                if(programsAdapter.getCount() ==1 && !(programsAdapter.getItem(0).equals("No Programs"))){
                    programView.performItemClick(programView, 0, R.id.programList);

                }
                //Removes default program and exercise if user has programmed at least one
                if(programsAdapter.getCount()>1){
                    for(int i=0; i< programsAdapter.getCount(); i++){
                        if(programsAdapter.getItem(i).equals("No Programs")){
                            programsAdapter.remove("No Programs");
                            exercisesAdapter.remove("No Exercises");
                        }
                    }
                }

            } catch (NullPointerException e) {

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //Gets data from firebase for user's exercises
    private ValueEventListener exerciseListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            exercises = new ArrayList<>();

                if (exercisesAdapter != null) {
                    exercisesAdapter.clear();
                }
                //variables for calculating total weight for use in logging and progress bars
                Vector<Integer> setsvect = new Vector<>();
                Vector<Integer> repsvect = new Vector<>();
                Vector<Integer> weightsvect = new Vector<>();
                progReps = 0;
                progSets = 0;
                progWeight = 0;

                //Gets a snapshot of the exercises data every time it is changed
                //Adds the exercise info to the vector for calculation later
                int index = 0;
                try {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (index == 0) {
                            exercises.add(Objects.requireNonNull(ds.getValue()).toString());
                        }

                        if(index == 1){
                            progSets = Integer.parseInt(ds.getValue().toString());
                            setsvect.add(progSets);
                        }
                        if(index == 2){
                            progReps = Integer.parseInt(ds.getValue().toString());
                            repsvect.add(progReps);
                        }
                        if(index == 3){

                            progWeight = Integer.parseInt(ds.getValue().toString());
                            weightsvect.add(progWeight);
                        }

                        index++;
                        if (index == 4) {

                            index = 0;
                        }

                    }
                    totalProgWeight = 0;
                    //calculates the total program weight from the vectors
                    for(int i=0; i<setsvect.size(); i++){
                        int a = setsvect.elementAt(i);
                        int b = repsvect.elementAt(i);
                        int c = weightsvect.elementAt(i);
                        totalProgWeight += (a*b*c);
                    }

                    exercisesAdapter.addAll(exercises);
                    //Adds a default exercise if there are no programs
                    if(programsAdapter.getItem(0).equals("No Programs")){
                        exercisesAdapter.add("No Exercises");
                    }
                    //passes the data from the snapshot to this method which populates the
                    //sets reps and weight for the currently selected exercise
                    setExerciseViews(dataSnapshot, exerciseInfoIndex);

                } catch (NullPointerException e) {

                }
                //If the program is switched the first exercise is selected
                if(switchedPrograms){
                    exerciseView.performItemClick(exerciseView, 0, R.id.currentList);
                    switchedPrograms = false;
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
    //Inflates the page, buttons, progress bars, and listviews
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

        TextView percent1 = v.findViewById(R.id.percent1);
        TextView percent2 = v.findViewById(R.id.percent2);
        TextView progressLabel1 = v.findViewById(R.id.progressLabel1);
        TextView progressLabel2 = v.findViewById(R.id.progressLabel2);

        ProgressBar exerciseProgress = v.findViewById(R.id.progressBar1);
        ProgressBar progProgress = v.findViewById(R.id.progressBar2);

        String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        //Initializes array for programs & gets reference for selected program from firebase
        ArrayList<String> programs = new ArrayList<>();
        currentProgram = db.getReference("Users").child(userid).child("Programs");
        currentProgram.addValueEventListener(programListener);
        programsAdapter = new ArrayAdapter<>(this.requireActivity(), android.R.layout.simple_list_item_1, programs);
        programView.setAdapter(programsAdapter);

            //When log button is clicked, allows the user to enter numbers for exercises and reflects
            // in program exercise progress bars
            logButton.setOnClickListener(v1 -> {
                if(programsAdapter.getItem(0).equals("No Programs")){
                    Toast t = Toast.makeText(getContext(),"Cant log without a Program and Exercises",Toast.LENGTH_SHORT);
                    t.show();
                }else {
                    LayoutInflater li = LayoutInflater.from(getContext());
                    View log = li.inflate(R.layout.log_layout, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
                    builder.setTitle("Log Your Set");

                    EditText numReps = log.findViewById(R.id.num_reps);
                    EditText numWeight = log.findViewById(R.id.num_weight);

                    builder.setView(log);

                    //Runs code when user clicks the log button in the log alert dialog box
                    builder.setPositiveButton("Log", (d, w) -> {

                        double activityPercent = 0.0;
                        double programPercent = 0.0;

                        try {
                            //math for user entered log numbers
                            int currentlogweight = Integer.parseInt(numReps.getText().toString()) *
                                    Integer.parseInt(numWeight.getText().toString());
                            currentExerciseProgress = currentExerciseProgress + currentlogweight;
                            currentProgramProgress = currentProgramProgress + currentlogweight;
                            activityPercent = currentExerciseProgress / totalLogWeight;
                            programPercent = currentProgramProgress / totalProgWeight;

                        } catch (NumberFormatException nfe) {
                            Toast.makeText(this.getActivity(), "Please Log both Reps and Weight", Toast.LENGTH_SHORT).show();
                            d.cancel();
                        }
                        //Creates decimal format for progress bars
                        DecimalFormat df = new DecimalFormat("#.##");
                        activityPercent = Double.parseDouble(df.format(activityPercent));
                        DecimalFormat df1 = new DecimalFormat("#.##");
                        programPercent = Double.parseDouble(df1.format(programPercent));

                        //sets progress for exercise and programs
                        double hundred = 100.0;
                        double progressBar = hundred * activityPercent;
                        int progressPercent = (int) progressBar;
                        if (progressPercent >= 100.0) {
                            Toast t = Toast.makeText(getContext(), "Reached Your Goal! Move on to next exercise or keep grinding!", Toast.LENGTH_SHORT);
                            t.show();
                        }
                        percent1.setText(progressPercent + "%");

                        System.out.println("Progress bar percentage: " + progressPercent);
                        if (progressBar <= 100) {
                            exerciseProgress.setProgress(progressPercent);
                        }
                        if (progressBar > 100) {
                            exerciseProgress.setProgress(100);
                        }

                        double hundred1 = 100.0;
                        double programProgress = hundred1 * programPercent;
                        int programProgressPercent = (int) programProgress;
                        if(programProgressPercent >= 100) {
                            Toast t = Toast.makeText(getContext(), "You completed 100% of your program! You're a true FitX athlete!", Toast.LENGTH_SHORT);
                            t.show();
                        }
                        percent2.setText(programProgressPercent + "%");
                        progProgress.setProgress(programProgressPercent);

                    });
                    //Cancels when user hits the cancel button in the log alert dialog box
                    builder.setNegativeButton("Cancel", (d, w) -> {
                        d.cancel();
                    });
                    builder.show();
                }


            });


        //Runs code when user hits the plate calculator button
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
            //Math for when user hits done in plate calculator alert dialog box
            //Adds up and multiplies the amount of plates entered to get the total
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
            //Cancels when the user hits the cancel button in plate calculator alert dialog box
            builder.setNegativeButton("Cancel", (d,w)->{
                d.cancel();
            });
            builder.show();


        });
        //Runs code when user selects a program from the program listview
        programView.setOnItemClickListener((p, view, pos, id) -> {
            switchedPrograms = true;
            currentProgramProgress = 0;
            exercises = new ArrayList<>();

            selectProgram = currentProgram.child(Objects.requireNonNull(programsAdapter.getItem(pos)));
            exerciseList = selectProgram.child("Exercises");
            //updates the exercises adapter with the exercises list for the exercises view
            exercisesAdapter = new ArrayAdapter<>(this.requireActivity(), android.R.layout.simple_list_item_1, exercises);
            exerciseList.addValueEventListener(exerciseListener);
            exerciseView.setAdapter(exercisesAdapter);

            exerciseProgress.setProgress(0);
            percent1.setText("0%");
            //Sets default program progress bar
            if(!programsAdapter.getItem(0).equals("No Programs")) {
                progressLabel2.setText(programsAdapter.getItem(pos) + " Progress");
                progProgress.setProgress(0);
                percent2.setText("0%");
            }else{
                progressLabel2.setText("Add a program on the next page");
                percent2.setText("0%");
                progProgress.setProgress(0);
            }
        });

        //Runs code when user selects and exercise from exercise listview
        exerciseView.setOnItemClickListener((p, view, pos, id) -> {

            exerciseInfoIndex = pos * 4;
            exerciseList = selectProgram.child("Exercises");

            //updates the exercises adapter with the exercises list for the exercises view
            exercisesAdapter = new ArrayAdapter<>(this.requireActivity(), android.R.layout.simple_list_item_1, exercises);
            exerciseList.addValueEventListener(exerciseListener);


            exerciseView.setAdapter(exercisesAdapter);
            exerciseView.setSelection(pos);
            //Sets exercise program progress bar label and percentage
            if(!programsAdapter.getItem(0).equals("No Programs")) {
                exerciseProgress.setProgress(0);
                percent1.setText("0%");
                progressLabel1.setText(exercisesAdapter.getItem(pos) + " Progress");
            }else{
                progressLabel1.setText("Add an exercise to a program");
            }
        });
        //Logs the user out when clicking the logout button
        logoutButton.setOnClickListener(v1 -> {
            fAuth.signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
        });

        return v;

    }

    //Test code
    public static String dummyFunction() {
        return "This is a useful test.";
    }
    //Sets the numbers for sets, reps, and weight for the selected exercise
    private void setExerciseViews(DataSnapshot ds, int index) {
        int i = 0;
        String s1 = "Sets: ";
        String s2 = "Reps: ";
        String s3 = "Weight: ";
        loggingSets = "";
        loggingReps = "";
        loggingWeight = "";
        currentSets = 0;
        currentReps = 0;
        currentWeight = 0;
        //Checks if there is at least one program then creates variables for use in logging
        if(!programsAdapter.getItem(0).equals("No Programs")) {
            for (DataSnapshot dss : ds.getChildren()) {
                if (i == (index + 1)) {
                    s1 = s1 + (dss.getValue().toString());
                    loggingSets = dss.getValue().toString();
                    currentSets = Integer.parseInt(loggingSets);
                    sets.setText(s1);
                } else if (i == (index + 2)) {
                    s2 = s2 + (dss.getValue().toString());
                    loggingReps = dss.getValue().toString();
                    currentReps = Integer.parseInt(loggingReps);
                    reps.setText(s2);
                } else if (i == (index + 3)) {
                    s3 = s3 + (dss.getValue().toString());
                    loggingWeight = dss.getValue().toString();
                    currentWeight = Integer.parseInt(loggingWeight);
                    weight.setText(s3);
                }
                i++;
            }
            currentExerciseProgress = 0;
            totalLogWeight = 0;
            totalLogWeight = currentSets * currentReps * currentWeight;
        }else{
            sets.setText(s1);
            reps.setText(s2);
            weight.setText(s3);
        }

    }

}
