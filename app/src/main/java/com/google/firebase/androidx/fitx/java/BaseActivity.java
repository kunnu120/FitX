package com.google.firebase.androidx.fitx.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.VisibleForTesting;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.content.Context;
import android.app.ProgressDialog;

import com.google.firebase.androidx.fitx.R;


public class BaseActivity extends AppCompatActivity {

    @VisibleForTesting
    public ProgressDialog progLog;

    public void displayProgressDialog(){
        if(progLog == null){
            progLog = new ProgressDialog(this);
            progLog.setMessage(getString(R.string.loading));
            progLog.setIndeterminate(true);
        }
        progLog.show();
    }

    public void hideProgressDialog(){
        if(progLog.isShowing() && progLog != null){
            progLog.dismiss();
        }
    }

    public void hideKeys(View v){
        final InputMethodManager i = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(i != null){
            i.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        hideProgressDialog();
    }

}
