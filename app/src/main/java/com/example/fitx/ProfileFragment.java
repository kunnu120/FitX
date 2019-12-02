package com.example.fitx;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private static int RESULT_LOAD_IMAGE = 1;
    public Uri uri;
    public ImageView img;
    //FirebaseStorage storage;
    StorageReference storageReference;

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, null);
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        ImageView img = v.findViewById(R.id.profile_pic);
        Button uploadbtn = v.findViewById(R.id.btnUpload);

        img.setOnClickListener(v1 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);


        });

        uploadbtn.setOnClickListener(v12 -> uploadImage());


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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String path = getPath(uri);

            File src = new File(path);
            String filename = uri.getLastPathSegment();
            File destination = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Images/" + filename);
        }
    }


    private String getPath(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            path = uri.getPath();
        } else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }


    private void uploadImage() {
        StorageReference Ref = storageReference.child(System.currentTimeMillis() + "." + getPath(uri));

        Ref.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }
}