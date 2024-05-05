package com.example.cs4084_project;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AccountFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    Button signOutButton;
    ArrayList<String> friends_list = new ArrayList<>();
    ArrayList<String> post_list = new ArrayList<>();

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        auth = FirebaseAuth.getInstance();
        signOutButton = rootView.findViewById(R.id.sign_out);
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user == null) {
            Intent intent = new Intent(requireActivity().getApplicationContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        }

        fillUserInfo(rootView, user.getUid());

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(requireActivity().getApplicationContext(), Login.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });



        return rootView;
    }

    private void fillUserInfo(View view, String uid) {

        ImageView profilePicture = view.findViewById(R.id.profile_picture);
        TextView username = view.findViewById(R.id.profile_name);
        TextView post_count = view.findViewById(R.id.profile_post_count);
        TextView friend_count = view.findViewById(R.id.profile_friends_count);

        DocumentReference userDoc = db.collection("users").document(uid);

        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String profilePic = task.getResult().getString("profilePicture");
                    String name = task.getResult().getString("username");
                    int friend_list_size = 0;
                    int post_list_size = getPostCount(task.getResult().getString("uid"));

                    username.setText(name);
                    if (profilePic != null) {
                        profilePicture.setImageTintList(null);
                        Picasso.get().load(profilePic).into(profilePicture);
                    }

                    ArrayList<String> friends = (ArrayList<String>) task.getResult().get("friends");

                    if (friends != null) {
                        friends_list.addAll(friends);
                        friend_list_size = friends.size();
                    }
                    String friend_string = "Friends: " + friend_list_size;
                    String post_string = "Posts: " + post_list_size;
                    friend_count.setText(friend_string);
                    post_count.setText(post_string);
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private int getPostCount(String uid) {

        int post_count = 0;

        return post_count;

    }
}