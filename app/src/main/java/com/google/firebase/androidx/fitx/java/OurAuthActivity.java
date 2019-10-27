package com.google.firebase.androidx.fitx.java;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;


import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.quickstart.auth.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;


public class OurAuthActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "OurAuthActivity";

    private FirebaseAuth oAuth;

}
