package com.google.firebase.androidx.fitx.java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.androidx.fitx.BuildConfig;
import com.google.firebase.androidx.fitx.R;

import java.util.Collections;

public class FirebaseUIActivity extends AppCompatActivity implements View.OnClickListener   {


    private void signOut() {
        AuthUI.getInstance().signOut(this);
        updateUI(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signInButton:
                startSignIn();
                break;
            case R.id.signOutButton:
                signOut();
                break;
        }
    }
}
