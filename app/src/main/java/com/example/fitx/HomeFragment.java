package com.example.fitx;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;


public class HomeFragment extends Fragment {

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, null);
        Button logoutButton = v.findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(v1 -> {
            fAuth.signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
        });
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        return v;

    }

    public static String dummyFunction() {
        return "This is a useful test.";
    }


}
