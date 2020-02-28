package com.example.fitx;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;
import java.lang.String;
import static android.app.Activity.RESULT_OK;



public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private Uri uri;
    private Button btnupload;
    private ImageView img;
    private ProgressBar progressBar;
    //FirebaseStorage storage;
    private DatabaseReference ProfilePicUrlRef;
    private StorageReference storageRef;
    private ArrayList<String> goals;
    private DatabaseReference goalsRef;
    private ArrayAdapter<String> adapter;


    private ValueEventListener goalListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            try {
                adapter.addAll((ArrayList<String>) dataSnapshot.getValue());
            } catch (NullPointerException e) {

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }


    };


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_profile, null);

        img = v.findViewById(R.id.profile_pic);
        progressBar = v.findViewById(R.id.ventilator_progress);
        //StorageReference imageRef = storageRef.child("1575623427796.jpg");
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        btnupload = v.findViewById(R.id.btnUpload);
        ListView goalView = v.findViewById(R.id.goalList);
        String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        ProfilePicUrlRef = db.getReference("Users").child(userid).child("ProfilePicURL");


        ProfilePicUrlRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String url = dataSnapshot.getValue(String.class);
                    //Picasso.get().load(url).into(img);
                    Glide.with(getContext()).load(url).into(img);
                } catch (NullPointerException e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        img.setOnClickListener(v1 -> {

            openFileChooser();

        });

        btnupload.setOnClickListener(v12 -> {
            uploadFile();
        });


        goals = new ArrayList<>();
        goalsRef = db.getReference("Users").child(userid).child("Goals");
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


        return v;
    }

//#################################### GOAL CODE ############################################

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void editGoalDialog(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
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

    public void addGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
        builder.setTitle("Add Goal");
        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", (d, w) -> {
            adapter.add(input.getText().toString());
            goalsRef.setValue(goals);
        });
        builder.setNegativeButton("Cancel", (d, w) -> {
            d.cancel();
        });

        builder.show();
    }

    public void button2Clicked(View v) {

        EditText editTextHeight = (EditText) getView().findViewById(R.id.userHeight);
        EditText editTextWeight = (EditText) getView().findViewById(R.id.userWeight);
        TextView textViewResult = (TextView) getView().findViewById(R.id.userBMI);

        double height = Double.parseDouble(editTextHeight.getText().toString());
        double weight = Double.parseDouble(editTextWeight.getText().toString());

        double BMI = weight / (height * height);

        textViewResult.setText(Double.toString(BMI));
    }

    //#########################GOAL CODE END ###########################################

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();
            Picasso.get().load(uri).into(img);
            ProfilePicUrlRef.setValue(uri.toString());
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (img != null) {
            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));

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

                            //uri = taskSnapshot.getUploadSessionUri();
                            //String uploadUri = uri.toString();
                            //ProfilePicUrlRef.setValue(uploadUri);

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