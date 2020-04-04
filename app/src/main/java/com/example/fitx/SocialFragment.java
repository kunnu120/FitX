package com.example.fitx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class SocialFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 10101;
    private ImageView img;
    private EditText e;
    private TextView txt;
    private String userid;
    private StorageReference storageRef;

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_social, null);

        e = v.findViewById(R.id.textField);
        img = v.findViewById(R.id.profile_pic);
        storageRef = FirebaseStorage.getInstance().getReference();
        userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Glide.with(getContext()).load(storageRef.child("test.jpg")).into(img);
        e.setText("Hello");

        return inflater.inflate(R.layout.fragment_social, null);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            //uri = data.getData();
            //Picasso.get().load(uri).into(img);
            //ProfilePicUrlRef.setValue(uri.toString());
        }
    }

    public void createPost(String text, Uri uri) {

    }

    //saltRef.addListenerForSingleValueEvent(saltListener(acct.getId(),uid))
    //DataSnapshot dataSnapshot
    //String url = dataSnapshot.getValue(String.class);
    //img = v.findViewById(R.id.profile_pic);
    //Glide.with(getContext()).load(url).into(img);

}
