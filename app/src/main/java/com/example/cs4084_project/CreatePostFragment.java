package com.example.cs4084_project;

import static android.content.ContentValues.TAG;

import static com.example.cs4084_project.classes.Utils.compressImage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cs4084_project.classes.Cafe;
import com.example.cs4084_project.classes.Post;
import com.example.cs4084_project.classes.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreatePostFragment extends Fragment implements EnterCafeDialogFragment.EnterCafeDialogListener {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseStorage storage;
    FirebaseFirestore db;
    TextInputEditText titleTextEdit;
    TextInputEditText descriptionTextEdit;
    TextView cafe;
    String cafeName = "";
    String cafeLocation = "";
    ImageView imagePreview;
    ImageView oneStarButton;
    ImageView twoStarButton;
    ImageView threeStarButton;
    ImageView fourStarButton;
    ImageView fiveStarButton;
    int rating = 0;
    ProgressBar progressBar;
    boolean hasImage = false;
    Post post;
    boolean isEditingPost = false;

    public CreatePostFragment() {
    }

    public CreatePostFragment(Post post) {
        this.post = post;
        isEditingPost = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_post, container, false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        if (user == null) {
            Intent intent = new Intent(requireActivity().getApplicationContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        }

        Button backButton = rootView.findViewById(R.id.back_button);
        if (isEditingPost) {
            backButton.setOnClickListener(v -> navigateToPostFragment(post));
        } else {
            backButton.setOnClickListener(v -> navigateToHomeFragment());
        }

        TextView headingTextView = rootView.findViewById(R.id.heading);
        titleTextEdit = rootView.findViewById(R.id.post_title);
        descriptionTextEdit = rootView.findViewById(R.id.post_description);

        Button selectImage = rootView.findViewById(R.id.select_image_button);
        imagePreview = rootView.findViewById(R.id.image_preview);
        selectImage.setOnClickListener(v -> imageSelector());

        cafe = rootView.findViewById(R.id.post_cafe);
        cafe.setOnClickListener(v -> showEnterCafeDialogFragment());

        oneStarButton = rootView.findViewById(R.id.star_one);
        twoStarButton = rootView.findViewById(R.id.star_two);
        threeStarButton = rootView.findViewById(R.id.star_three);
        fourStarButton = rootView.findViewById(R.id.star_four);
        fiveStarButton = rootView.findViewById(R.id.star_five);
        oneStarButton.setOnClickListener(v -> setStars(1));
        twoStarButton.setOnClickListener(v -> setStars(2));
        threeStarButton.setOnClickListener(v -> setStars(3));
        fourStarButton.setOnClickListener(v -> setStars(4));
        fiveStarButton.setOnClickListener(v -> setStars(5));

        progressBar = rootView.findViewById(R.id.progress_bar);
        Button createPostButton = rootView.findViewById(R.id.create_post_button);
        createPostButton.setOnClickListener(v -> createPost());

        if (isEditingPost) {
            headingTextView.setText(R.string.edit_post);
            titleTextEdit.setText(post.getTitle());
            descriptionTextEdit.setText(post.getDescription());
            if (!TextUtils.isEmpty(post.getImagePath())) {
                hasImage = true;
                Picasso.get().load(post.getImagePath()).into(imagePreview);
            }
            if (post.getCafe() != null) {
                Cafe postCafe = post.getCafe();
                cafeName = postCafe.getName();
                cafeLocation = postCafe.getLocation();
                cafe.setText(String.format("%s, %s", cafeName, cafeLocation));
                setStars(post.getRating());
            }
            createPostButton.setText(R.string.edit_post);
        }

        return rootView;
    }

    private void showEnterCafeDialogFragment() {
        EnterCafeDialogFragment dialog = new EnterCafeDialogFragment();
        dialog.show(getChildFragmentManager(), "cafe");
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

    private void navigateToPostFragment(Post postToDisplay) {
        Fragment viewPostFragment = new ViewPostFragment(postToDisplay, this);
        FragmentManager fm = getParentFragmentManager();
        fm.popBackStack();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.slide_out_left)
                .replace(R.id.flFragment, viewPostFragment)
                .commit();
    }

    private void imageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        launchActivity.launch(intent);
    }
    ActivityResultLauncher<Intent> launchActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), selectedImageUri);
                        } catch (IOException e) {
                            Log.d(TAG, String.format("%s at %s", e.getMessage(), Arrays.toString(e.getStackTrace())));
                        }
                        imagePreview.setImageBitmap(selectedImageBitmap);
                        hasImage = true;
                    }
                }
            }
    );

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText cafeNameEditText = Objects.requireNonNull(Objects.requireNonNull(dialog.getDialog()).getWindow()).findViewById(R.id.cafe_name);
        EditText cafeLocationEditText = dialog.getDialog().getWindow().findViewById(R.id.cafe_location);

        cafeName = String.valueOf(cafeNameEditText.getText());
        cafeLocation = String.valueOf(cafeLocationEditText.getText());

        cafe.setText(String.format("%s, %s", cafeName, cafeLocation));
    }

    private void setStars(int stars) {
        fiveStarButton.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.primary));
        fourStarButton.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.primary));
        threeStarButton.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.primary));
        twoStarButton.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.primary));
        oneStarButton.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.primary));
        rating = 0;
        switch (stars) {
            case 5:
                fiveStarButton.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.gold));
                rating++;
            case 4:
                fourStarButton.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.gold));
                rating++;
            case 3:
                threeStarButton.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.gold));
                rating++;
            case 2:
                twoStarButton.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.gold));
                rating++;
            case 1:
                oneStarButton.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.gold));
                rating++;
        }
    }

    private void createPost() {
        progressBar.setVisibility(View.VISIBLE);
        String title, description, postId, posterId;
        title = String.valueOf(this.titleTextEdit.getText());
        description = String.valueOf(this.descriptionTextEdit.getText());

        if (!validateFields(title, description)) {
            return;
        }

        if (isEditingPost && !TextUtils.isEmpty(post.getImagePath())) {
            storage.getReference().child("post-images").child(post.getPostId()).delete();
        }

        String docRef = isEditingPost ? post.getPostId() : UUID.randomUUID().toString();
        postId = docRef;
        posterId = user.getUid();
        Timestamp date = isEditingPost ? post.getDate() : Timestamp.now();
        HashMap<String, Object> postMap = new HashMap<>();

        if (hasImage) {
            imagePreview.setDrawingCacheEnabled(true);
            imagePreview.buildDrawingCache();
            Bitmap imageBitmap = ((BitmapDrawable) imagePreview.getDrawable()).getBitmap();
            imageBitmap = compressImage(imageBitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();

            StorageReference postImagesRef = storage.getReference().child("post-images").child(docRef);

            postImagesRef.putBytes(imageBytes).addOnSuccessListener(task -> {
                postImagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    postMap.put("imagePath", uri.toString());
                    populateRemainderOfPostMap(postMap, postId, posterId, date, title, description);
                    uploadPost(postId, postMap);

                });
            });
        } else {
            postMap.put("imagePath", "");
            populateRemainderOfPostMap(postMap, postId, posterId, date, title, description);
            uploadPost(postId, postMap);
        }
    }

    private boolean validateFields(String title, String description) {
        if (TextUtils.isEmpty(title) || !validateTitle(title)) {
            Toast.makeText(getActivity(), "Please enter a title between 2 and 32 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(description) || !validateDescription(description)) {
            Toast.makeText(getActivity(), "Please enter a description between 2 and 300 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!TextUtils.isEmpty(cafeName) && !TextUtils.isEmpty(cafeLocation) && rating == 0) {
            Toast.makeText(getActivity(), "Please enter your rating for this cafe", Toast.LENGTH_SHORT).show();
            return false;
        }
        if ((TextUtils.isEmpty(cafeName) || TextUtils.isEmpty(cafeLocation)) && rating != 0) {
            Toast.makeText(getActivity(), "Please enter a cafe that you are rating", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private static boolean validateTitle(String title) {
        Pattern pattern;
        Matcher matcher;
        final String NAME_PATTERN = "^.{2,32}$";
        pattern = Pattern.compile(NAME_PATTERN);
        matcher = pattern.matcher(title);

        return matcher.matches();
    }

    private static boolean validateDescription(String description) {
        Pattern pattern;
        Matcher matcher;
        final String NAME_PATTERN = "^.{2,300}$";
        pattern = Pattern.compile(NAME_PATTERN);
        matcher = pattern.matcher(description);

        return matcher.matches();
    }

    private void populateRemainderOfPostMap(HashMap<String, Object> postMap, String postId, String posterId, Timestamp date, String title, String description) {
        postMap.put("cafeId", "-");
        if ((!TextUtils.isEmpty(cafeName)) && (!TextUtils.isEmpty(cafeLocation)) && !(rating == 0)) {
            HashMap<String, Object> cafeMap = new HashMap<>();
            cafeMap.put("name", cafeName);
            cafeMap.put("location", cafeLocation);
            db.collection("cafes").document(cafeName+cafeLocation).set(cafeMap);
            db.collection("cafes").document(cafeName+cafeLocation).update("ratings", FieldValue.arrayUnion(rating));
            postMap.replace("cafeId", cafeName+cafeLocation);
        }

        postMap.put("postId", postId);
        postMap.put("posterId", posterId);
        postMap.put("date", date);
        postMap.put("title", title);
        postMap.put("description", description);
        postMap.put("likes", 0);
        postMap.put("dislikes", 0);
        postMap.put("rating", rating);
    }

    private void uploadPost(String postId, HashMap<String, Object> postMap) {
        db.collection("posts").document(postId).set(postMap).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                if (isEditingPost) {
                    Toast.makeText(requireContext(), "Post edited successfully!", Toast.LENGTH_SHORT).show();
                    db.collection("posts").document(post.getPostId()).get().addOnSuccessListener(documentSnapshot -> navigateToPostFragment(documentSnapshot.toObject(Post.class)));
                } else {
                    Toast.makeText(requireContext(), "Post created successfully!", Toast.LENGTH_SHORT).show();
                    navigateToHomeFragment();
                }
            } else {
                Toast.makeText(getActivity(), "Error creating post, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}