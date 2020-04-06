package com.example.fitx;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

//import java.text.DateFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.model.CalendarEvent;
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;



public class ProgramsFragment extends Fragment{

    private HorizontalCalendar horizontalCalendar;
    private TextView dateSelected;
    private TextView title;
    private TextView dateToProgramText;
    private ListView programList;
    private String programText;
    private String dateClicked;

    //array lists for programs and its adapter
    private ArrayList<String> programs;
    private ArrayAdapter<String> programsAdapter;

    private ArrayList<String> comments;
    private ArrayAdapter<String> commentsAdapter;

    //array list for the combination of a date to a program
    //private ArrayList<String> dTPL;
    private ArrayAdapter<String> dTPLAdapter;

    //array lists for dates
    //private ArrayList<String> datesClicked;
    //private ArrayAdapter<String> datesClickedAdapter;

    //initialize and declare database reference
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference currentProgram;
    private DatabaseReference currentDate;
    private DatabaseReference currentComment;
    //initialized reference for Programs
    private DatabaseReference selectProgram;
    private DatabaseReference programAndDate;

    /*@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }*/

    private ValueEventListener commentListener = new ValueEventListener(){
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (commentsAdapter != null) {
                commentsAdapter.clear();
            }
            try {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    commentsAdapter.addAll(ds.getKey());
                }


            } catch (NullPointerException e) {

            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError){

        }
    };

    private ValueEventListener dTPListener = new ValueEventListener(){
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dTPLAdapter != null) {
                dTPLAdapter.clear();
            }
            try {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    dTPLAdapter.addAll(ds.getKey());
                }


            } catch (NullPointerException e) {

            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError){

        }
    };
    private ValueEventListener programListener = new ValueEventListener(){
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (programsAdapter != null) {
                programsAdapter.clear();
            }
            try {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    programsAdapter.addAll(ds.getKey());
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
    public View onCreateView(LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_programs, container, false);
        ListView commentInput = rootView.findViewById(R.id.CommentList);
        dateSelected = rootView.findViewById(R.id.dateBox);
        dateToProgramText = rootView.findViewById(R.id.dateToProgram);
        programList= rootView.findViewById(R.id.LT);
        title = rootView.findViewById(R.id.title);
        String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        //initializing add program to selected date button
        //Button addProgram = rootView.findViewById(R.id.addProgramToDate);

        ////////Calendar View Specifications////////
        /* starts before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);
        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        comments = new ArrayList<>();
        currentComment = db.getReference("Users").child(userid).child("Comments");
        currentComment.addValueEventListener(commentListener);
        commentsAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_list_item_1, comments);
        programList.setAdapter(programsAdapter);
        programList.setOnItemClickListener((p, view, pos, id) -> {
            selectProgram = currentProgram.child(Objects.requireNonNull(programsAdapter.getItem(pos)));
            programText = selectProgram.toString();
        });

        //programsAdapter = new RecyclerView.Adapter<ArrayList<String>>(this.getContext(), android.R.layout.simple_list_item_1, programs);
        if (programs == null){
        programs = new ArrayList<>();
        currentProgram = db.getReference("Users").child(userid).child("Programs");

        currentProgram.addValueEventListener(programListener);
        programsAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_list_item_1, programs);
        commentInput.setAdapter(commentsAdapter);
        commentInput.setOnItemClickListener((p, view, pos, id) -> {
            selectProgram = currentProgram.child(Objects.requireNonNull(programsAdapter.getItem(pos)));
            programText = selectProgram.toString();
        });}

        /*dTPL = new ArrayList<>();
        currentDate= db.getReference("Users").child(userid).child("Program Dates");
        currentDate.addValueEventListener(dTPListener);
        dTPLAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_list_item_1, dTPL);
        dateToProgramList.setAdapter(dTPLAdapter);
        dateToProgramList.setOnItemClickListener((p, view, pos, id) -> {
            programAndDate = currentDate.child(Objects.requireNonNull(dTPLAdapter.getItem(pos)));
        });*/


        horizontalCalendar = new HorizontalCalendar.Builder(rootView, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .configure()
                    .formatTopText("MMM dd")
                    .formatMiddleText("EEE")
                    .formatBottomText("")
                    .textSize(14f, 24f, 14f)
                    .showTopText(true)
                    .showBottomText(true)
                    .textColor(Color.LTGRAY, Color.WHITE)
                .end()
                .addEvents(new CalendarEventsPredicate() {

                    @Override
                    public List<CalendarEvent> events(Calendar date) {
                        List<CalendarEvent> events = new ArrayList<>();
                        if (date == horizontalCalendar.getSelectedDate())
                        {
                            events.add(new CalendarEvent(Color.RED, "event"));
                        }
                        return events;
                    }
                })
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                //Random rnd = new Random();
                LayoutInflater li = LayoutInflater.from(getContext());
                View scheduleProgram = li.inflate(R.layout.schedule_program_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
                TextView tv = scheduleProgram.findViewById(R.id.schedule_pro);
                dateSelected.setText(DateFormat.format("EEE, MMM dd", date));

                int currentProgramIndex = programList.getLastVisiblePosition();
                programText = programList.getItemAtPosition(currentProgramIndex).toString();
                String selectedDateStr = DateFormat.format("EEE, MMM dd", date).toString();
                String prompt = "Do you want to schedule your " + programText + " program on " + selectedDateStr + "?";
                tv.setText(prompt);
                builder.setView(scheduleProgram);

                builder.setPositiveButton("Accept", (d,w) ->{
                    //horizontalCalendar.getConfig().setShowBottomText(true);
                    //horizontalCalendar.getConfig().setFormatBottomText(programText);
                });
                builder.setNegativeButton("Cancel", (d,w)->{

                    d.cancel();
                });
                builder.show();

                /*LayoutInflater ci = LayoutInflater.from(getContext());
                View createComments = ci.inflate(R.layout.comment_dialog, null);
                AlertDialog.Builder cBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
                cBuilder.setTitle("Add a Comment");
                EditText cTV = createComments.findViewById(R.id.add_program_comment);
                cBuilder.setView(createComments);

                cBuilder.setPositiveButton("Done", (d,w)->{

                });
                cBuilder.setNegativeButton("Cancel", (d,w)->{
                    d.cancel();
                });
                cBuilder.show();*/
            }
            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                LayoutInflater ci = LayoutInflater.from(getContext());
                View createComments = ci.inflate(R.layout.comment_dialog, null);
                AlertDialog.Builder cBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
                cBuilder.setTitle("Add a Comment");
                EditText cTV = createComments.findViewById(R.id.add_program_comment);
                cBuilder.setView(createComments);

                cBuilder.setPositiveButton("Done", (d,w)->{
                    String commentSTR = cTV.toString();

                });
                cBuilder.setNegativeButton("Cancel", (d,w)->{
                    d.cancel();
                });
                cBuilder.show();
                /*datesClicked = new ArrayList<>();
                currentDate = db.getReference("Users").child(userid).child("Dates");
                currentDate.addValueEventListener(dateListener);
                datesClickedAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.onDateSelected(Calendar date,int position);, android.R.layout.simple_list_item_1, datesClicked);)
                datesClicked.setAdapter(datesClickedAdapter);
                programList.setOnItemClickListener((p, view, pos, id) -> {
                    selectProgram = currentProgram.child(Objects.requireNonNull(programsAdapter.getItem(pos)));
                */
                return true;
            }
        });
        return rootView;
    }
}
