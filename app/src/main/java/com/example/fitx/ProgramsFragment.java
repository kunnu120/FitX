package com.example.fitx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.text.format.DateFormat;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.model.CalendarEvent;
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class ProgramsFragment extends Fragment{
    //views and variables used
    private HorizontalCalendar horizontalCalendar;
    private TextView comText;
    private TextView dateSelected;
    private TextView title;
    private TextView dateOfProgram;
    //private ListView commentInput;
    //private ListView programList;
    private String programText;
    //private String commentText;

    //array lists for programs and its adapter
    private ArrayList<String> programs;
    private ArrayAdapter<String> programsAdapter;

    // array list for comments and its adapter
    private ArrayList<String> comments;
    private ArrayAdapter<String> commentsAdapter;

    //array lists for dates and its adapter
    private ArrayList<String> datesClicked;
    private ArrayAdapter<String> datesClickedAdapter;

    //initialize and declare database reference
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference userPrograms;
    private DatabaseReference Dates;
    private DatabaseReference currentComment;
    private DatabaseReference currentDate;
    private DatabaseReference currentUserRef;
    private DatabaseReference programOnDate;
    private Spinner sp;

    //Value Event listener for comments
    private ValueEventListener commentListener = new ValueEventListener(){
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            try {
                    if(commentsAdapter!= null){
                        commentsAdapter.clear();
                    }
                 //gets data that is entered in as comments and updates the view
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    commentsAdapter.add(ds.getValue().toString());
                }

                if(commentsAdapter.getCount()>0) {
                    comText.setText("");
                    for (int i = 0; i < commentsAdapter.getCount(); i++) {
                        comText.append(commentsAdapter.getItem(i) + "\n");
                    }
                }else{
                    //Default text in comment text view set to "No Comments"
                    comText.setText("No comments");
                }


            } catch (NullPointerException e) {

            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError){

        }
    };
    //Value event listner to add programs on dates
    private ValueEventListener programOnDateListener = new ValueEventListener(){
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            try {
                if(dataSnapshot.getValue() == null){
                    String noVal = "Hold on Date to Schedule or Comment";
                    dateSelected.setText(noVal);
                }else {
                    dateSelected.setText(dataSnapshot.getValue().toString());
                }



            } catch (NullPointerException e) {

            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError){

        }
    };
    // Value event listener to update when new programs are added
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
        //commentInput = rootView.findViewById(R.id.CommentList);
        dateSelected = rootView.findViewById(R.id.dateBox);
        comText = rootView.findViewById(R.id.textView3);
        comText.setMovementMethod(new ScrollingMovementMethod());
        dateOfProgram = rootView.findViewById(R.id.dateOfProgram);
        //commentToProgramText = rootView.findViewById(R.id.dateToProgram);
        //programList= rootView.findViewById(R.id.LT);
        title = rootView.findViewById(R.id.title);
        String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());
        sp = rootView.findViewById(R.id.programspinner);

        ///////Calendar View Specifications////////
        /* starts before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);
        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        //The current user from database
        currentUserRef = db.getReference("Users").child(userid);
        // The current users programs
        userPrograms = db.getReference("Users").child(userid).child("Programs");
        userPrograms.addValueEventListener(programListener);
        comments = new ArrayList<>();
        commentsAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, comments);

            //the list of programs
            programs = new ArrayList<>();
            programsAdapter = new ArrayAdapter<>((this.getActivity()), android.R.layout.simple_list_item_1, programs);

            //spinner gets updated as the user enters in programs on exercise page
            sp.setAdapter(programsAdapter);

        /**********
          On entering the page the user is greeted with the horizontal calendar that begins on the current date up to
          one month from that date. Dates are displayed as Month , Date ,and Day(Sun-Sat) The user can scroll up to that month
         limit forwards and backwards
        ************/
        //Builds horizontal calendar to predetermined specifications
        horizontalCalendar = new HorizontalCalendar.Builder(rootView, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .configure()
                    .formatTopText("MMM dd")
                    .formatMiddleText("EEE")
                    .formatBottomText("")
                    .textSize(14f, 24f, 14f)
                    .showTopText(true)
                    .showBottomText(false)
                    .textColor(Color.LTGRAY, Color.WHITE)
                .end()
                .addEvents(new CalendarEventsPredicate() {
                    @Override
                    public List<CalendarEvent> events(Calendar date) {
                        List<CalendarEvent> events = new ArrayList<>();
                        if (horizontalCalendar.getSelectedDate() == Calendar.getInstance())
                        {
                            events.add(new CalendarEvent(Color.RED, "Program On Date"));
                        }
                        return events;
                    }
                })
                .build();

        // Two types of click interactions the user can have
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @SuppressLint("SetTextI18n")
            @Override
            /****
            When a date on the calendar is selected it will display the program a user has scheduled,
             as well as the comments they have input for that day. If the user has no programs they cannot
             enter in comments or schedule dates
             *****/
            public void onDateSelected(Calendar date, int position) {

                //database reference for dates
                Dates = currentUserRef.child("Dates");
                //Corrects date format to be Day(Sun-Sat) ,Month , Date
                currentDate = Dates.child(DateFormat.format("EEE, MMM dd", date).toString());
                //Program scheduled on date to be stored in database
                programOnDate = currentDate.child("ProgramsScheduled");
                //Event listener to connect a program to the date selected and display that
                programOnDate.addValueEventListener(programOnDateListener);
                //database path for comments
                currentComment = currentDate.child("Comments");
                //Event listener shows comments for selected date
                currentComment.addValueEventListener(commentListener);

            }

            @Override
            //When a user long clicks on a date
            /***
             * When a user holds on a date they are prompted to schedule the program they have selected from the
             * spinner to add to that date or ,to add a comment to that date , they can clear comments or
             * reschedule a day to have a different program. They comment display is also scrollable to view larger
             * amounts content
             * Press cancel or outside of dialogue box to leave popup
             */
            public boolean onDateLongClicked(Calendar date, int position) {
                try{
                    programText = sp.getSelectedItem().toString();
                } catch (NullPointerException e) {

                    Toast t = Toast.makeText(getContext(), "No programs to schedule", Toast.LENGTH_SHORT);
                    t.show();
                }
            //Must have programs to start scheduling
            if(programText!=null) {
                Dates = currentUserRef.child("Dates");
                currentDate = Dates.child(DateFormat.format("EEE, MMM dd", date).toString());
                String prompt = "Schedule your " + programText + " program \n" +"on " + DateFormat.format("EEE, MMM dd", date).toString() + "?";
                LayoutInflater lii = LayoutInflater.from(getContext());
                View scheduleProgram = lii.inflate(R.layout.schedule_program_dialog, null);
                //Button commentBtn = scheduleProgram.findViewById(R.id.commentbutton);
                //Button scheduleBtn = scheduleProgram.findViewById(R.id.schedulebutton);
                TextView tv = scheduleProgram.findViewById(R.id.schedule_or_comment);
                String newprompt = prompt + " Or comment?";
                tv.setText(newprompt);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
                builder1.setView(scheduleProgram);
                builder1.setPositiveButton("Schedule", (d,w)->{
                    Dates = currentUserRef.child("Dates");
                    currentDate = Dates.child(DateFormat.format("EEE, MMM dd", date).toString());
                    programOnDate = currentDate.child("ProgramsScheduled");
                    programOnDate.addValueEventListener(programOnDateListener);
                    currentComment = currentDate.child("Comments");
                    currentComment.addValueEventListener(commentListener);
                    if(programText!= null){
                        programOnDate.setValue(programText);
                    }
                });
                builder1.setNeutralButton("Comment", (d,w)->{
                    currentComment = currentDate.child("Comments");
                    currentComment.addValueEventListener(commentListener);
                    if (programs != null) {
                        LayoutInflater ci = LayoutInflater.from(getContext());
                        View createComments = ci.inflate(R.layout.comment_dialog, null);
                        AlertDialog.Builder cBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);

                        cBuilder.setTitle("Add a Comment");
                        EditText cTV = createComments.findViewById(R.id.add_program_comment);
                        cBuilder.setView(createComments);

                        cBuilder.setPositiveButton("Add", (d1, w1) -> {

                            if (!cTV.getText().toString().equals("")){
                                commentsAdapter.add(cTV.getText().toString());
                                currentComment.setValue(comments);
                            }else{
                                d1.cancel();
                            }

                        });
                        cBuilder.setNeutralButton("Clear comments", (d1, w1) -> {
                           commentsAdapter.clear();
                           currentComment.setValue(comments);
                        });
                        cBuilder.setNegativeButton("Cancel", (d1, w1) -> {
                            d1.cancel();
                        });
                        cBuilder.show();
                    }
                });
                builder1.setNegativeButton("Close", (d,w)->{

                    d.cancel();
                });

                builder1.show();
            }
                return true;

                }
        });
        return rootView;

    }
}

