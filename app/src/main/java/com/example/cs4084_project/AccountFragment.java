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
import android.widget.TextView;

import com.example.cs4084_project.classes.Cafe;
import com.example.cs4084_project.classes.Comment;
import com.example.cs4084_project.classes.Friend;
import com.example.cs4084_project.classes.FriendAdapter;
import com.example.cs4084_project.classes.Post;
import com.example.cs4084_project.classes.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class AccountFragment extends Fragment implements PostAdapter.OpenPost, FriendAdapter.OpenFriend {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    Button signOutButton;
    ArrayList<Friend> friend_list = new ArrayList<>();
    ArrayList<Post> post_list = new ArrayList<>();
    ListView list;
    PostAdapter adapter_post;
    FriendAdapter adapter_friends;
    private int post_int_val = 0;
    private int friend_int_val  = 0;
    TextView post_count;
    TextView friend_count;

    public AccountFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        signOutButton = rootView.findViewById(R.id.sign_out);

        if (user == null) {
            Intent intent = new Intent(requireActivity().getApplicationContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        }
        getUserPosts(user.getUid());
        getUserFriends(user.getUid());

        fillUserInfo(rootView, user.getUid());
        friend_count = rootView.findViewById(R.id.profile_friends_count);
        post_count = rootView.findViewById(R.id.profile_post_count);


        list = rootView.findViewById(R.id.posts);
        adapter_post = new PostAdapter(requireContext(), post_list,this);
        adapter_friends = new FriendAdapter(requireContext(), friend_list, this);
        list.setAdapter(adapter_post);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(requireActivity().getApplicationContext(), Login.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        setAdapterListeners(rootView, adapter_post, adapter_friends);
        return rootView;
    }
    private void setAdapterListeners(View view, PostAdapter adapter_post, FriendAdapter adapter_friend) {

        TextView posts = view.findViewById(R.id.profile_post_button);
        TextView friends = view.findViewById(R.id.profile_friends_button);

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.setAdapter(adapter_post);
            }
        });

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.setAdapter(adapter_friend);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        post_list.clear();
        post_int_val = 0;
    }

    private void fillUserInfo(View view, String uid) {

        ImageView profilePicture = view.findViewById(R.id.profile_picture);
        TextView username = view.findViewById(R.id.profile_name);
        TextView friend_count = view.findViewById(R.id.profile_friends_count);

        DocumentReference userDoc = db.collection("users").document(uid);

        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String profilePic = task.getResult().getString("profilePicture");
                    String name = task.getResult().getString("username");

                    username.setText(name);
                    if (profilePic != null) {
                        profilePicture.setImageTintList(null);
                        Picasso.get().load(profilePic).into(profilePicture);
                    }

                    int post_list_size = post_list.size();
                    int friend_list_size = friend_list.size();
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

    private void getUserFriends(String uid) {
        CollectionReference users = db.collection("users");
        users.whereArrayContains("friends", uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Friend f = new Friend();
                            f.setUid(document.getString("uid"));
                            f.setUsername(document.getString("username"));
                            f.setProfilePic(document.getString("profilePicture"));
                            friend_list.add(f);

                            friend_int_val++;
                            String outputFriendString = "Friends: " + friend_int_val;
                            friend_count.setText(outputFriendString);

                            adapter_friends.notifyDataSetChanged();
                        }
                    } else {
                        Log.e("Firestore", "Error fetching friends: " + task.getException());
                    }
                });
    }

    private void getUserPosts(String uid) {

        db.collection("posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Post post = document.toObject(Post.class);
                            if (post.getPosterId().equals(uid)) {
                                db.collection("users").document(post.getPosterId()).get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        String profilePic = task1.getResult().getString("profilePicture");
                                        if (profilePic != null) {
                                            post.setProfilePicturePath(profilePic);
                                        }
                                        post.setUsername(task1.getResult().getString("username"));
                                        db.collection("cafes").document(post.getCafeId()).get().addOnCompleteListener(task11 -> {
                                            Cafe cafe = task11.getResult().toObject(Cafe.class);
                                            if (cafe != null) {
                                                post.setCafe(cafe);
                                            }
                                            post_list.add(post);
                                            post_int_val++;
                                            String outputPostString = "Posts: " + post_int_val;
                                            post_count.setText(outputPostString);

                                            Collections.sort(post_list);
                                            Collections.reverse(post_list);
                                            adapter_post.notifyDataSetChanged();
                                        });

                                    } else {
                                        Log.d(TAG, "get failed with ", task1.getException());
                                    }
                                });
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    @Override
    public void openPost(Post post) {
        post_list.clear();
        post_int_val = 0;
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

    @Override
    public void openFriend(String friendId) {

    }
}