package com.example.cs4084_project;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class CreatePostFragment extends Fragment implements EnterCafeDialogFragment.EnterCafeDialogListener {

    FirebaseAuth auth;
    FirebaseUser user;
    TextView cafe;
    ImageView imagePreview;
    ImageView oneStarButton;
    ImageView twoStarButton;
    ImageView threeStarButton;
    ImageView fourStarButton;
    ImageView fiveStarButton;
    int rating;

    public CreatePostFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_post, container, false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(requireActivity().getApplicationContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        }

        Button backButton = rootView.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> navigateToHomeFragment());

        TextInputEditText title = rootView.findViewById(R.id.post_title);
        TextInputEditText description = rootView.findViewById(R.id.post_description);

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

        Button createPostButton = rootView.findViewById(R.id.create_post_button);

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
                    }
                }
            }
    );

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText cafeName = Objects.requireNonNull(Objects.requireNonNull(dialog.getDialog()).getWindow()).findViewById(R.id.cafe_name);
        EditText cafeLocation = dialog.getDialog().getWindow().findViewById(R.id.cafe_location);

        cafe.setText(String.format("%s, %s", cafeName.getText(), cafeLocation.getText()));
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
}