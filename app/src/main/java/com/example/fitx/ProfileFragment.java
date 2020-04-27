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


import org.w3c.dom.Text;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.lang.String;
import static android.app.Activity.RESULT_OK;


//Creating public class profilefragment that extends the fragment and implements AdapterView.OnItemSelectedListener
public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    //Setting the global variables we need for this class
    ArrayAdapter<String> myAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private Uri uri;
    private Button btnupload;
    private TextView prftxt;
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


    //goallistener that is the valueeventlistner
    private ValueEventListener goalListener = new ValueEventListener() {
        @Override
        @SuppressWarnings("unchecked")
        //when data is changed it go through the arraylist goals and decode it
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            try {
                goalsEnc.addAll((ArrayList<String>) Objects.requireNonNull(dataSnapshot.getValue()));
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    for (int i = 0; i < goalsEnc.size(); ++i) {
                        adapter.add(Security.decode(goalsEnc.get(i)));
                    }
                }, 100);
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
        //Creating a spinner for gender
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_profile, null);

        //  View v = inflater.inflate(R.layout.manual, container, false);

        //Values in the spinner is going to be Male, female and other
        String [] values =
                {"Male","Female","Other"};

        //Looking to see what's in the spinner provided by the user
        Spinner spinner = (Spinner) v.findViewById(R.id.spinner1);
        //Saving the infor to the ArrayAdapter<String> created myAdapter
        myAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        myAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(myAdapter);

        //getting the profile pic, and information on the profile page and saving it to newly created variables
        img = v.findViewById(R.id.profile_pic);
        prftxt = v.findViewById(R.id.profile_txt);
        progressBar = v.findViewById(R.id.ventilator_progress);
        progressBar.setVisibility(View.INVISIBLE);

        //StorageReference imageRef = storageRef.child("1575623427796.jpg");

        //saving the pictures to the firebase under profilepics reference
        storageRef = FirebaseStorage.getInstance().getReference("profilepics");

        btnupload = v.findViewById(R.id.btnUpload);
        ListView goalView = v.findViewById(R.id.goalList);
        userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        ProfilePicUrlRef = db.getReference("Users").child(userid).child("ProfilePicURL");


        //this automatically updated the changes on the profile page
        ProfilePicUrlRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String url = dataSnapshot.getValue(String.class);
                    if (!url.isEmpty()) {
                        Glide.with(getContext()).load(url).into(img);
                        prftxt.setVisibility(View.INVISIBLE);
                    }
                } catch (NullPointerException e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //cliecked on upload button
        btnupload.setOnClickListener(v1 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        goals = new ArrayList<>();

        //saving the data to the firebase under users->child(GoalsEnc) reference
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

    //Converting the selected string at position passed in the parameter and then showing it as toast
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    //onNothignselected void function
    public void onNothingSelected(AdapterView<?> parent) {

    }

//#################################### GOAL CODE ############################################

    //EditGoalDialog that takes the parameter which is integer values named pos - can edit the goal.
    public void editGoalDialog(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
        builder.setTitle("Edit/Delete Goal");
        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(adapter.getItem(pos), TextView.BufferType.EDITABLE);
        builder.setView(input);
        builder.setPositiveButton("Edit", (d,w) -> {
            String s = input.getText().toString();

            //if string s is empty, user didn't provide any information and should toast - Goals cannot be empty
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
        //deleting the specific goal at a position
        builder.setNegativeButton("Delete", (d,w) -> {
            adapter.remove(adapter.getItem(pos));
            goalsEnc.remove(pos);
            goalsRef.setValue(goalsEnc);
            d.cancel();
        });

        builder.show();
    }

    //Adding the goal for the user on the profile page
    public void addGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogStyle);
        builder.setTitle("Add Goal");
        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", (d, w) -> {
            String s = input.getText().toString();

            //if string s empty tell user that cannot add empty goal, else it should add the goal if the string s is not empty
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



    //if the bmi button is clicked, show the user their BMI
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

    //Uploading the profile picture, when user wants to upload the picture and click the upload button - this function will be called
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            ProfilePicUrlRef.setValue(uri.toString());
            btnupload.setEnabled(false);
            StorageReference fileRef = storageRef.child(userid + "." + getFileExtension(uri));
            progressBar.setVisibility(View.VISIBLE);
            fileRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        //successfully upload the picture
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btnupload.setEnabled(true);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Glide.with(getContext()).load(uri).into(img);
                                    prftxt.setVisibility(View.INVISIBLE);
                                }
                            }, 5000);

                            //uri = taskSnapshot.getUploadSessionUri();
                            //String uploadUri = uri.toString();
                            //ProfilePicUrlRef.setValue(uploadUri);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {

                        //Upload failed
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Upload failed...",
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
        }
    }

    //Passing Uri and returning string uri after converting
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}