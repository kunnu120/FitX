package com.example.fitx;

import android.app.AlertDialog;
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
    private TextView programsScroll;
    private ScrollView dateScroll;
    private ListView dateListPrint;
    private ListView programList;

    //array lists for programs and exercises
    private ArrayList<String> programs;
    private ArrayAdapter<String> programsAdapter;

    //array lists for dates
    //private ArrayList<Calendar> datesClicked;
    //private ArrayAdapter<Calendar> datesClickedAdapter;

    //initialize and declare database reference
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    //initialized reference for Programs
    private DatabaseReference userPrograms;



    /*private ValueEventListener programListener = new ValueEventListener(){
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
    };*/

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_programs, container, false);
        dateSelected = rootView.findViewById(R.id.dateBox);
        //dateScroll = rootView.findViewById(R.id.dateScroll);
        programList= rootView.findViewById(R.id.programList);
        title = rootView.findViewById(R.id.title);
        //programsScroll = rootView.findViewById(R.id.programScroll);

        //initializing add program to selected date button
        //Button addProgram = rootView.findViewById(R.id.addProgramToDate);

        ////////Calendar View Specifications////////
        /* starts before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);
        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        //programsAdapter = new RecyclerView.Adapter<ArrayList<String>>(this.getContext(), android.R.layout.simple_list_item_1, programs);

        horizontalCalendar = new HorizontalCalendar.Builder(rootView, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .configure()
                    .formatTopText("MMM")
                    .formatMiddleText("dd")
                    .formatBottomText("EEE")
                    .textSize(14f, 24f, 14f)
                    .showTopText(true)
                    .showBottomText(true)
                    .textColor(Color.LTGRAY, Color.WHITE)
                .end()
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                dateSelected.setText(DateFormat.format("EEE, MMM d, yyyy", date));
                /* builder1.setTitle("Select Program");
                //get current user id
                String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());
                //declare programs reference
                programs = new ArrayList<>();
                userPrograms = db.getReference("Users").child(userid).child("Programs");
                userPrograms.addListenerForSingleValueEvent(programListener);

                programList.setAdapter(programsAdapter);
                builder1.setNegativeButton("Cancel", (d, w) ->{
                    d.cancel();
                });
                builder1.show();
                */
            }
            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                return true;
            }
        });

        //add program click listener
/*
        addProgram.setOnClickListener(v1 -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
            builder1.setTitle("Select Program");
            //get current user id
            String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());
            //declare programs reference
            programs = new ArrayList<>();
            userPrograms = db.getReference("Users").child(userid).child("Programs");
            userPrograms.addListenerForSingleValueEvent(programListener);
            programsAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, programs);

            programList.setAdapter(programsAdapter);
            builder1.setNegativeButton("Cancel", (d, w) ->{
                d.cancel();
            });
            builder1.show();
        });

        */
        return rootView;
    }

}
