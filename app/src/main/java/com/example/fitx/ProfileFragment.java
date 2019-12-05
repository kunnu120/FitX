package com.example.fitx;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uri;
    private Button btnupload;
    private ImageView img;
    private ProgressBar progressBar;
    //FirebaseStorage storage;
    private DatabaseReference imgdatabRef;
    private StorageReference storageRef;
    private ArrayList<String> goals;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference goalsRef;
    private ArrayAdapter<String> adapter;


    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        ArrayList<String> goalssave = new ArrayList<>();
        goalssave.addAll(goals);
        savedInstanceState.putStringArrayList("GOALS", goalssave );
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_profile, null);
        storageRef = FirebaseStorage.getInstance().getReference("Images");

        progressBar = v.findViewById(R.id.ventilator_progress);
        img = v.findViewById(R.id.profile_pic);
        btnupload = v.findViewById(R.id.btnUpload);
        ListView goalView = v.findViewById(R.id.goalList);
        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        imgdatabRef = FirebaseDatabase.getInstance().getReference("uploads");


        img.setOnClickListener(v1 -> {
            //Intent intent = new Intent();
            //intent.setType("image/*");
            //intent.setAction(Intent.ACTION_GET_CONTENT);
            //startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

            openFileChooser();

        });

        btnupload.setOnClickListener(v12 -> {
            uploadfile();
        });

        if(savedInstanceState!=null){
            goals = savedInstanceState.getStringArrayList("GOALS");
        }else {
            goals = new ArrayList<>();
        }
        goalsRef = db.getReference("profile").child("goals");
        goalsRef.addListenerForSingleValueEvent(goalListener);
        adapter = new ArrayAdapter<>(
                this.getActivity(), android.R.layout.simple_list_item_1, goals);
        goalView.setAdapter(adapter);
        goalView.setOnItemClickListener((p, view, pos, id) -> {
            editGoalDialog(pos);
        });
        v.findViewById(R.id.btnAddGoal).setOnClickListener(v1 -> {
            addGoalDialog();
        });

        //FirebaseStorage storage = FirebaseStorage.getInstance("gs://fitx-71ea1.appspot.com");
        //StorageReference sRef = storage.getReference();

        //Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        //StorageReference riversRef = sRef.child("images/"+file.getLastPathSegment());
        //UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        //uploadTask.addOnFailureListener(new OnFailureListener() {
        //    @Override
        //    public void onFailure(@NonNull Exception exception) {
        // Handle unsuccessful uploads
        //    }
        //}).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        //    @Override
        //    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
        // ...
        //    }
        //});


        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        return v;
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

//#################################### GOAL CODE ############################################

    public void addGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Add Goal");
        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", (d,w) -> {
                adapter.add(input.getText().toString());
                goalsRef.setValue(goals);
            });
        builder.setNegativeButton("Cancel", (d,w) -> {
                d.cancel();
            });

        builder.show();
    }

    public void editGoalDialog(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Add Goal");
        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(adapter.getItem(pos), TextView.BufferType.EDITABLE);
        builder.setView(input);
        builder.setPositiveButton("Edit", (d,w) -> {
            adapter.remove(adapter.getItem(pos));
            adapter.insert(input.getText().toString(),pos);
            goalsRef.setValue(goals);
        });
        builder.setNegativeButton("Delete", (d,w) -> {
            adapter.remove(adapter.getItem(pos));
            goalsRef.setValue(goals);
            d.cancel();
        });

        builder.show();
    }

    ValueEventListener goalListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            adapter.addAll((ArrayList<String>)dataSnapshot.getValue());
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //#########################GOAL CODE END ###########################################

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            Picasso.get().load(uri).into(img);
        }
    }


    private String getFileExtentsion(Uri uri){
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadfile(){
        if(img!=null) {
            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtentsion(uri));

            fileRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 5000);

                            //empty upload names for now edittextname.getText().toString().trim()
                            Upload upload = new Upload("", taskSnapshot.getUploadSessionUri().toString());
                            String uploadId = imgdatabRef.push().getKey();
                            imgdatabRef.child(uploadId).setValue(upload);
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