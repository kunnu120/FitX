package com.example.fitx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostAdapter extends
        RecyclerView.Adapter<PostAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView name;
        public TextView text;
        public TextView date;
        public ImageView img;
        public ImageView pfpic;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.user_name);
            text = (TextView) itemView.findViewById(R.id.post_text);
            date = (TextView) itemView.findViewById(R.id.date_text);
            img = (ImageView) itemView.findViewById(R.id.post_img);
            pfpic = (ImageView) itemView.findViewById(R.id.prof_img);
        }

    }

    private List<Post> mPosts;
    private String email;

    private final RequestManager glide;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference uploadRef = storage.getReference("uploads");
    private final StorageReference profileRef = storage.getReference("profilepics");
    private final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

    // Pass in the contact array into the constructor
    public PostAdapter(RequestManager _glide, List<Post> posts) {
        glide = _glide;
        mPosts = posts;
    }


    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View postView = inflater.inflate(R.layout.item_post, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Post p = mPosts.get(position);

        // Set item views based on your views and data model
        TextView name = viewHolder.name;
        TextView text = viewHolder.text;
        TextView date = viewHolder.date;
        ImageView img = viewHolder.img;
        ImageView pfpic = viewHolder.pfpic;

        text.setText(p.getPostText());
        date.setText("" + new Date(Long.parseLong(p.getPostid())));
        userRef.child(p.getUserid()+"/Email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    name.setText(dataSnapshot.getValue(String.class));
                } catch (NullPointerException e) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        glide.load(uploadRef.child(p.getPostid()+".jpg")).into(img);
        glide.load(profileRef.child(p.getUserid()+".jpg")).signature(
                new ObjectKey(System.currentTimeMillis())).into(pfpic);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mPosts.size();
    }


}
