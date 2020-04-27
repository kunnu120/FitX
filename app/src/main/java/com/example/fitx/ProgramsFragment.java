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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.model.CalendarEvent;
import devs.mulham.horizontalcalendar.model.HorizontalCalendarConfig;
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class ProgramsFragment extends Fragment{

    private HorizontalCalendar horizontalCalendar;
    private TextView comText;
    private TextView dateSelected;
    private TextView title;
    private TextView dateOfProgram;
    //private TextView commentToProgramText;
    private ListView commentInput;
    private ListView programList;
    private String programText;
    private String commentText;
    //private String dateText;

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

    private ValueEventListener commentListener = new ValueEventListener(){
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            try {
                    if(commentsAdapter!= null){
                        commentsAdapter.clear();
                    }
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    commentsAdapter.add(ds.getValue().toString());
                }

                if(commentsAdapter.getCount()>0) {
                    comText.setText("");
                    for (int i = 0; i < commentsAdapter.getCount(); i++) {
                        comText.append(commentsAdapter.getItem(i) + "\n");
                    }
                }else{
                    comText.setText("      Welcome to FitX Programs page \n      here is where you add comments \n      and schedule workout programs \n           to whatever days you want\n\n                   Just hold on a day\n             to schedule or comment!\n    Make sure to check out the social\n     page to the right to meet friends\n                    to work out with!");
                }


            } catch (NullPointerException e) {

            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError){

        }
    };
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

        currentUserRef = db.getReference("Users").child(userid);
        userPrograms = db.getReference("Users").child(userid).child("Programs");
        userPrograms.addValueEventListener(programListener);
        comments = new ArrayList<>();
        commentsAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, comments);

        if (programs == null) {
            programs = new ArrayList<>();
            programsAdapter = new ArrayAdapter<>((this.getActivity()), android.R.layout.simple_list_item_1, programs);
            //programList.setAdapter(programsAdapter);
            sp.setAdapter(programsAdapter);

        }


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
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSelected(Calendar date, int position) {

                Dates = currentUserRef.child("Dates");
                currentDate = Dates.child(DateFormat.format("EEE, MMM dd", date).toString());
                programOnDate = currentDate.child("ProgramsScheduled");
                programOnDate.addValueEventListener(programOnDateListener);
                currentComment = currentDate.child("Comments");
                currentComment.addValueEventListener(commentListener);

            }
            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                programText = sp.getSelectedItem().toString();
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
                        cBuilder.setNegativeButton("Cancel", (d1, w1) -> {
                            d1.cancel();
                        });
                        cBuilder.show();
                    }
                });
                builder1.setNegativeButton("Close", (d,w)->{

                    d.cancel();
                });
        /*        commentBtn.setOnClickListener(v -> {

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
                            cBuilder.setNegativeButton("Cancel", (d1, w1) -> {
                                d1.cancel();
                            });
                            cBuilder.show();
                        }
                });   */
           /*     scheduleBtn.setOnClickListener(v -> {
                    Dates = currentUserRef.child("Dates");
                    currentDate = Dates.child(DateFormat.format("EEE, MMM dd", date).toString());
                    programOnDate = currentDate.child("ProgramsScheduled");
                    programOnDate.addValueEventListener(programOnDateListener);
                    currentComment = currentDate.child("Comments");
                    currentComment.addValueEventListener(commentListener);
                    if(programText!= null){
                        programOnDate.setValue(programText);
                    }
                }); */

            /*    builder1.setPositiveButton("Schedule" , (d, w)->{
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
                //builder1.setPositiveButton("Comment", (d,w)->{
                //    currentComment = currentDate.child("Comments");
                //    currentComment.addValueEventListener(commentListener);
                //    if (programs != null) {
                //        LayoutInflater ci = LayoutInflater.from(getContext());
                //        View createComments = ci.inflate(R.layout.comment_dialog, null);
                //        AlertDialog.Builder cBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
                /*
                        cBuilder.setTitle("Add a Comment");

                        EditText cTV = createComments.findViewById(R.id.add_program_comment);
                        cBuilder.setView(createComments);

                        cBuilder.setPositiveButton("Done", (d1, w1) -> {

                            if (!cTV.getText().toString().equals("")){
                                commentsAdapter.add(cTV.getText().toString());
                                currentComment.setValue(comments);
                            }else{
                                d.cancel();
                            }

                        });
                        cBuilder.setNegativeButton("Cancel", (d1, w1) -> {
                            d1.cancel();
                        });
                        cBuilder.show();
                    }
                });
                //builder1.setNegativeButton("Cancel", (d,w)->{
                //    d.cancel();
              */  //});
                builder1.show();
            }else{
                Toast t = Toast.makeText(getContext(), "Click a program on the program list to schedule.", Toast.LENGTH_SHORT);
                t.show();
            }
                return true;

                }
        });
        return rootView;

    }
}

