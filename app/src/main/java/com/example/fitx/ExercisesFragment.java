package com.example.fitx;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.util.Vector;


public class ExercisesFragment extends Fragment {

    private ListView programList;

    //array lists for programs and exercises
    private ArrayList<String> programs;
    private ArrayAdapter<String> programsAdapter;
    private ArrayList<String> exercises;
    private ArrayAdapter<String> exercisesAdapter;

    //initialize and declare database reference
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    //initalized reference for Programs
    private DatabaseReference userPrograms;
    private DatabaseReference currentProgram;
    private DatabaseReference currentProgram_exercises;
    private TableLayout exerciseTable;
    private int tableposition = 1;



    private ValueEventListener programListener = new ValueEventListener(){
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            try {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                programsAdapter.addAll((String)ds.getKey());
            } catch (NullPointerException e) {

            }
        }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){

            }
    };

    private ValueEventListener exerciseListener = new ValueEventListener(){
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            exercisesAdapter.clear();
            exercisesAdapter.addAll((ArrayList<String>) dataSnapshot.getValue());

            try {
                //for printing to table
                Vector<String> data = new Vector<>();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                    data.add(ds.getValue().toString());

                int i = 0;
                for(int j=1; j <= data.size()/5; j++) {
                    TableRow r = (TableRow) exerciseTable.getChildAt(j);
                    for (int k = 0; k < 5; k++) {
                        TextView cell = (TextView) r.getChildAt(k);
                        cell.setText(data.get(i));
                        i++;
                    }
                }
                //////////////////////////
                data.clear();

            } catch (NullPointerException e) {

            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError){

        }
    };


    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_exercises, null);

        //initializing tablelayout
        exerciseTable = v.findViewById(R.id.exercise_table);

        //declare buttons on exercise page
        //initialize buttons for exercise page
        Button addExercise = v.findViewById(R.id.addexercise);
        Button editExercise = v.findViewById(R.id.editexercise);
        Button removeExercise = v.findViewById(R.id.removeexercise);
        Button addProgram = v.findViewById(R.id.addprogram);
        Button selectProgram = v.findViewById(R.id.selectprogram);
        Button removeProgram = v.findViewById(R.id.removeprogram);

        programList = v.findViewById(R.id.program_list);

        //get current user id
        String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());



        //declare programs reference
        programs = new ArrayList<>();
        userPrograms = db.getReference("Users").child(userid).child("Programs");
        userPrograms.addListenerForSingleValueEvent(programListener);
        programsAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, programs);
        programList.setAdapter(programsAdapter);
        programList.setOnItemClickListener((p, view, pos, id) -> {
            currentProgram = userPrograms.child(Objects.requireNonNull(programsAdapter.getItem(pos)));
            currentProgram_exercises = currentProgram.child("Exercises");
        });


        exercises = new ArrayList<>();
        exercisesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, exercises);





        //add program click listener
        addProgram.setOnClickListener(v1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
            builder.setTitle("Add Program");
            final EditText input = new EditText(this.getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("OK", (d, w) -> {
               String currProgram = input.getText().toString();
               programsAdapter.add(currProgram);
               currentProgram = userPrograms.child(currProgram);
            });
            builder.setNegativeButton("Cancel", (d, w) ->{
               d.cancel();
            });
            builder.show();
        });

        //add exercise click listener
        addExercise.setOnClickListener(v1 -> {
            LayoutInflater li = LayoutInflater.from(getContext());
            View addExerciseView = li.inflate(R.layout.add_exercise_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
            builder.setTitle("Add Exercise");

            builder.setView(addExerciseView);


            builder.setPositiveButton("OK", (d, w) -> {

                final EditText exerciseName = addExerciseView.findViewById(R.id.add_exercise_name);
                final EditText setNum = addExerciseView.findViewById(R.id.add_exercise_sets);
                final EditText repNum = addExerciseView.findViewById(R.id.add_exercise_reps);
                final EditText weightAmt = addExerciseView.findViewById(R.id.add_exercise_weight);
                String s1 = exerciseName.getText().toString();
                String s2 = setNum.getText().toString();
                String s3 = repNum.getText().toString();
                String s4 = weightAmt.getText().toString();
                String s5 = "";


                currentProgram_exercises = currentProgram.child("Exercises");
                currentProgram_exercises.addValueEventListener(exerciseListener);
                exercisesAdapter.add(s1);
                exercisesAdapter.add(s2);
                exercisesAdapter.add(s3);
                exercisesAdapter.add(s4);
                exercisesAdapter.add(s5);
                currentProgram_exercises.setValue(exercises);
            });
            builder.setNegativeButton("Cancel", (d, w) ->{
                d.cancel();
            });
            builder.show();
        });

        //remove exercise click listener
        removeExercise.setOnClickListener(v1 -> {

        });

        //remove program click listener
        removeProgram.setOnClickListener(v1 -> {

        });

        //edit exercise click listener
        editExercise.setOnClickListener(v1 -> {

        });

        //select program click listener
        selectProgram.setOnClickListener(v1 -> {

        });



        return v;
    }


}
