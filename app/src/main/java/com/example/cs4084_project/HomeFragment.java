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
import android.widget.ListView;

import com.example.cs4084_project.classes.Cafe;
import com.example.cs4084_project.classes.Comment;
import com.example.cs4084_project.classes.Post;
import com.example.cs4084_project.classes.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment implements PostAdapter.OpenPost {

    private FirebaseFirestore db;
    private ArrayList<Post> allPosts = new ArrayList<>();
    private PostAdapter adapter;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user == null) {
            Intent intent = new Intent(requireActivity().getApplicationContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        }

        Button createPostButton = rootView.findViewById(R.id.create_post);
        createPostButton.setOnClickListener(v -> {
            Fragment createPostFragment = new CreatePostFragment();
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.fade_out)
                    .addToBackStack(null)
                    .replace(R.id.flFragment, createPostFragment)
                    .commit();
        });
        this.getAllPosts();
        this.setProfilePictureImageView(rootView, user.getUid());
        ListView postsListView = rootView.findViewById(R.id.posts);
        adapter = new PostAdapter(this.getContext(), allPosts, this);
        postsListView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void openPost(Post post) {
        db.collection("posts").document(post.getPostId()).collection("comments").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot queryDocumentSnapshots = task.getResult();
                ArrayList<Comment> comments = new ArrayList<>();
                for (DocumentSnapshot d : queryDocumentSnapshots) {
                    Comment comment = d.toObject(Comment.class);
                    comments.add(comment);
                }
                post.setComments(comments);
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
            Fragment viewPostFragment = new ViewPostFragment(post, this);
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.fade_out)
                    .addToBackStack(null)
                    .replace(R.id.flFragment, viewPostFragment)
                    .commit();
        });
    }

    private void setProfilePictureImageView(View view, String uid) {
        ImageView profilePicture = view.findViewById(R.id.profile_picture);
        DocumentReference userDoc = db.collection("users").document(uid);
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String profilePic = task.getResult().getString("profilePicture");
                    if (profilePic != null) {
                        profilePicture.setImageTintList(null);
                        Picasso.get().load(profilePic).into(profilePicture);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void getAllPosts() {
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = document.toObject(Post.class);
                                db.collection("users").document(post.getPosterId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            String profilePic = task.getResult().getString("profilePicture");
                                            if (profilePic != null) {
                                                post.setProfilePicturePath(profilePic);
                                            }
                                            post.setUsername(task.getResult().getString("username"));
                                            db.collection("cafes").document(post.getCafeId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    Cafe cafe = task.getResult().toObject(Cafe.class);
                                                    if (cafe != null) {
                                                        post.setCafe(cafe);
                                                    }
                                                    allPosts.add(post);
                                                    Collections.sort(allPosts);
                                                    Collections.reverse(allPosts);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });

                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        allPosts = new ArrayList<>();
    }
}