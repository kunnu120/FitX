package com.example.fitx;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
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
    private DatabaseReference currentProgram;
    private DatabaseReference currentDate;
    private DatabaseReference currentComment;
    private DatabaseReference selectComment;
    private DatabaseReference selectProgram;
    private DatabaseReference programOnDate;


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
    private ValueEventListener dateListener = new ValueEventListener(){
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (datesClickedAdapter != null) {
                datesClickedAdapter.clear();
            }
            try {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    datesClickedAdapter.addAll(ds.getKey());
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
        programList= rootView.findViewById(R.id.LT);
        title = rootView.findViewById(R.id.title);
        String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        ///////Calendar View Specifications////////
        /* starts before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);
        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        if (programs == null){
        programs = new ArrayList<>();
        currentProgram = db.getReference("Users").child(userid).child("Programs");
        currentProgram.addValueEventListener(programListener);
        programsAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_list_item_1, programs);
        programList.setAdapter(programsAdapter);
        programList.setOnItemClickListener((p, view, pos, id) -> {
            selectProgram = currentProgram.child(Objects.requireNonNull(programsAdapter.getItem(pos)));
            programText = selectProgram.toString();
            HorizontalCalendarConfig.DEFAULT_FORMAT_TEXT_BOTTOM.concat(programText);
            //System.out.println(programText);
        });}

        /*if (comments == null){
        comments = new ArrayList<>();
        currentComment = db.getReference("Users").child(userid).child("Programs").child("Program Comments");
        currentComment.addValueEventListener(commentListener);
        commentsAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_list_item_1, comments);
        commentInput.setAdapter(commentsAdapter);
        commentInput.setOnItemClickListener((p, view, pos, id) -> {
            selectComment = currentComment.child(Objects.requireNonNull(commentsAdapter.getItem(pos)));
            //commentText = selectComment.toString();
        });}

        //databaseDate = db.getReference("Users").child(userid).child("Program Dates");
        //String PD = String.valueOf(dateSelected);

        /*datesClicked = new ArrayList<>();
        currentDate= db.getReference("Users").child(userid).child("Program Dates");
        currentDate.addValueEventListener(dateListener);
        datesClickedAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_list_item_1, datesClicked);
        /*dateToProgramList.setAdapter(datesClickedAdapter);
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
                dateSelected.setText(DateFormat.format("EEE, MMM dd", date));
                currentDate = db.getReference("Users").child(userid).child("Programs").child("Dates");
                programOnDate = db.getReference("Users").child(userid).child("Programs").child("Dates").child("Program on Date");
                if (programText != null) {
                        LayoutInflater li = LayoutInflater.from(getContext());
                        View scheduleProgram = li.inflate(R.layout.schedule_program_dialog, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
                        TextView tv = scheduleProgram.findViewById(R.id.schedule_pro);

                        int currentProgramIndex = programList.getLastVisiblePosition();
                        programText = programList.getItemAtPosition(currentProgramIndex).toString();
                        String selectedDateStr = DateFormat.format("EEE, MMM dd", date).toString();
                        String prompt = "Do you want to schedule your " + programText + " program on " + selectedDateStr + "?";
                        tv.setText(prompt);
                        builder.setView(scheduleProgram);

                        builder.setPositiveButton("Accept", (d, w) -> {
                            //horizontalCalendar.getConfig().setShowBottomText(true);
                            //horizontalCalendar.getConfig().setFormatBottomText(programText);
                            //programOnDate.equals(true);
                            dateOfProgram.setText("You have scheduled " + programText + " on " + selectedDateStr + ".");
                            currentDate.push().setValue(selectedDateStr);
                            programOnDate.push().setValue(dateOfProgram.getText());
                            System.out.println(programOnDate.push().setValue(dateOfProgram.toString()));
                            System.out.println(currentDate.push().setValue(selectedDateStr));
                            /*if (programOnDate!= null) {
                                //programOnDate.equals(false);
                                dateOfProgram.setText("You have already scheduled " + programText + " on " + selectedDateStr + ".");

                            }
                            else {
                                //programOnDate.equals(false);
                                dateOfProgram.setText("You have scheduled " + programText + " on " + selectedDateStr + ".");
                            }
                            //dateOfProgram.setText("You have scheduled " + programText + " on " + selectedDateStr + ".");
                            HorizontalCalendarConfig config = new HorizontalCalendarConfig();
                            config.setFormatBottomText(programText);
                            horizontalCalendar.getConfig().getFormatBottomText();
                            horizontalCalendar.getConfig().setupDefaultValues(config.setFormatBottomText(programText));*/
                        });
                        builder.setNegativeButton("Cancel", (d, w) -> {
                            dateOfProgram.setText("You have no program scheduled on " + selectedDateStr);
                            /*programOnDate.equals(false);
                            if (programOnDate == null) {
                                dateOfProgram.setText("You have already scheduled " + programText + " on " + selectedDateStr + ".");
                            }
                            else {
                                dateOfProgram.setText("You have no program scheduled on " + selectedDateStr);
                            }*/
                            d.cancel();
                        });
                        builder.show();
                    }
            }
            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                currentComment = db.getReference("Users").child(userid).child("Programs").child("Dates").child("Program on Date").child("Comment on Program");
                if (programs != null) {
                        LayoutInflater ci = LayoutInflater.from(getContext());
                        View createComments = ci.inflate(R.layout.comment_dialog, null);
                        AlertDialog.Builder cBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
                        cBuilder.setTitle("Add a Comment");
                        EditText cTV = createComments.findViewById(R.id.add_program_comment);
                        cBuilder.setView(createComments);

                        cBuilder.setPositiveButton("Done", (d, w) -> {
                            //if(commentText == null) {
                            //commentText = selectComment.toString();
                            //commentText = cTV.toString();
                            commentText = cTV.getText().toString();
                            //comText.setText(commentText);
                            comText.setText(commentText);
                            currentComment.push().setValue(comText.getText());
                            //comText.setText(commentText);
                            //commentInput.setAdapter(commentsAdapter);
                            //commentInput.setFilterText(commentText);
                            //}
                        });
                        cBuilder.setNegativeButton("Cancel", (d, w) -> {
                            d.cancel();
                        });
                        cBuilder.show();
                    }
                    return true;
                }
        });
        return rootView;
    }
}


/*   HorizontalCalendarConfig config = new HorizontalCalendarConfig()
config.setFormatBottomText(programText);
horizontalCalendar.getConfig().getFormatBottomText();
horizontalCalendar.getConfig().setupDefaultValues(config.setFormatBottomText(programText));
/*System.out.println(HorizontalCalendarConfig.DEFAULT_FORMAT_TEXT_BOTTOM.length());
//System.out.println(HorizontalCalendarConfig.DEFAULT_FORMAT_TEXT_BOTTOM.subSequence(0,3));
//System.out.println(HorizontalCalendarConfig.DEFAULT_FORMAT_TEXT_BOTTOM.concat(programText));
//System.out.println(horizontalCalendar.getConfig().setShowBottomText(true).isShowBottomText());
System.out.println(config.setFormatBottomText(programText));
System.out.println(programText);
System.out.println(horizontalCalendar.contains(date));
System.out.println(horizontalCalendar.getConfig().getFormatBottomText());
System.out.println(horizontalCalendar.getConfig().setFormatBottomText(programText).toString());
//horizontalCalendar.getConfig().setFormatBottomText(HorizontalCalendarConfig.DEFAULT_FORMAT_TEXT_BOTTOM.concat(programText));
//System.out.println(horizontalCalendar.getConfig().setupDefaultValues(config.setFormatBottomText(programText));
//horizontalCalendar.getConfig();
//List<CalendarEvent> events = new ArrayList<>();
events.add(new CalendarEvent(Color.RED, "Program On Date"));*/