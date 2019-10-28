package com.google.firebase.androidx.fitx.java;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.androidx.fitx.R;


public class SelectorActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final Class[] CLASSES = new Class[] {
        GoogleSignInActivity.class,
        EmailPasswordActivity.class,
        NoPasswordActivity.class,
        PhoneAuthActivity.class,
        OurAuthActivity.class,
        FirebaseUIActivity.class,
    };

    private static final int[] DESCRIPTION_IDS = new int[] {
        R.string.desc_google_sign_in,
        R.string.desc_emailpassword,
        R.string.desc_nopassword,
        R.string.desc_phone_auth,
        R.string.desc_our_auth,
        R.string.desc_firebase_ui,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);

        ListView listView = findViewById(R.id.listView);

        arrayAdapter adapter = new arrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES);
        adapter.setDescriptionIds(DESCRIPTION_IDS);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class clicked = CLASSES[position];
        startActivity(new Intent(this, clicked));
    }

    public static class arrayAdapter extends ArrayAdapter<Class> {
        private Context fContext;
        private Class[] fClasses;
        private int[] fDescriptionIds;

        public arrayAdapter(Context context, int resource, Class[] objects) {
            super(context, resource, objects);

            fContext = context;
            fClasses = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) fContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(android.R.layout.simple_list_item_2, null);

            }

            ((TextView) view.findViewById(android.R.id.text1)).setText(fClasses[position].getSimpleName());
            ((TextView) view.findViewById(android.R.id.text2)).setText(fDescriptionIds[position]);

            return view;
        }

        public void setDescriptionIds(int[] descriptionIds) {
            fDescriptionIds = descriptionIds;
        }
    }
}
