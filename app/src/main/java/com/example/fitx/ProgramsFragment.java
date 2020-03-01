package com.example.fitx;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    private ScrollView dateScroll;
    private ListView dateListPrint;
    private ListView programList;

    //array lists for programs and exercises
    private ArrayList<String> programs;
    private ArrayAdapter<String> programsAdapter;

    //array lists for dates
    private ArrayList<Calendar> datesClicked;
    private ArrayAdapter<Calendar> datesClickedAdapter;

    //initialize and declare database reference
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    //initialized reference for Programs
    private DatabaseReference userPrograms;



    private ValueEventListener programListener = new ValueEventListener(){
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

    private ValueEventListener dateListener = new ValueEventListener(){
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            try {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    datesClickedAdapter.add(Calendar.getInstance());
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
        dateSelected = rootView.findViewById(R.id.dateBox);
        //dateScroll = rootView.findViewById(R.id.dateScroll);

        ////////Calendar View Specifications////////
        /* starts before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);
        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

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
                .addEvents(new CalendarEventsPredicate() {

                    Random rnd = new Random();
                    @Override
                    public List<CalendarEvent> events(Calendar date) {
                        List<CalendarEvent> events = new ArrayList<>();
                        int count = rnd.nextInt(6);

                        for (int i = 0; i <= count; i++){
                            events.add(new CalendarEvent(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)), "event"));
                        }

                        return events;
                    }
                })
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                dateSelected.setText(DateFormat.format("EEE, MMM d, yyyy", date));
                //dateScroll.addView((View) DateFormat.format("EEE, MMM d, yyyy", date));
                //initializing add program to selected date button
                //Button addProgram = rootView.findViewById(R.id.addProgramToDate);

                //initializing dates selected list view
                //dateListPrint = rootView.findViewById(R.id.dateOutputList);

                //declare programs reference for user
                //datesClicked = new ArrayList<Calendar>();
                //datesClicked.add(date);
                //userDates = db.getReference("Users").child(userid).child("Program Dates");
                //userDates.addListenerForSingleValueEvent();
                //DateListAdapter dla = new DateListAdapter(this, android.R.layout.simple_list_item_1, datesClicked);
                //datesClickedAdapter = new ArrayAdapter<Calendar>(this,datesClicked );
                //dateListPrint.setAdapter(dla);
                //dateListPrint.setOnItemClickListener((AdapterView.OnItemClickListener) DateFormat.format("EEE, MMM d, yyyy", date));
            }
            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                //get current user id
                //String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());
                //declare programs reference
                //userPrograms = db.getReference("Users").child(userid).child("Programs");
                //userPrograms.addListenerForSingleValueEvent(programListener);
                return true;
            }
        });

        return rootView;
    }
}
