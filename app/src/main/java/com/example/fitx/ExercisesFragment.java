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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
    private ArrayList<String> new_exercises;
    private ArrayAdapter<String> new_exercisesAdapter;

    //initialize and declare database reference
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    //initalized reference for Programs
    private DatabaseReference userPrograms;
    private DatabaseReference currentProgram;
    private DatabaseReference currentProgram_exercises;
    private DatabaseReference userFullfillment;
    private DatabaseReference FF_currentProgram;
    private DatabaseReference FF_currentProgram_exercises;
    private TableLayout exerciseTable;
    private ArrayList<String> ff_array;
    private ArrayAdapter<String> ffAdapter;

    private ChildEventListener fullfillmentListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if(ffAdapter != null){
                ffAdapter.clear();
                ffAdapter.addAll((ArrayList<String>)dataSnapshot.getValue());
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ChildEventListener tableSwitchListener = new ChildEventListener() {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            if(exercisesAdapter != null) {
                exercisesAdapter.clear();
                exercisesAdapter.addAll((ArrayList<String>) dataSnapshot.getValue());
            }

                Vector<String> data = new Vector<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    data.add(ds.getValue().toString());
                }
                int i = 0;
                for(int j=1; j <= data.size()/5; j++) {
                    TableRow r = (TableRow) exerciseTable.getChildAt(j);
                    for (int k = 0; k < 5; k++) {
                        TextView cell = (TextView) r.getChildAt(k);
                        cell.setText(data.get(i));
                        i++;
                    }
                }
                data.clear();

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            Vector<String> data = new Vector<>();
            for(DataSnapshot ds : dataSnapshot.getChildren()){
                data.add(ds.getValue().toString());
            }
            int i = 0;
            for(int j=1; j <= data.size()/5; j++) {
                TableRow r = (TableRow) exerciseTable.getChildAt(j);
                for (int k = 0; k < 5; k++) {
                    TextView cell = (TextView) r.getChildAt(k);
                    cell.setText(data.get(i));
                    i++;
                }
            }
            data.clear();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            //clears the table for new program
            for(int j=1; j <= 12; j++) {
                TableRow r = (TableRow) exerciseTable.getChildAt(j);
                for (int k = 0; k < 5; k++) {
                    TextView cell = (TextView) r.getChildAt(k);
                    cell.setText("");
                }
            }



            Vector<String> data = new Vector<>();
            for(DataSnapshot ds : dataSnapshot.getChildren()){
                data.add(ds.getValue().toString());
            }

            for(int i=0; i < 5; i++) {
                data.removeElementAt(data.size() - 1);
            }

            int i = 0;
            for(int j=1; j <= data.size()/5; j++) {
                TableRow r = (TableRow) exerciseTable.getChildAt(j);
                for (int k = 0; k < 5; k++) {
                    TextView cell = (TextView) r.getChildAt(k);
                    cell.setText(data.get(i));
                    i++;
                }
            }
            data.clear();

            if(programList.getCount() == 0){
                for(int j=1; j <= 12; j++) {
                    TableRow r = (TableRow) exerciseTable.getChildAt(j);
                    for (int k = 0; k < 5; k++) {
                        TextView cell = (TextView) r.getChildAt(k);
                        cell.setText("");
                    }
                }
            }

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener programListener = new ValueEventListener(){
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(programsAdapter.isEmpty()){
                programsAdapter.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    programsAdapter.addAll((String) ds.getKey());
                }
                if(programsAdapter.getCount()>0) {
                    programList.performItemClick(programList, 0, R.id.program_list);
                }
                programsAdapter.clear();
            }

            try {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    programsAdapter.addAll((String) ds.getKey());
                }


            } catch (NullPointerException e) {

            }
        }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){

            }
    };




    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        Button removeProgram = v.findViewById(R.id.removeprogram);

        programList = v.findViewById(R.id.program_list);


        //fullfillment array and adapter
        ff_array = new ArrayList<>();
        ffAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, ff_array);

        //get current user id
        String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());


        //declare fullfillment references
        userFullfillment = db.getReference("Users").child(userid).child("Exercise Fullfillment");


        //declare programs reference
        programs = new ArrayList<>();
        userPrograms = db.getReference("Users").child(userid).child("Programs");
        userPrograms.addListenerForSingleValueEvent(programListener);
        programsAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, programs);
        programList.setAdapter(programsAdapter);



        programList.setOnItemClickListener((p, view, pos, id) -> {

            //clears table before switch
            for(int j=1; j <= 12; j++) {
                TableRow r = (TableRow) exerciseTable.getChildAt(j);
                for (int k = 0; k < 5; k++) {
                    TextView cell = (TextView) r.getChildAt(k);
                    cell.setText("");
                }
            }


            currentProgram = userPrograms.child(Objects.requireNonNull(programsAdapter.getItem(pos)));
            currentProgram_exercises = currentProgram.child("Exercises");
            currentProgram.addChildEventListener(tableSwitchListener);

            //fullfillment
            FF_currentProgram = userFullfillment.child(Objects.requireNonNull(programsAdapter.getItem(pos)));
            FF_currentProgram_exercises = FF_currentProgram.child("Exercises");
            FF_currentProgram.addChildEventListener(fullfillmentListener);


        });


        //add program click listener
        addProgram.setOnClickListener(v1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
            builder.setTitle("Add Program");
            final EditText input = new EditText(this.getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("Add", (d, w) -> {

               exercises = new ArrayList<>();
               exercisesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, exercises);
               String currProgram = input.getText().toString();
               boolean program_exists = false;
               for(int i=0; i<programsAdapter.getCount(); i++){
                   if(programsAdapter.getItem(i).equals(currProgram)){
                       program_exists = true;
                   }

               }
               if(!program_exists) {
                   programsAdapter.add(currProgram);
                   currentProgram = userPrograms.child(currProgram);
                   currentProgram_exercises = currentProgram.child("Exercises");
                   currentProgram.addChildEventListener(tableSwitchListener);
                   programList.setSelection(programList.getCount() - 1);

                   //fullfillment array and adapter
                   ff_array = new ArrayList<>();
                   ffAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, ff_array);

                   FF_currentProgram = userFullfillment.child(currProgram);
                   FF_currentProgram_exercises = FF_currentProgram.child("Exercises");

                   //forced add exercise box
                   LayoutInflater li = LayoutInflater.from(getContext());
                   View addExerciseView = li.inflate(R.layout.add_exercise_dialog, null);
                   AlertDialog.Builder forcedbuilder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
                   forcedbuilder.setTitle("Add Exercise");

                   forcedbuilder.setView(addExerciseView);


                   forcedbuilder.setPositiveButton("Add", (u, t) -> {

                       if (exercisesAdapter == null) {

                           Vector<String> data = new Vector<>();

                           for (int j = 1; j <= 12; j++) {
                               TableRow r = (TableRow) exerciseTable.getChildAt(j);
                               TextView startCell = (TextView) r.getChildAt(0);
                               for (int k = 0; k < 5; k++) {
                                   if (startCell.getText().toString().equals("")) {
                                       j = 13;
                                   }
                                   TextView cell = (TextView) r.getChildAt(k);
                                   String cellData = cell.getText().toString();
                                   data.add(cellData);
                               }
                           }
                           //removes the 5 empty spaces added at the end of the vector
                           for (int i = 0; i < 5; i++) {
                               data.removeElementAt(data.size() - 1);
                           }

                           exercises = new ArrayList<>();
                           exercisesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, exercises);
                           for (int i = 0; i < data.size(); i++) {
                               exercisesAdapter.add(data.elementAt(i));
                           }
                           data.clear();
                       }

                       final EditText exerciseName = addExerciseView.findViewById(R.id.add_exercise_name);
                       final EditText setNum = addExerciseView.findViewById(R.id.add_exercise_sets);
                       final EditText repNum = addExerciseView.findViewById(R.id.add_exercise_reps);
                       final EditText weightAmt = addExerciseView.findViewById(R.id.add_exercise_weight);
                       String s1 = exerciseName.getText().toString();
                       String s2 = setNum.getText().toString();
                       String s3 = repNum.getText().toString();
                       String s4 = weightAmt.getText().toString();
                       String s5 = "";


                       exercisesAdapter.add(s1);
                       exercisesAdapter.add(s2);
                       exercisesAdapter.add(s3);
                       exercisesAdapter.add(s4);
                       exercisesAdapter.add(s5);
                       currentProgram_exercises.setValue(exercises);


                       //fullfillment
                       final int numofentries = Integer.parseInt(s2);
                       if (numofentries != 0) {
                           for (int f = 0; f < numofentries; ++f) {
                               ff_array.add(s1);
                               ff_array.add(s2);
                               ff_array.add(s3);
                               ff_array.add(s4);
                               ff_array.add(s5);
                           }
                           FF_currentProgram_exercises.setValue(ff_array);
                       }
                   });
                   forcedbuilder.show();

                   //clears the table for new program
                   for(int j=1; j <= 12; j++) {
                       TableRow r = (TableRow) exerciseTable.getChildAt(j);
                       for (int k = 0; k < 5; k++) {
                           TextView cell = (TextView) r.getChildAt(k);
                           cell.setText("");
                       }
                   }
                   //////////////////////////////////
               }else{
                   Toast t = Toast.makeText(getContext(), "Program name entered already exists. Please enter a different name", Toast.LENGTH_LONG);
                   t.show();
                   d.cancel();
                   addProgram.performClick();

               }
            });
            //end of forced dialog

            builder.show();

        });

        //add exercise click listener
        addExercise.setOnClickListener(v1 -> {
            LayoutInflater li = LayoutInflater.from(getContext());
            View addExerciseView = li.inflate(R.layout.add_exercise_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
            builder.setTitle("Add Exercise");

            builder.setView(addExerciseView);


            builder.setPositiveButton("Add", (d, w) -> {

                if(exercisesAdapter == null){

                    Vector<String> data = new Vector<>();

                    for(int j=1; j <= 12; j++) {
                        TableRow r = (TableRow) exerciseTable.getChildAt(j);
                        TextView startCell = (TextView)r.getChildAt(0);
                        for (int k = 0; k < 5; k++) {
                            if(startCell.getText().toString().equals("")){
                                j = 13;
                            }
                            TextView cell = (TextView) r.getChildAt(k);
                            String cellData = cell.getText().toString();
                            data.add(cellData);
                        }
                    }
                    //removes the 5 empty spaces added at the end of the vector
                    for(int i=0; i < 5; i++) {
                        data.removeElementAt(data.size() - 1);
                    }

                    exercises = new ArrayList<>();
                    exercisesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, exercises);
                    for(int i=0; i < data.size(); i++) {
                        exercisesAdapter.add(data.elementAt(i));
                    }
                    data.clear();
                }

                final EditText exerciseName = addExerciseView.findViewById(R.id.add_exercise_name);
                final EditText setNum = addExerciseView.findViewById(R.id.add_exercise_sets);
                final EditText repNum = addExerciseView.findViewById(R.id.add_exercise_reps);
                final EditText weightAmt = addExerciseView.findViewById(R.id.add_exercise_weight);
                String s1 = exerciseName.getText().toString();
                String s2 = setNum.getText().toString();
                String s3 = repNum.getText().toString();
                String s4 = weightAmt.getText().toString();
                String s5 = "";


                exercisesAdapter.add(s1);
                exercisesAdapter.add(s2);
                exercisesAdapter.add(s3);
                exercisesAdapter.add(s4);
                exercisesAdapter.add(s5);
                currentProgram_exercises.setValue(exercises);


                //fullfillment
                final int numofentries = Integer.parseInt(s2);
                if(numofentries != 0) {
                    for (int f = 0; f < numofentries; ++f) {
                        ff_array.add(s1);
                        ff_array.add(s2);
                        ff_array.add(s3);
                        ff_array.add(s4);
                        ff_array.add(s5);
                    }
                    FF_currentProgram_exercises.setValue(ff_array);
                }




            });
            builder.setNegativeButton("Cancel", (d, w) ->{
                d.cancel();
            });
            builder.show();
        });

        //remove exercise click listener
        removeExercise.setOnClickListener(v1 -> {
            LayoutInflater li = LayoutInflater.from(getContext());
            View removeExercisePrompt = li.inflate(R.layout.edit_exercise_prompt, null);
            AlertDialog.Builder editPrompt = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
            editPrompt.setTitle("Remove What Exercise?");
            TextView input = removeExercisePrompt.findViewById(R.id.edit_exercise_name);
            editPrompt.setView(removeExercisePrompt);
            editPrompt.setPositiveButton("Remove", (d,w)->{
                String exerciseStr = input.getText().toString();
                exerciseToDelete(exerciseStr);
            });
            editPrompt.setNegativeButton("Cancel", (d,w)->{
                d.cancel();
            });
            editPrompt.show();
        });



        //remove program click listener
        removeProgram.setOnClickListener(v1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
            builder.setTitle("Remove What Program?");
            final EditText input = new EditText(this.getContext());
            input.setHint("Program Name");
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("Delete", (d, w) ->{
                String currProgram = input.getText().toString();
                currentProgram.setValue(null);
                programsAdapter.remove(currProgram);
                //fullfillment program removal
                FF_currentProgram.setValue(null);
                ffAdapter.remove(currProgram);

                if(programsAdapter.getCount() > 0) {
                    //clears the table
                    for(int j=1; j <= 12; j++) {
                        TableRow r = (TableRow) exerciseTable.getChildAt(j);
                        for (int k = 0; k < 5; k++) {
                            TextView cell = (TextView) r.getChildAt(k);
                            cell.setText("");
                        }
                    }
                    programList.setSelection(0);
                    programList.performItemClick(programList, 0, R.id.program_list);
                }else{
                    //clears the table
                    for(int j=1; j <= 12; j++) {
                        TableRow r = (TableRow) exerciseTable.getChildAt(j);
                        for (int k = 0; k < 5; k++) {
                            TextView cell = (TextView) r.getChildAt(k);
                            cell.setText("");
                        }
                    }
                }


            });
            builder.setNegativeButton("Cancel", (d, w) ->{
                d.cancel();
            });
            builder.show();
        });


        //edit exercise click listener
        editExercise.setOnClickListener(v1 -> {
            LayoutInflater li = LayoutInflater.from(getContext());
            View editExercisePrompt = li.inflate(R.layout.edit_exercise_prompt, null);
            AlertDialog.Builder editPrompt = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
            editPrompt.setTitle("Edit What Exercise?");
            TextView input = editExercisePrompt.findViewById(R.id.edit_exercise_name);
            editPrompt.setView(editExercisePrompt);
            editPrompt.setPositiveButton("Edit", (d,w)->{
                String exerciseStr = input.getText().toString();
                editEnteredExercise(exerciseStr);
            });
            editPrompt.setNegativeButton("Cancel", (d,w)->{
               d.cancel();
            });
            editPrompt.show();


        });


        return v;
    }

    private void exerciseToDelete(String exerciseStr){

        //copy whole table into exercises adapter for editing
        Vector<String> data = new Vector<>();

        for(int h=1; h <= 12; h++) {
            TableRow row = (TableRow) exerciseTable.getChildAt(h);
            TextView namecell = (TextView)row.getChildAt(0);
            for (int k = 0; k < 5; k++) {
                if(namecell.getText().toString().equals("")){
                    h = 13;
                }
                TextView cell = (TextView) row.getChildAt(k);
                String cellData = cell.getText().toString();
                data.add(cellData);
            }
        }
        //removes the 5 empty spaces added at the end of the vector
        for(int i=0; i < 5; i++) {
            data.removeElementAt(data.size() - 1);
        }

        int databaseIndex = 0;
        exercises = new ArrayList<>();
        exercisesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, exercises);
        for(int m=0; m < data.size(); ++m){
            exercisesAdapter.add(data.elementAt(m));
        }

        for(int p=0; p < exercisesAdapter.getCount(); ++p){
            if(exercisesAdapter.getItem(p).equals(exerciseStr)){
                databaseIndex = p;
            }
        }

        final int start = databaseIndex;

        new_exercises = new ArrayList<>();
        new_exercisesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, new_exercises);


        for(int a = 0; a < exercisesAdapter.getCount(); ++a){
            if(((a >= start) && (a <= (start+4)))){

            }else{
                new_exercisesAdapter.add(exercisesAdapter.getItem(a));
            }
        }

        exercisesAdapter.clear();
        currentProgram_exercises.setValue(new_exercises);

        //for updating the table after updating firebase exercise data
        if(programList.getCount()>0) {
            int current = programList.getLastVisiblePosition();
            programList.performItemClick(programList, current, R.id.program_list);
        }

    }





    private void editEnteredExercise(String exerciseStr){

        //copy whole table into exercises adapter for editing
        Vector<String> data = new Vector<>();

        for(int h=1; h <= 12; h++) {
            TableRow row = (TableRow) exerciseTable.getChildAt(h);
            TextView namecell = (TextView)row.getChildAt(0);
            for (int k = 0; k < 5; k++) {
                if(namecell.getText().toString().equals("")){
                    h = 13;
                }
                TextView cell = (TextView) row.getChildAt(k);
                String cellData = cell.getText().toString();
                data.add(cellData);
            }
        }
        //removes the 5 empty spaces added at the end of the vector
        for(int i=0; i < 5; i++) {
            data.removeElementAt(data.size() - 1);
        }

        int databaseIndex = 0;
        exercises = new ArrayList<>();
        exercisesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, exercises);
        for(int m=0; m < data.size(); ++m){
            exercisesAdapter.add(data.elementAt(m));
        }

        for(int p=0; p < exercisesAdapter.getCount(); ++p){
            if(exercisesAdapter.getItem(p).equals(exerciseStr)){
                databaseIndex = p;
            }
        }

        LayoutInflater lii = LayoutInflater.from(getContext());
        View editExerciseView = lii.inflate(R.layout.add_exercise_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
        builder.setTitle("Edit Exercise");

        final int start = databaseIndex;
        final EditText exerciseName = editExerciseView.findViewById(R.id.add_exercise_name);
        exerciseName.setInputType(InputType.TYPE_CLASS_TEXT);
        String s1 = exercisesAdapter.getItem(databaseIndex);
        exerciseName.setText(s1, TextView.BufferType.EDITABLE);
        final EditText setNum = editExerciseView.findViewById(R.id.add_exercise_sets);
        setNum.setInputType(InputType.TYPE_CLASS_TEXT);
        String s2 = exercisesAdapter.getItem((databaseIndex+1));
        setNum.setText(s2, TextView.BufferType.EDITABLE);
        final EditText repNum = editExerciseView.findViewById(R.id.add_exercise_reps);
        repNum.setInputType(InputType.TYPE_CLASS_TEXT);
        String s3 = exercisesAdapter.getItem((databaseIndex+2));
        repNum.setText(s3, TextView.BufferType.EDITABLE);
        final EditText weightAmt = editExerciseView.findViewById(R.id.add_exercise_weight);
        weightAmt.setInputType(InputType.TYPE_CLASS_TEXT);
        String s4 = exercisesAdapter.getItem((databaseIndex+3));
        weightAmt.setText(s4, TextView.BufferType.EDITABLE);

        builder.setView(editExerciseView);

        builder.setPositiveButton("Edit", (d, w) -> {

            Vector<String> newEdit = new Vector<>();

            if(exerciseName.getText().toString().equals(s1)){
                newEdit.add(s1);
            }else{
                newEdit.add(exerciseName.getText().toString());
            }

            if(setNum.getText().toString().equals(s2)){
                newEdit.add(s2);
            }else{
                newEdit.add(setNum.getText().toString());
            }

            if(repNum.getText().toString().equals(s3)){
                newEdit.add(s3);
            }else{
                newEdit.add(repNum.getText().toString());
            }

            if(weightAmt.getText().toString().equals(s4)){
                newEdit.add(s4);
            }else{
                newEdit.add(weightAmt.getText().toString());
            }

            new_exercises = new ArrayList<>();
            new_exercisesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, new_exercises);

            int b = 0;
            for(int a = 0; a < exercisesAdapter.getCount(); ++a){
                if((a >= start) && (a <= (start+3))){
                    new_exercisesAdapter.add(newEdit.elementAt(b));
                    ++b;
                }else {
                    new_exercisesAdapter.add(exercisesAdapter.getItem(a));
                }
            }

            exercisesAdapter.clear();
            newEdit.clear();
            currentProgram_exercises.setValue(new_exercises);


        });
        builder.setNegativeButton("Cancel", (d, w) ->{
            d.cancel();
        });
        builder.show();
    }
}
