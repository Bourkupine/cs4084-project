package com.example.cs4084_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.cs4084_project.classes.Post;
import com.example.cs4084_project.classes.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    ListView postsListView;
    ArrayList<Post> posts = new ArrayList<>();

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(requireActivity().getApplicationContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        }

        Post p = new Post("a", "b", "c", "d", "e");
        posts.add(p);

        postsListView = rootView.findViewById(R.id.posts);
        PostAdapter adapter = new PostAdapter(this.getContext(), posts);
        postsListView.setAdapter(adapter);

        return rootView;
    }
}