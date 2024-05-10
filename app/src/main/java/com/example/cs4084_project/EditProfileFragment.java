package com.example.cs4084_project;

import static com.example.cs4084_project.classes.Utils.compressImage;
import static com.example.cs4084_project.classes.Utils.validatePassword;
import static com.example.cs4084_project.classes.Utils.validateUsername;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfileFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    String uid;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    View rootView;

    public EditProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        uid = user.getUid();

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                StorageReference storage = FirebaseStorage.getInstance()
                                                          .getReference();
                StorageReference pfp = storage.child("profile_pictures/" + uid + ".jpg");
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                bitmap = compressImage(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                pfp.putBytes(baos.toByteArray())
                   .addOnSuccessListener(task -> {
                       makeToast("New profile picture uploaded successfully");
                       pfp.getDownloadUrl()
                          .addOnSuccessListener(pfp_link -> db.collection("users")
                                                              .document(uid)
                                                              .update("profilePicture", pfp_link.toString())
                                                              .addOnSuccessListener(v -> setProfilePictureImageView(uid)));
                   })
                   .addOnFailureListener(task -> makeToast("Profile picture failed to upload"));
            } else {
                makeToast("Please select an image");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        setProfilePictureImageView(uid);
        getUsername(rootView, db, uid);

        rootView.findViewById(R.id.upload_photo_button)
                .setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));

        rootView.findViewById(R.id.update_username_button)
                .setOnClickListener(v -> {
                    String newUsername = ((EditText) rootView.findViewById(R.id.edit_username_text)).getText()
                                                                                                    .toString();

                    if (TextUtils.isEmpty(newUsername)) {
                        makeToast("Please enter a new username");
                        return;
                    }

                    if (!validateUsername(newUsername)) {
                        makeToast("Invalid username, must be between 2-32 characters");
                        return;
                    }

                    usernameAvailable(newUsername, available -> {
                        if (!available) {
                            makeToast("This username is already taken");
                        } else {
                            db.collection("users")
                              .document(uid)
                              .update("username", newUsername);
                            ((TextView) rootView.findViewById(R.id.current_username_text)).setText(String.format(getString(R.string.current_username), newUsername));
                            makeToast("Username updated successfully");
                        }
                    });
                    ((EditText) rootView.findViewById(R.id.edit_username_text)).getText()
                                                                               .clear();
                });

        rootView.findViewById(R.id.update_password_button)
                .setOnClickListener(v -> {
                    String currentPassword = ((EditText) rootView.findViewById(R.id.current_password_text)).getText()
                                                                                                           .toString();
                    String newPassword = ((EditText) rootView.findViewById(R.id.new_password_text)).getText()
                                                                                                   .toString();
                    String newPasswordConfirm = ((EditText) rootView.findViewById(R.id.new_password_confirm_text)).getText()
                                                                                                                  .toString();

                    if (currentPassword.isEmpty() || newPassword.isEmpty() || newPasswordConfirm.isEmpty()) {
                        makeToast("Please fill in all fields");
                        return;
                    }

                    AuthCredential credentials = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                    user.reauthenticate(credentials)
                        .addOnSuccessListener(task -> {
                            if (newPassword.equalsIgnoreCase(newPasswordConfirm)) {
                                if (validatePassword(newPassword)) {
                                    user.updatePassword(newPassword);
                                    makeToast("Password changed successfully");
                                    ((EditText) rootView.findViewById(R.id.current_password_text)).getText()
                                                                                                  .clear();
                                    ((EditText) rootView.findViewById(R.id.new_password_text)).getText()
                                                                                              .clear();
                                    ((EditText) rootView.findViewById(R.id.new_password_confirm_text)).getText()
                                                                                                      .clear();
                                } else {
                                    makeToast("Passwords should have at least 8 characters including a number and a lower and upper case letter");
                                }
                            } else {
                                makeToast("The new passwords do not match");
                            }
                        })
                        .addOnFailureListener(task -> {
                            makeToast("The current password provided is incorrect");
                        });
                });

        rootView.findViewById(R.id.sign_out_button)
                .setOnClickListener(v -> {
                    auth.signOut();
                    Intent intent = new Intent(requireActivity().getApplicationContext(), Login.class);
                    startActivity(intent);
                    requireActivity().finish();
                });
        return rootView;
    }

    private void setProfilePictureImageView(String uid) {
        ImageView profilePicture = rootView.findViewById(R.id.current_profile_picture);
        DocumentReference userDoc = db.collection("users")
                                      .document(uid);
        userDoc.get()
               .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       String profilePic = task.getResult()
                                               .getString("profilePicture");
                       if (profilePic != null) {
                           profilePicture.setImageTintList(null);
                           Picasso.get()
                                  .load(profilePic)
                                  .into(profilePicture);
                       }
                   }
               });
    }

    private void getUsername(View view, FirebaseFirestore db, String uid) {
        DocumentReference userDoc = db.collection("users")
                                      .document(uid);
        TextView usernameTv = view.findViewById(R.id.current_username_text);
        userDoc.get()
               .addOnCompleteListener(task -> {
                   String username = task.getResult()
                                         .getString("username");
                   usernameTv.setText(String.format(getString(R.string.current_username), username));
               });
    }

    private void usernameAvailable(String username, OnCompleteCallback callback) {
        db.collection("users")
          .whereEqualTo("username", username)
          .get()
          .addOnCompleteListener(task -> callback.onCompleteUsernameAvailable(task.getResult()
                                                                                  .isEmpty()));
    }

    private void makeToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT)
             .show();
    }

    public interface OnCompleteCallback {
        void onCompleteUsernameAvailable(boolean available);
    }
}