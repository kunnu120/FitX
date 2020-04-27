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
import android.widget.Spinner;
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
    private TableLayout exerciseTable;



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
            for(int j=1; j <= data.size()/4; j++) {
                TableRow r = (TableRow) exerciseTable.getChildAt(j);
                for (int k = 0; k < 4; k++) {
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
            for(int j=1; j <= data.size()/4; j++) {
                TableRow r = (TableRow) exerciseTable.getChildAt(j);
                for (int k = 0; k < 4; k++) {
                    TextView cell = (TextView) r.getChildAt(k);
                    cell.setText(data.get(i));
                    i++;
                }
            }
            data.clear();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            if(programsAdapter.getCount()>0) {
                //clears the table for new program
                for (int j = 1; j <= 12; j++) {
                    TableRow r = (TableRow) exerciseTable.getChildAt(j);
                    for (int k = 0; k < 4; k++) {
                        TextView cell = (TextView) r.getChildAt(k);
                        cell.setText("");
                    }
                }


                Vector<String> data = new Vector<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    data.add(ds.getValue().toString());
                }

                for (int i = 0; i < 4; i++) {
                    data.removeElementAt(data.size() - 1);
                }

                int i = 0;
                for (int j = 1; j <= data.size() / 4; j++) {
                    TableRow r = (TableRow) exerciseTable.getChildAt(j);
                    for (int k = 0; k < 4; k++) {
                        TextView cell = (TextView) r.getChildAt(k);
                        cell.setText(data.get(i));
                        i++;
                    }
                }
                data.clear();

                if (programList.getCount() == 0) {
                    for (int j = 1; j <= 12; j++) {
                        TableRow r = (TableRow) exerciseTable.getChildAt(j);
                        for (int k = 0; k < 4; k++) {
                            TextView cell = (TextView) r.getChildAt(k);
                            cell.setText("");
                        }
                    }
                }
            }else{
                //clears table
                for(int j=1; j <= 12; j++) {
                    TableRow r = (TableRow) exerciseTable.getChildAt(j);
                    for (int k = 0; k < 4; k++) {
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
            if(programsAdapter != null) {
                if (programsAdapter.isEmpty()) {
                    programsAdapter.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        programsAdapter.addAll((String) ds.getKey());
                    }
                    if (programsAdapter.getCount() > 0) {
                        programList.performItemClick(programList, 0, R.id.program_list);
                    }
                    programsAdapter.clear();
                }

                try {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        programsAdapter.addAll((String) ds.getKey());
                    }


                } catch (NullPointerException e) {

                }
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

        //get current user id
        String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());


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
                for (int k = 0; k < 4; k++) {
                    TextView cell = (TextView) r.getChildAt(k);
                    cell.setText("");
                }
            }

            if(programsAdapter.getCount()>0) {
                currentProgram = userPrograms.child(Objects.requireNonNull(programsAdapter.getItem(pos)));
                currentProgram_exercises = currentProgram.child("Exercises");
                currentProgram.addChildEventListener(tableSwitchListener);
                if(exercisesAdapter != null) {
                    if (exercisesAdapter.getCount() == 0) {
                        Toast t21 = Toast.makeText(getContext(), programsAdapter.getItem(pos) + " program has no exercises. Removing " + programsAdapter.getItem(pos) + ".", Toast.LENGTH_SHORT);
                        t21.show();
                        programsAdapter.remove(programsAdapter.getItem(pos));
                        if (programsAdapter.getCount() > 0) {
                            currentProgram = userPrograms.child(Objects.requireNonNull(programsAdapter.getItem(0)));
                            currentProgram_exercises = currentProgram.child("Exercises");
                            currentProgram.addChildEventListener(tableSwitchListener);
                            programList.setSelection(0);
                        }
                    }
                }
            }
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
                if(currProgram.length() == 0){
                    Toast t12 = Toast.makeText(getContext(),"You entered nothing. Please enter a name for a workout program.", Toast.LENGTH_SHORT);
                    t12.show();
                    addProgram.performClick();
                }else {

                    boolean program_exists = false;
                    for (int i = 0; i < programsAdapter.getCount(); i++) {
                        if (programsAdapter.getItem(i).equals(currProgram)) {
                            program_exists = true;
                        }

                    }
                    if (!program_exists) {
                        programsAdapter.add(currProgram);
                        currentProgram = userPrograms.child(currProgram);
                        currentProgram_exercises = currentProgram.child("Exercises");
                        currentProgram.addChildEventListener(tableSwitchListener);
                        programList.setSelection(programList.getCount() - 1);

                        Toast t2 = Toast.makeText(getContext(), "Program " + currProgram + " added. Please enter your first exercise for this program.", Toast.LENGTH_LONG);
                        t2.show();
                        //clears table before switch
                        for(int j=1; j <= 12; j++) {
                            TableRow r = (TableRow) exerciseTable.getChildAt(j);
                            for (int k = 0; k < 4; k++) {
                                TextView cell = (TextView) r.getChildAt(k);
                                cell.setText("");
                            }
                        }
                        addExercise.performClick();


                    }
                }
            });
            builder.setNegativeButton("Cancel", (d,w) ->{
                d.cancel();
            });
            //end of forced dialog

            builder.show();

        });

        //add exercise click listener
        addExercise.setOnClickListener(v1 -> {
            if(programsAdapter.getCount()>0) {
                LayoutInflater li = LayoutInflater.from(getContext());
                View addExerciseView = li.inflate(R.layout.add_exercise_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
                builder.setTitle("Add Exercise");

                builder.setView(addExerciseView);


                builder.setPositiveButton("Add", (d, w) -> {

                    if (exercisesAdapter == null) {

                        Vector<String> data = new Vector<>();

                        for (int j = 1; j <= 12; j++) {
                            TableRow r = (TableRow) exerciseTable.getChildAt(j);
                            TextView startCell = (TextView) r.getChildAt(0);
                            for (int k = 0; k < 4; k++) {
                                if (startCell.getText().toString().equals("")) {
                                    j = 13;
                                }
                                TextView cell = (TextView) r.getChildAt(k);
                                String cellData = cell.getText().toString();
                                data.add(cellData);
                            }
                        }
                        //removes the 4 empty spaces added at the end of the vector
                        for (int i = 0; i < 4; i++) {
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


                    Toast t;
                    boolean s1check = true;
                    boolean s2check = true;
                    boolean s3check = true;
                    boolean s4check = true;
                    if (s1.length() == 0 && s2.length() == 0 && s3.length() == 0 && s4.length() == 0) {
                        s1check = false;
                        s2check = false;
                        s3check = false;
                        s4check = false;
                        t = Toast.makeText(getContext(), "No information entered. Please enter all information correctly.", Toast.LENGTH_LONG);
                        t.show();
                        d.cancel();
                        addExercise.performClick();
                    } else {
                        if (s1.length() == 0) {
                            s1check = false;
                            t = Toast.makeText(getContext(), "No exercise name entered.", Toast.LENGTH_SHORT);
                            t.show();
                            d.cancel();
                        }
                        for (int i = 0; i < exercisesAdapter.getCount(); i++) {
                            if (exercisesAdapter.getItem(i).equals(s1)) {
                                s1check = false;
                                t = Toast.makeText(getContext(), "Exercise already exists.", Toast.LENGTH_SHORT);
                                t.show();
                                d.cancel();
                            }
                        }
                        if (s2.length() > 0) {
                            if (s2.length() > 2) {
                                s2check = false;
                                t = Toast.makeText(getContext(), "Sets should be a 1 or 2 digit number.", Toast.LENGTH_SHORT);
                                t.show();
                                d.cancel();
                            } else {
                                int s2intcheck = 0;
                                try {
                                    s2intcheck = Integer.parseInt(s2);
                                } catch (NumberFormatException nfe) {
                                    s2check = false;
                                    t = Toast.makeText(getContext(), "Sets is not in number format.", Toast.LENGTH_SHORT);
                                    t.show();
                                    d.cancel();
                                }
                                if (s2intcheck < 1) {
                                    s2check = false;
                                    t = Toast.makeText(getContext(), "You must have at least 1 set.", Toast.LENGTH_SHORT);
                                    t.show();
                                    d.cancel();
                                }
                            }

                        } else {
                            s2check = false;
                            t = Toast.makeText(getContext(), "No set number entered.", Toast.LENGTH_SHORT);
                            t.show();
                            d.cancel();
                        }

                        if (s3.length() > 0) {

                            if (s3.length() > 2) {
                                s3check = false;
                                t = Toast.makeText(getContext(), "Reps should be a 1 or 2 digit number.", Toast.LENGTH_SHORT);
                                t.show();
                                d.cancel();
                            } else {
                                int s3intcheck = 0;
                                try {
                                    s3intcheck = Integer.parseInt(s3);
                                } catch (NumberFormatException nfe) {
                                    s3check = false;
                                    t = Toast.makeText(getContext(), "Reps is not in number format.", Toast.LENGTH_SHORT);
                                    t.show();
                                    d.cancel();
                                }
                                if (s3intcheck < 1) {
                                    s3check = false;
                                    t = Toast.makeText(getContext(), "You must have at least 1 rep.", Toast.LENGTH_SHORT);
                                    t.show();
                                    d.cancel();
                                }
                            }

                        } else {
                            s3check = false;
                            t = Toast.makeText(getContext(), "No rep number entered.", Toast.LENGTH_SHORT);
                            t.show();
                            d.cancel();
                        }

                        if (s4.length() > 0) {

                            if (s4.length() > 3) {
                                s4check = false;
                                t = Toast.makeText(getContext(), "Weight should be a 1, 2, or 3 digit number.", Toast.LENGTH_SHORT);
                                t.show();
                                d.cancel();
                            } else {
                                int s4intcheck = 0;
                                try {
                                    s4intcheck = Integer.parseInt(s4);
                                } catch (NumberFormatException nfe) {
                                    s4check = false;
                                    t = Toast.makeText(getContext(), "Weight is not in number format.", Toast.LENGTH_SHORT);
                                    t.show();
                                    d.cancel();
                                }
                                if (s4intcheck < 1) {
                                    s4check = false;
                                    t = Toast.makeText(getContext(), "You must have at least 1 pound.", Toast.LENGTH_SHORT);
                                    t.show();
                                    d.cancel();
                                }
                            }

                        } else {
                            s4check = false;
                            t = Toast.makeText(getContext(), "No weight entered.", Toast.LENGTH_SHORT);
                            t.show();
                            d.cancel();
                        }

                        if (s1check && s2check && s3check && s4check) {
                            exercisesAdapter.add(s1);
                            exercisesAdapter.add(s2);
                            exercisesAdapter.add(s3);
                            exercisesAdapter.add(s4);
                            currentProgram_exercises.setValue(exercises);
                        } else {
                            t = Toast.makeText(getContext(), "Information wasn't entered correctly. Please enter correctly.", Toast.LENGTH_LONG);
                            t.show();
                            d.cancel();
                            addExercise.performClick();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", (d, w) -> {
                    int index = programList.getLastVisiblePosition();
                    programList.performItemClick(programList, index, R.id.program_list);
                    d.cancel();
                });
                builder.show();
            }else{
                Toast t11 = Toast.makeText(getContext(), "You have no programs, exercises are added to programs.", Toast.LENGTH_SHORT);
                t11.show();
            }
        });

        //remove exercise click listener
        removeExercise.setOnClickListener(v1 -> {
            //copy whole table into exercises adapter for editing
            Vector<String> data1 = new Vector<>();

            for(int h=1; h <= 12; h++) {
                TableRow row = (TableRow) exerciseTable.getChildAt(h);
                TextView namecell = (TextView)row.getChildAt(0);
                for (int k = 0; k < 4; k++) {
                    if(namecell.getText().toString().equals("")){
                        h = 13;
                    }
                    TextView cell = (TextView) row.getChildAt(k);
                    String cellData = cell.getText().toString();
                    data1.add(cellData);
                }
            }
            //removes the 4 empty spaces added at the end of the vector
            for(int i=0; i < 4; i++) {
                data1.removeElementAt(data1.size() - 1);
            }

            ArrayList<String> exercisenames = new ArrayList<>();
            ArrayAdapter<String> exercisenamesadapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, exercisenames);
            for(int i=0; i<data1.size(); i++){
                if(i%4==0){
                    exercisenamesadapter.add(data1.elementAt(i));
                }
            }

            Spinner sp = new Spinner(getActivity());
            sp.setAdapter(exercisenamesadapter);

            if(exercisenamesadapter.getCount()>0) {

                LayoutInflater li = LayoutInflater.from(getContext());
                View removeExercisePrompt = li.inflate(R.layout.edit_exercise_prompt, null);
                AlertDialog.Builder editPrompt = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
                editPrompt.setTitle("Remove What Exercise?");
                editPrompt.setView(sp);
                editPrompt.setPositiveButton("Remove", (d, w) -> {
                    if (programsAdapter.getCount() > 0) {
                        String exerciseStr = exercisenamesadapter.getItem(sp.getSelectedItemPosition());
                        exerciseToDelete(exerciseStr, sp.getSelectedItemPosition());
                    } else {
                        d.cancel();
                    }
                });
                editPrompt.setNegativeButton("Cancel", (d, w) -> {
                    d.cancel();
                });
                editPrompt.show();
            }else{
                Toast t9 = Toast.makeText(getContext(), "You have no exercises to remove yet.", Toast.LENGTH_SHORT);
                t9.show();
            }
        });



        //remove program click listener
        removeProgram.setOnClickListener(v1 -> {

            Spinner sp = new Spinner(getActivity());
            sp.setAdapter(programsAdapter);

            if(programsAdapter.getCount()>0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
                builder.setTitle("Remove What Program?");

                builder.setView(sp);
                builder.setPositiveButton("Delete", (d, w) -> {



                    String currProgram = programsAdapter.getItem(sp.getSelectedItemPosition());


                    currentProgram.setValue(null);
                    programsAdapter.remove(currProgram);


                    //clears the table
                    for (int j = 1; j <= 12; j++) {
                        TableRow r = (TableRow) exerciseTable.getChildAt(j);
                        for (int k = 0; k < 4; k++) {
                            TextView cell = (TextView) r.getChildAt(k);
                            cell.setText("");
                        }
                    }
                    programList.setSelection(0);
                    programList.performItemClick(programList, 0, R.id.program_list);






                });
                builder.setNegativeButton("Cancel", (d, w) -> {
                    d.cancel();
                });
                builder.show();
            }else{
                Toast t4 = Toast.makeText(getContext(), "You don't have any programs to remove.", Toast.LENGTH_SHORT);
                t4.show();
                //clears the table
                for (int j = 1; j <= 12; j++) {
                    TableRow r = (TableRow) exerciseTable.getChildAt(j);
                    for (int k = 0; k < 4; k++) {
                        TextView cell = (TextView) r.getChildAt(k);
                        cell.setText("");
                    }
                }
            }
        });


        //edit exercise click listener
        editExercise.setOnClickListener(v1 -> {
            //copy whole table into exercises adapter for editing
            Vector<String> data1 = new Vector<>();

            for(int h=1; h <= 12; h++) {
                TableRow row = (TableRow) exerciseTable.getChildAt(h);
                TextView namecell = (TextView)row.getChildAt(0);
                for (int k = 0; k < 4; k++) {
                    if(namecell.getText().toString().equals("")){
                        h = 13;
                    }
                    TextView cell = (TextView) row.getChildAt(k);
                    String cellData = cell.getText().toString();
                    data1.add(cellData);
                }
            }
            //removes the 4 empty spaces added at the end of the vector
            for(int i=0; i < 4; i++) {
                data1.removeElementAt(data1.size() - 1);
            }

            ArrayList<String> exercisenames = new ArrayList<>();
            ArrayAdapter<String> exercisenamesadapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, exercisenames);
            for(int i=0; i<data1.size(); i++){
                if(i%4==0){
                    exercisenamesadapter.add(data1.elementAt(i));
                }
            }
            if(exercisenamesadapter.getCount()>0) {
                Spinner sp = new Spinner(getActivity());
                sp.setAdapter(exercisenamesadapter);
                LayoutInflater li = LayoutInflater.from(getContext());
                AlertDialog.Builder editPrompt = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
                editPrompt.setTitle("Edit What Exercise?");
                editPrompt.setView(sp);
                editPrompt.setPositiveButton("Edit", (d, w) -> {

                    //copy whole table into exercises adapter for editing
                    Vector<String> data = new Vector<>();

                    for (int h = 1; h <= 12; h++) {
                        TableRow row = (TableRow) exerciseTable.getChildAt(h);
                        TextView namecell = (TextView) row.getChildAt(0);
                        for (int k = 0; k < 4; k++) {
                            if (namecell.getText().toString().equals("")) {
                                h = 13;
                            }
                            TextView cell = (TextView) row.getChildAt(k);
                            String cellData = cell.getText().toString();
                            data.add(cellData);
                        }
                    }
                    //removes the 4 empty spaces added at the end of the vector
                    for (int i = 0; i < 4; i++) {
                        data.removeElementAt(data.size() - 1);
                    }

                    exercises = new ArrayList<>();
                    exercisesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, exercises);
                    for (int m = 0; m < data.size(); ++m) {
                        exercisesAdapter.add(data.elementAt(m));
                    }


                    int databaseIndex = sp.getSelectedItemPosition() * 4;

                    LayoutInflater lii = LayoutInflater.from(getContext());
                    View editExerciseView = lii.inflate(R.layout.add_exercise_dialog, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
                    builder.setTitle("Edit Exercise");

                    final EditText exerciseName = editExerciseView.findViewById(R.id.add_exercise_name);
                    exerciseName.setInputType(InputType.TYPE_CLASS_TEXT);
                    String s1 = exercisesAdapter.getItem(databaseIndex);
                    exerciseName.setText(s1, TextView.BufferType.EDITABLE);
                    final EditText setNum = editExerciseView.findViewById(R.id.add_exercise_sets);
                    String s2 = exercisesAdapter.getItem((databaseIndex + 1));
                    setNum.setText(s2, TextView.BufferType.EDITABLE);
                    final EditText repNum = editExerciseView.findViewById(R.id.add_exercise_reps);
                    String s3 = exercisesAdapter.getItem((databaseIndex + 2));
                    repNum.setText(s3, TextView.BufferType.EDITABLE);
                    final EditText weightAmt = editExerciseView.findViewById(R.id.add_exercise_weight);
                    String s4 = exercisesAdapter.getItem((databaseIndex + 3));
                    weightAmt.setText(s4, TextView.BufferType.EDITABLE);

                    builder.setView(editExerciseView);

                    final int editpos = databaseIndex;
                    builder.setPositiveButton("Edit", (d1, w1) -> {

                        Vector<String> newEdit = new Vector<>();
                        boolean s1flag = true;
                        boolean s2flag = true;
                        boolean s3flag = true;
                        boolean s4flag = true;
                        String new_s1 = exerciseName.getText().toString();
                        String new_s2;
                        String new_s3;
                        String new_s4;


                        if (new_s1.length() == 0) {
                            s1flag = false;
                        }


                        if (s1flag) {

                            new_s2 = setNum.getText().toString();
                            if (new_s2.length() == 0) {
                                s2flag = false;
                            } else {
                                if (new_s2.length() < 3) {
                                    try {
                                        int check_new_s2 = Integer.parseInt(new_s2);
                                    } catch (NumberFormatException nfe) {
                                        s2flag = false;
                                        Toast t13 = Toast.makeText(getContext(), "Sets entered was not a 1 or 2 digit number.", Toast.LENGTH_SHORT);
                                        t13.show();
                                    }
                                } else {
                                    s2flag = false;
                                    Toast t14 = Toast.makeText(getContext(), "Sets entered is not valid. Should be a 1 or 2 digit number.", Toast.LENGTH_SHORT);
                                    t14.show();

                                }
                            }


                            new_s3 = repNum.getText().toString();
                            if (new_s3.length() == 0) {
                                s3flag = false;
                            } else {
                                if (new_s3.length() < 3) {
                                    try {
                                        int check_new_s3 = Integer.parseInt(new_s3);
                                    } catch (NumberFormatException nfe) {
                                        s3flag = false;
                                        Toast t14 = Toast.makeText(getContext(), "Reps entered was not a 1 or 2 digit number.", Toast.LENGTH_SHORT);
                                        t14.show();
                                    }
                                } else {
                                    s3flag = false;
                                    Toast t16 = Toast.makeText(getContext(), "Reps entered is not valid. Should be a 1 or 2 digit number.", Toast.LENGTH_SHORT);
                                    t16.show();

                                }
                            }


                            new_s4 = weightAmt.getText().toString();
                            if (new_s4.length() == 0) {
                                s4flag = false;
                            } else {
                                if (new_s4.length() < 4) {
                                    try {
                                        int check_new_s4 = Integer.parseInt(new_s4);
                                    } catch (NumberFormatException nfe) {
                                        s4flag = false;
                                        Toast t17 = Toast.makeText(getContext(), "Sets entered was not a 1, 2, or 3 digit number.", Toast.LENGTH_SHORT);
                                        t17.show();
                                    }
                                } else {
                                    s4flag = false;
                                    Toast t18 = Toast.makeText(getContext(), "Sets entered is not valid. Should be a 1, 2, or 3 digit number.", Toast.LENGTH_SHORT);
                                    t18.show();

                                }
                            }


                            if (s2flag && s3flag && s4flag) {
                                newEdit.add(new_s1);
                                newEdit.add(new_s2);
                                newEdit.add(new_s3);
                                newEdit.add(new_s4);

                                new_exercises = new ArrayList<>();
                                new_exercisesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, new_exercises);

                                int b = 0;
                                for (int a = 0; a < exercisesAdapter.getCount(); ++a) {
                                    if ((a >= editpos) && (a <= (editpos + 3))) {
                                        new_exercisesAdapter.add(newEdit.elementAt(b));
                                        ++b;
                                    } else {
                                        new_exercisesAdapter.add(exercisesAdapter.getItem(a));
                                    }
                                }

                                exercisesAdapter.clear();
                                for (int i = 0; i < new_exercisesAdapter.getCount(); i++) {
                                    exercisesAdapter.add(new_exercisesAdapter.getItem(i));
                                }
                                newEdit.clear();
                                new_exercisesAdapter.clear();
                                currentProgram_exercises.setValue(exercises);
                                Toast t20 = Toast.makeText(getContext(), "Exercise edited successfully.", Toast.LENGTH_SHORT);
                                t20.show();
                            } else {
                                Toast t19 = Toast.makeText(getContext(), "Information entered for exercise edit not valid. Please enter valid information.", Toast.LENGTH_SHORT);
                                t19.show();
                                d1.cancel();
                                editExercise.performClick();
                            }
                        }


                    });
                    builder.setNegativeButton("Cancel", (d2, w3) -> {
                        d2.cancel();
                    });
                    builder.show();

                });
                editPrompt.setNegativeButton("Cancel", (d, w) -> {
                    d.cancel();
                });
                editPrompt.show();
            }else{
                Toast t7 = Toast.makeText(getContext(), "You have no programs or exercises.", Toast.LENGTH_SHORT);
                t7.show();
            }

        });


        return v;
    }

    private void exerciseToDelete(String exerciseStr, int pos){
        pos = pos*4;
        //copy whole table into exercises adapter for editing
        Vector<String> data = new Vector<>();

        for(int h=1; h <= 12; h++) {
            TableRow row = (TableRow) exerciseTable.getChildAt(h);
            TextView namecell = (TextView)row.getChildAt(0);
            for (int k = 0; k < 4; k++) {
                if(namecell.getText().toString().equals("")){
                    h = 13;
                }
                TextView cell = (TextView) row.getChildAt(k);
                String cellData = cell.getText().toString();
                data.add(cellData);
            }
        }
        //removes the 4 empty spaces added at the end of the vector
        for(int i=0; i < 4; i++) {
            data.removeElementAt(data.size() - 1);
        }


        exercises = new ArrayList<>();
        exercisesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, exercises);
        for(int m=0; m < data.size(); ++m){
            if((m < pos) || (m >pos+3)) {
                exercisesAdapter.add(data.elementAt(m));
            }
        }

        currentProgram_exercises.setValue(exercises);

        //for updating the table after updating firebase exercise data
        if(programList.getCount()>0) {
            int current = programList.getLastVisiblePosition();
            programList.performItemClick(programList, current, R.id.program_list);
        }

    }

}