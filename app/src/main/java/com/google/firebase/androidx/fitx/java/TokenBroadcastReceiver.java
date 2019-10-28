package com.google.firebase.androidx.fitx.java;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.content.BroadcastReceiver;

public abstract class TokenBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "TokenBroadcastReceiver";

    public static final String EXTRA_KEY_TOKEN = "key_token";
    public static final String ACTION_TOKEN = "com.google.example.ACTION_TOKEN";

    @Override
    public void onReceive(Context cont, Intent intent){
        Log.d(TAG, "token_Recieve:" + intent);

        if (ACTION_TOKEN.equals(intent.getAction())) {
            String token = intent.getExtras().getString(EXTRA_KEY_TOKEN);
            newToken(token);
        }
    }

    public static IntentFilter getFilter(){
        IntentFilter filter = new IntentFilter(ACTION_TOKEN);
        return filter;
    }

    public abstract void newToken(String token);
}
