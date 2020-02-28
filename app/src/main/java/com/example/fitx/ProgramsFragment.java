package com.example.fitx;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

//import java.text.DateFormat;
import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;



public class ProgramsFragment extends Fragment{

    private HorizontalCalendar horizontalCalendar;
    private TextView dateSelected;
    private ListView datePrint;

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard

        View rootView = inflater.inflate(R.layout.fragment_programs, container, false);
        dateSelected = rootView.findViewById(R.id.dateBox);
        datePrint = rootView.findViewById(R.id.dateOutput);

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
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                //dateSelected.setText("Date Selected: " + date);
                //Toast.makeText(getContext(), DateFormat.format("EEE, MMM d, yyyy", date) + " is selected!", Toast.LENGTH_SHORT).show();
                dateSelected.setText(DateFormat.format("EEE, MMM d, yyyy", date));
                datePrint.setTextAlignment(DateFormat.format("EEE"+ "/" + "MMM d" + "/" + "yyyy", date));
            }

        });

        return rootView;
        //return inflater.inflate(R.layout.fragment_programs, null);
    }
}
