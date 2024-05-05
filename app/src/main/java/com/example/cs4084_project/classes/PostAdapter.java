package com.example.cs4084_project.classes;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cs4084_project.CreatePostFragment;
import com.example.cs4084_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends BaseAdapter {
    private final List<Post> posts;
    private final Context context;
    FirebaseFirestore db;
    FirebaseUser user;
    OpenPost openPostListener;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
        this.db = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public PostAdapter(Context context, List<Post> posts, OpenPost openPostListener) {
        this(context, posts);
        this.openPostListener = openPostListener;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.listitem_post, null);
        Post post = posts.get(position);

        if (!TextUtils.isEmpty(post.getProfilePicturePath())) {
            ImageView profilePicture = view.findViewById(R.id.poster_profile_picture);
            Picasso.get().load(post.getProfilePicturePath()).into(profilePicture);
        }

        TextView username = view.findViewById(R.id.poster_username);
        username.setText(post.getUsername());

        TextView title = view.findViewById(R.id.post_title);
        title.setText(post.getTitle());

        TextView date = view.findViewById(R.id.post_date);
        Date d = post.getDate().toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.UK);
        date.setText(sdf.format(d));

        TextView description = view.findViewById(R.id.post_description);
        description.setText(post.getDescription());

        ImageView image = view.findViewById(R.id.post_image);
        if (post.getImagePath() == null || post.getImagePath().isEmpty()) {
            image.setVisibility(View.GONE);
        } else {
            Picasso.get().load(post.getImagePath()).into(image);
        }

        LinearLayout cafe = view.findViewById(R.id.post_cafe);
        if (post.getCafe() == null) {
            cafe.setVisibility(View.GONE);
        } else {
            TextView cafeName = view.findViewById(R.id.post_cafe_name);
            cafeName.setText(String.format("@ %s,", post.getCafe().getName()));
            TextView cafeLocation = view.findViewById(R.id.post_cafe_location);
            cafeLocation.setText(post.getCafe().getLocation());
            TextView cafeRating = view.findViewById(R.id.post_cafe_rating);
            cafeRating.setText(String.format(Locale.ENGLISH, "%d stars", post.getRating()));
        }

        ImageView like = view.findViewById(R.id.post_like);
        ImageView dislike = view.findViewById(R.id.post_dislike);
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> likes = (ArrayList<String>) task.getResult().get("likes");
                    ArrayList<String> dislikes = (ArrayList<String>) task.getResult().get("dislikes");

                    if (likes != null) {
                        for (String id: likes) {
                            if (post.getPostId().equals(id)) {
                                like.setColorFilter(ContextCompat.getColor(context, R.color.gold));
                                return;
                            }
                        }
                    }
                    if (dislikes != null) {
                        for (String id: dislikes) {
                            if (post.getPostId().equals(id)) {
                                dislike.setColorFilter(ContextCompat.getColor(context, R.color.gold));
                                return;
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        this.updatePostScore(view, post);
        this.setLikeDislikeListeners(view, post);
        this.setCommentListener(view, post);
        return view;
    }

    private void setLikeDislikeListeners(View view, Post post) {
        String postId = post.getPostId();
        ImageView like = view.findViewById(R.id.post_like);
        ImageView dislike = view.findViewById(R.id.post_dislike);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> likes = (ArrayList<String>) task.getResult().get("likes");
                            ArrayList<String> dislikes = (ArrayList<String>) task.getResult().get("dislikes");

                            if (likes != null) {
                                for (String id: likes) {
                                    if (postId.equals(id)) {
                                        unlikePost(post, like);
                                        updatePostScore(view, post);
                                        return;
                                    }
                                }
                            }
                            if (dislikes != null) {
                                for (String id: dislikes) {
                                    if (postId.equals(id)) {
                                        undislikePost(post, dislike);
                                        likePost(post, like);
                                        updatePostScore(view, post);
                                        return;
                                    }
                                }
                            }
                            likePost(post, like);
                            updatePostScore(view, post);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> likes = (ArrayList<String>) task.getResult().get("likes");
                            ArrayList<String> dislikes = (ArrayList<String>) task.getResult().get("dislikes");

                            if (dislikes != null) {
                                for (String id: dislikes) {
                                    if (postId.equals(id)) {
                                        undislikePost(post, dislike);
                                        updatePostScore(view, post);
                                        return;
                                    }
                                }
                            }
                            if (likes != null) {
                                for (String id: likes) {
                                    if (postId.equals(id)) {
                                        unlikePost(post, like);
                                        dislikePost(post, dislike);
                                        updatePostScore(view, post);
                                        return;
                                    }
                                }
                            }
                            dislikePost(post, dislike);
                            updatePostScore(view, post);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });
    }

    private void likePost(Post post, ImageView like) {
        post.addLike();
        db.collection("users").document(user.getUid()).update("likes", FieldValue.arrayUnion(post.getPostId()));
        db.collection("posts").document(post.getPostId()).update("likes", post.getLikes());
        like.setColorFilter(ContextCompat.getColor(context, R.color.gold));
    }

    private void unlikePost(Post post, ImageView like) {
        post.removeLike();
        db.collection("users").document(user.getUid()).update("likes", FieldValue.arrayRemove(post.getPostId()));
        db.collection("posts").document(post.getPostId()).update("likes", post.getLikes());
        like.setColorFilter(ContextCompat.getColor(context, R.color.primary));
    }

    private void dislikePost(Post post, ImageView dislike) {
        post.addDislike();
        db.collection("users").document(user.getUid()).update("dislikes", FieldValue.arrayUnion(post.getPostId()));
        db.collection("posts").document(post.getPostId()).update("dislikes", post.getDislikes());
        dislike.setColorFilter(ContextCompat.getColor(context, R.color.gold));
    }

    private void undislikePost(Post post, ImageView dislike) {
        post.removeDislike();
        db.collection("users").document(user.getUid()).update("dislikes", FieldValue.arrayRemove(post.getPostId()));
        db.collection("posts").document(post.getPostId()).update("dislikes", post.getDislikes());
        dislike.setColorFilter(ContextCompat.getColor(context, R.color.primary));
    }

    private void updatePostScore(View view, Post post) {
        TextView postScore = view.findViewById(R.id.post_score);
        int score = post.getLikes() - post.getDislikes();
        postScore.setText(String.format(Locale.ENGLISH, "%d", score));
    }

    private void setCommentListener(View view, Post post) {
        ImageView commentButton = view.findViewById(R.id.post_comment);
        commentButton.setOnClickListener(v -> openPostListener.openPost(post));
    }

    public interface OpenPost {
        void openPost(Post post);
    }
}
