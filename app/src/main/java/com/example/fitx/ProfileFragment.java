package com.example.fitx;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Base64;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import org.w3c.dom.Text;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.lang.String;
import static android.app.Activity.RESULT_OK;



public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    ArrayAdapter<String> myAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private Uri uri;
    private Button btnupload;
    private ImageView img;
    private ProgressBar progressBar;

    private DatabaseReference ProfilePicUrlRef;
    private StorageReference storageRef;
    private List<String> goals;
    private List<String> goalsEnc = new ArrayList<String>();
    private DatabaseReference goalsRef;
    private ArrayAdapter<String> adapter;
    private String userid;
    TextView display_data;



    private ValueEventListener goalListener = new ValueEventListener() {
        @Override
        @SuppressWarnings("unchecked")
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            try {
                goalsEnc.addAll((ArrayList<String>)dataSnapshot.getValue());
                for (int i = 0; i < goalsEnc.size(); ++i) {
                    adapter.add(Security.decode(goalsEnc.get(i)));
                }
            } catch (Exception e) {

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

        //  View v = inflater.inflate(R.layout.manual, container, false);

        String [] values =
                {"Male","Female","Other"};
        Spinner spinner = (Spinner) v.findViewById(R.id.spinner1);
        myAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        myAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(myAdapter);


        img = v.findViewById(R.id.profile_pic);
        progressBar = v.findViewById(R.id.ventilator_progress);
        progressBar.setVisibility(View.INVISIBLE);

        //StorageReference imageRef = storageRef.child("1575623427796.jpg");

        storageRef = FirebaseStorage.getInstance().getReference("profilepics");

        btnupload = v.findViewById(R.id.btnUpload);
        btnupload.setEnabled(false);
        ListView goalView = v.findViewById(R.id.goalList);
        userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
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

        goalsRef = db.getReference("Users").child(userid).child("GoalsEnc");
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

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView<?> parent) {

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
        builder.setTitle("Edit/Delete Goal");
        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(adapter.getItem(pos), TextView.BufferType.EDITABLE);
        builder.setView(input);
        builder.setPositiveButton("Edit", (d,w) -> {
            String s = input.getText().toString();
            if (s.isEmpty()) {
                Toast.makeText(getContext(), "Goals cannot be empty!",
                        Toast.LENGTH_SHORT).show();
            } else {
                adapter.remove(adapter.getItem(pos));
                adapter.insert(s, pos);
                goalsEnc.set(pos, Security.encode(s));
                goalsRef.setValue(goalsEnc);
            }
        });
        builder.setNegativeButton("Delete", (d,w) -> {
            adapter.remove(adapter.getItem(pos));
            goalsEnc.remove(pos);
            goalsRef.setValue(goalsEnc);
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
            String s = input.getText().toString();
            if (s.isEmpty()) {
                Toast.makeText(getContext(), "Cannot add empty goal!",
                        Toast.LENGTH_SHORT).show();
            } else {
                adapter.add(s);
                goalsEnc.add(Security.encode(s));
                goalsRef.setValue(goalsEnc);
            }
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
            //Picasso.get().load(uri).into(img);
            Glide.with(getContext()).load(uri).into(img);
            ProfilePicUrlRef.setValue(uri.toString());
            btnupload.setEnabled(true);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (img != null) {
            StorageReference fileRef = storageRef.child(userid + "." + getFileExtension(uri));
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