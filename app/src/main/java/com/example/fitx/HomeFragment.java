package com.example.fitx;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.tv.TvContract;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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


public class HomeFragment extends Fragment {

    //database references
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private DatabaseReference currentProgram;
    private DatabaseReference exerciseList;
    private DatabaseReference selectProgram;
    private DatabaseReference exerciseNumbers;
    private SharedPreferences sharedPreference;


    private ArrayList<String> exercises;
    private ArrayList<String> exercises2;
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
    private double progress = 0;
    private boolean exerciseCalled;
    private boolean programCalled;
    private double programProgress;
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


                if(programsAdapter.getCount() == 0) {
                    programsAdapter.add("No Programs");
                    programView.performItemClick(programView, 0, R.id.programList);
                    exerciseView.performItemClick(exerciseView, 0, R.id.currentList);
                }

                if(programsAdapter.getCount() ==1 && !(programsAdapter.getItem(0).equals("No Programs"))){
                    programView.performItemClick(programView, 0, R.id.programList);

                }


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

    private ValueEventListener exerciseListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            exercises = new ArrayList<>();

                if (exercisesAdapter != null) {
                    exercisesAdapter.clear();
                }
                //new code
                Vector<Integer> setsvect = new Vector<>();
                Vector<Integer> repsvect = new Vector<>();
                Vector<Integer> weightsvect = new Vector<>();
                progReps = 0;
                progSets = 0;
                progWeight = 0;

                //new code above
                int index = 0;
                try {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (index == 0) {
                            exercises.add(Objects.requireNonNull(ds.getValue()).toString());
                        }
                        //new code
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
                        //new code above
                        index++;
                        if (index == 5) {

                            index = 0;
                        }

                    }
                    totalProgWeight = 0;
                    //new code
                    for(int i=0; i<setsvect.size(); i++){
                        int a = setsvect.elementAt(i);
                        int b = repsvect.elementAt(i);
                        int c = weightsvect.elementAt(i);
                        totalProgWeight += (a*b*c);
                    }


                    exercisesAdapter.addAll(exercises);


                    if(programsAdapter.getItem(0).equals("No Programs")){
                        exercisesAdapter.add("No Exercises");
                    }

                    setExerciseViews(dataSnapshot, exerciseInfoIndex);

                } catch (NullPointerException e) {

                }

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

        ArrayList<String> programs = new ArrayList<>();
        currentProgram = db.getReference("Users").child(userid).child("Programs");
        currentProgram.addValueEventListener(programListener);
        programsAdapter = new ArrayAdapter<>(this.requireActivity(), android.R.layout.simple_list_item_1, programs);
        programView.setAdapter(programsAdapter);



            logButton.setOnClickListener(v1 -> {
                LayoutInflater li = LayoutInflater.from(getContext());
                View log = li.inflate(R.layout.log_layout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
                builder.setTitle("Log Your Set");

                EditText numReps = log.findViewById(R.id.num_reps);
                EditText numWeight = log.findViewById(R.id.num_weight);

                builder.setView(log);


                builder.setPositiveButton("Log", (d, w) -> {

                    for(int i =0; i <= exercises.size() - 1; i++)  {
                        System.out.println("ExerciseTest " + exercises.get(i));
                    }

                    double activityPercent = 0.0;
                    double programPercent = 0.0;



                    if(loggingSets == null || loggingReps == null || loggingWeight == null) {
                        Toast.makeText(this.getActivity(), "Choose a program", Toast.LENGTH_SHORT).show();
                    }

                    //new code
                    int currentlogweight = Integer.parseInt(numReps.getText().toString())*
                            Integer.parseInt(numWeight.getText().toString());
                    currentExerciseProgress = currentExerciseProgress + currentlogweight;
                    currentProgramProgress = currentProgramProgress + currentlogweight;
                    activityPercent = currentExerciseProgress/totalLogWeight;
                    programPercent = currentProgramProgress/totalProgWeight;

                    ///////new code above




                    DecimalFormat df = new DecimalFormat("#.##");
                    activityPercent = Double.parseDouble(df.format(activityPercent));
                    DecimalFormat df1 = new DecimalFormat("#.##");
                    programPercent = Double.parseDouble(df1.format(programPercent));



                        double hundred = 100.0;
                        double progressBar = hundred * activityPercent;
                        int progressPercent = (int) progressBar;
                        if(progressPercent >= 100.0){
                            Toast t = Toast.makeText(getContext(),"Reached Your Goal! Move on to next exercise or keep grinding!", Toast.LENGTH_SHORT);
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
                        percent2.setText(programProgressPercent + "%");
                        progProgress.setProgress(programProgressPercent);

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







        programView.setOnItemClickListener((p, view, pos, id) -> {
            switchedPrograms = true;
            currentProgramProgress = 0;
            exercises = new ArrayList<>();

            selectProgram = currentProgram.child(Objects.requireNonNull(programsAdapter.getItem(pos)));
            exerciseList = selectProgram.child("Exercises");

            exercisesAdapter = new ArrayAdapter<>(this.requireActivity(), android.R.layout.simple_list_item_1, exercises);
            exerciseList.addValueEventListener(exerciseListener);
            exerciseView.setAdapter(exercisesAdapter);

            exerciseProgress.setProgress(0);
            percent1.setText("0%");

            if(!programsAdapter.getItem(0).equals("No Programs")) {
                progressLabel2.setText(programsAdapter.getItem(pos) + " Progress");
                progProgress.setProgress(0);
                percent2.setText("0%");
                programCalled = true;
            }else{
                progressLabel2.setText("Add a program on the next page");
                percent2.setText("0%");

            }
        });

   /*     final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!programsAdapter.isEmpty()) {
                        programView.performItemClick(programView, 0, programView.getAdapter().getItemId(0));
                    }
                } catch(NullPointerException e) {

                }
            }
        }, 1500); */


        exerciseView.setOnItemClickListener((p, view, pos, id) -> {

            exerciseInfoIndex = pos * 5;
            //selectProgram = currentProgram.child(Objects.requireNonNull(programsAdapter.getItem(pos)));
            exerciseList = selectProgram.child("Exercises");

            exercisesAdapter = new ArrayAdapter<>(this.requireActivity(), android.R.layout.simple_list_item_1, exercises);
            exerciseList.addValueEventListener(exerciseListener);


            exerciseView.setAdapter(exercisesAdapter);
            exerciseView.setSelection(pos);

            if(!programsAdapter.getItem(0).equals("No Programs")) {
                exerciseCalled = true;
                exerciseProgress.setProgress(0);
                percent1.setText("0%");
                progressLabel1.setText(exercisesAdapter.getItem(pos) + " Progress");
            }else{
                exerciseCalled = true;
                progressLabel1.setText("Add an exercise to a program");
            }
        });

 /*       final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!exercisesAdapter.isEmpty()) {
                        exerciseView.performItemClick(exerciseView, 0, exerciseView.getAdapter().getItemId(0));
                    }
                }catch (NullPointerException e) {

                }
            }
        }, 1600);   */

        logoutButton.setOnClickListener(v1 -> {
            fAuth.signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
        });

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
        currentSets = 0;
        currentReps = 0;
        currentWeight = 0;
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
