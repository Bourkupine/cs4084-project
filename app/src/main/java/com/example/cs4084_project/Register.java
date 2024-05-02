package com.example.cs4084_project;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextUsername, editTextPassword, editTextConfirmPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView goToLogin;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // temporary dark theme disable
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.confirm_password);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        goToLogin = findViewById(R.id.goToLogin);

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, username, password, confirmPassword;
                email = String.valueOf(editTextEmail.getText());
                username = String.valueOf(editTextUsername.getText());
                password = String.valueOf(editTextPassword.getText());
                confirmPassword = String.valueOf(editTextConfirmPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Please enter an email address", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!validateEmail(email)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(username)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Please enter a first name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!validateName(username)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Please enter a valid username between 2 and 32 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!validatePassword(password)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Passwords have at least 8 characters including a number, lowercase letter, and uppercase letter", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.signInWithEmailAndPassword(email, password);
                            String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> user = new HashMap<>();
                            user.put("uid", uid);
                            user.put("email", email);
                            user.put("username", username);
                            db.collection("users").document(uid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Register.this, "Account registered successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Register.this, "Failed to create account, please try again", Toast.LENGTH_SHORT).show();
                                    Log.w(TAG, "Failed to create account!", e);
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Register.this, "Authentication failed, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private static boolean validateEmail(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();
    }

    private static boolean validatePassword(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private static boolean validateName(String name) {
        Pattern pattern;
        Matcher matcher;
        final String NAME_PATTERN = "^[A-Za-z '-]{2,32}$";
        pattern = Pattern.compile(NAME_PATTERN);
        matcher = pattern.matcher(name);

        return matcher.matches();
    }
}