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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
    private EditText postTextField;
    private Button btnupload;
    private TextView txt;
    private String userid;
    private Uri uri;
    private ProgressBar progressBar;
    private List<Post> postList = new ArrayList<Post>();
    private DatabaseReference postListRef;
    private PostAdapter adapter;
    private RecyclerView rvPosts;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads");

    private ChildEventListener postListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            postList.add(dataSnapshot.getValue(Post.class));
            adapter.notifyItemInserted(postList.size()-1);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

        postTextField = v.findViewById(R.id.postTextField);

        img = v.findViewById(R.id.postpic);
        img.setOnClickListener(v1 -> {
            openFileChooser();
        });

        progressBar = v.findViewById(R.id.ventilator_progress);
        progressBar.setVisibility(View.INVISIBLE);

        btnupload = v.findViewById(R.id.btnPostUpload);
        btnupload.setOnClickListener(x -> {
            if (uri == null & postTextField.getText().toString() == "") {
                Toast.makeText(getContext(), "Cannot upload empty post!",
                        Toast.LENGTH_SHORT).show();
            } else {
                uploadPost();
            }
        });

        uri = null;

        userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        postList = new ArrayList<Post>();
        postListRef = db.getReference("postList");

        rvPosts = (RecyclerView) v.findViewById(R.id.posts_view);
        adapter = new PostAdapter(Glide.with(this), postList);
        // Attach the adapter to the recyclerview to populate items
        rvPosts.setAdapter(adapter);
        // Set layout manager to position the items
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        postListRef.addChildEventListener(postListener);
        return v;
    }

    private void openFileChooser() {
        btnupload.setEnabled(false);
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
        btnupload.setEnabled(false);
        String postText = postTextField.getText().toString();
        String postid = ""+System.currentTimeMillis();
        if (uri != null) {
            progressBar.setVisibility(View.VISIBLE);
            String refS = postid + "." + getFileExtension(uri);
            StorageReference fileRef = storageRef.child(refS);
            fileRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                postTextField.setText("");
                                Post p = new Post(userid,postid,postText, true);
                                postListRef.push().setValue(p);
                                progressBar.setVisibility(View.INVISIBLE);
                                img.setImageResource(android.R.color.white);
                                uri = null;
                                Toast.makeText(getContext(), "Your post has been uploaded.",
                                        Toast.LENGTH_SHORT).show();
                                btnupload.setEnabled(true);
                            }
                        }, 5000);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Upload failed.",
                                Toast.LENGTH_SHORT).show();
                        btnupload.setEnabled(true);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    }
                });
        } else {
            postTextField.setText("");
            Post p = new Post(userid,postid,postText,false);
            postListRef.push().setValue(p);
            Toast.makeText(getContext(), "Your post has been uploaded.",
                    Toast.LENGTH_SHORT).show();
            btnupload.setEnabled(true);
        }
    }
}
