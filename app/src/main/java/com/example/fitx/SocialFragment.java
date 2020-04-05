package com.example.fitx;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class SocialFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 10101;
    private ImageView img;
    private EditText postText;
    private Button btnupload;
    private TextView txt;
    private String userid;
    private Uri uri;
    private ProgressBar progressBar;
    private List<String> postList = new ArrayList<String>();
    private DatabaseReference postListRef;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads");

    private ValueEventListener postListener = new ValueEventListener() {
        @Override
        @SuppressWarnings("unchecked")
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            try {
                postList.addAll((ArrayList<String>)dataSnapshot.getValue());
                //for (int i = 0; i < postList.size(); ++i) {
                    //adapter.add(Security.decodeSaltCipher(Security.decB64(goalsEnc.get(i))));
                //}
            } catch (Exception e) {

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_social, null);

        postText = v.findViewById(R.id.postTextField);

        img = v.findViewById(R.id.postpic);
        img.setOnClickListener(v1 -> {
            openFileChooser();
        });

        progressBar = v.findViewById(R.id.ventilator_progress);
        progressBar.setVisibility(View.INVISIBLE);

        btnupload = v.findViewById(R.id.btnPostUpload);
        btnupload.setEnabled(false);
        btnupload.setOnClickListener(x -> {
            uploadPost();
        });

        userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        postList = new ArrayList<>();
        postListRef = db.getReference("postList");
        postListRef.addListenerForSingleValueEvent(postListener);
        return v;
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
            uri = data.getData();
            Glide.with(getContext()).load(uri).into(img);
            btnupload.setEnabled(true);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadPost() {
        String postid = "" + System.currentTimeMillis();
        String post = userid + "." + postid + "." + postText.getText().toString();
        postList.add(post);
        postListRef.setValue(postList);
        postText.setText("Uploading...");
        btnupload.setEnabled(false);
        if (img != null) {
            StorageReference fileRef = storageRef.child(postid + "." + getFileExtension(uri));
            progressBar.setVisibility(View.VISIBLE);
            fileRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                                img.setImageResource(android.R.color.white);
                                postText.setText("Complete!");
                            }
                        }, 5000);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    }
                });
        }
    }
}
