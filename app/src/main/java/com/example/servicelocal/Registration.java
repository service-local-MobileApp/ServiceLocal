package com.example.servicelocal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {
    RadioGroup radioGroup;
    Button loginButton;
    Button signUpButton;
    EditText usernameEditText;
    EditText passwordEditText;

    private DatabaseReference database; // Firebase Database Reference

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference();

        radioGroup = findViewById(R.id.radio);
        loginButton = findViewById(R.id.btn_login);
        signUpButton = findViewById(R.id.btn_sign);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);

        loginButton.setOnClickListener(this::onLoginClicked);
        signUpButton.setOnClickListener(this::onSignUpClicked);
    }

    private void onLoginClicked(View view) {
        // Navigate to the login screen
        Intent loginIntent = new Intent(Registration.this, Login.class);
        startActivity(loginIntent);
    }

    private void onSignUpClicked(View view) {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedRoleId = radioGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine role from RadioButton
        RadioButton selectedRole = findViewById(selectedRoleId);
        String role = selectedRole.getText().toString();

        // Save user data to Firebase
        saveUserData(username, password, role);
    }

    private void saveUserData(String username, String password, String role) {
        // Create a unique key for the user
        String userId = database.child("users").push().getKey();

        if (userId != null) {
            // Create a User object
            User user = new User(username, password, role);

            // Save the user data in the "users" node in Firebase
            database.child("users").child(userId).setValue(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();

                            // Redirect to login screen
                            Intent loginIntent = new Intent(Registration.this, Login.class);
                            startActivity(loginIntent);
                        } else {
                            Toast.makeText(this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Error generating user ID", Toast.LENGTH_SHORT).show();
        }
    }

    // User class for Firebase
    public static class User {
        public String username;
        public String password; // Not recommended to store plain text passwords in production
        public String role;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }
    }
}
