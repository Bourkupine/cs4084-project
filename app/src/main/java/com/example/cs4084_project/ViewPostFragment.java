package com.example.cs4084_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cs4084_project.classes.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ViewPostFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    Post post;

    public ViewPostFragment(Post post) {
        this.post = post;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_view_post, container, false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(requireActivity().getApplicationContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        }

        Button backButton = rootView.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> navigateToHomeFragment());

        ImageView editButton = rootView.findViewById(R.id.edit_button);
        ImageView deleteButton = rootView.findViewById(R.id.delete_button);
        if (user.getUid().equals(post.getPosterId())) {
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }

        setPostInformation(rootView);

        return rootView;
    }

    private void navigateToHomeFragment() {
        Fragment homeFragment = new HomeFragment();
        FragmentManager fm = getParentFragmentManager();
        fm.popBackStack();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.slide_out_left)
                .replace(R.id.flFragment, homeFragment)
                .commit();
    }

    private void setPostInformation(View view) {
        ImageView posterProfilePicture = view.findViewById(R.id.poster_profile_picture);
        if (!TextUtils.isEmpty(post.getProfilePicturePath())) {
            Picasso.get().load(post.getProfilePicturePath()).into(posterProfilePicture);
        }

        TextView posterUsername = view.findViewById(R.id.poster_username);
        posterUsername.setText(post.getUsername());

        TextView title = view.findViewById(R.id.post_title);
        title.setText(post.getTitle());

        TextView description = view.findViewById(R.id.post_description);
        description.setText(post.getDescription());

        GridLayout grid = view.findViewById(R.id.post_grid);
        TextView cafe = view.findViewById(R.id.post_cafe);
        ImageView image = view.findViewById(R.id.post_image);
        if (TextUtils.isEmpty(post.getImagePath())) {
            image.setVisibility(View.GONE);
            grid.removeAllViews();
            grid.addView(title);
            grid.addView(description);
            ViewGroup.LayoutParams layoutParams = description.getLayoutParams();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            layoutParams.width = displayMetrics.widthPixels;
            description.setLayoutParams(layoutParams);
            grid.addView(cafe);
        } else {
            Picasso.get().load(post.getImagePath()).into(image);
        }

        if (post.getCafe() != null) {
            cafe.setText(String.format("@%s, %s", post.getCafe().getName(), post.getCafe().getLocation()));
        } else {
            grid.removeView(cafe);
        }
    }
}