package com.example.cs4084_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.cs4084_project.classes.Friend;
import com.example.cs4084_project.classes.FriendAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class CoffeeFragment extends Fragment implements FriendAdapter.OpenFriend {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    ArrayList<Friend> user_list = new ArrayList<>();
    ListView list;
    FriendAdapter adapter_friends;

    public CoffeeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_coffee, container, false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        clear();

        if (user == null) {
            Intent intent = new Intent(requireActivity().getApplicationContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        }

        getAllUsers();

        list = rootView.findViewById(R.id.userList);
        adapter_friends = new FriendAdapter(requireContext(), user_list, this, true);
        list.setAdapter(adapter_friends);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clear();
    }

    private void clear() {
        user_list.clear();
    }


    void getAllUsers() {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (!document.getString("uid").equals(user.getUid())) {
                        Friend f = new Friend();
                        f.setUid(document.getString("uid"));
                        f.setUsername(document.getString("username"));
                        f.setProfilePic(document.getString("profilePicture"));
                        user_list.add(f);
                        adapter_friends.notifyDataSetChanged();
                    }
                }
            } else {
                Log.e("Firestore", "Error fetching friends: " + task.getException());
            }
        });
    }

    @Override
    public void removeFriend(String friendId) {

    }
    @Override
    public void addFriend(String friendId) {
        //check if friend already exists
        db.collection("users").document(user.getUid()).update("friends", FieldValue.arrayUnion(friendId));
        db.collection("users").document(friendId).update("friends", FieldValue.arrayUnion(user.getUid()));

    }
}