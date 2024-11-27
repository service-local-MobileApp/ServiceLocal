package com.example.servicelocal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private TextView usernameTextView, jobTextView, locationTextView;
    private Button editProfileButton, logoutButton;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        usernameTextView = findViewById(R.id.tv_username);
        jobTextView = findViewById(R.id.tv_job);
        locationTextView = findViewById(R.id.tv_location);
        editProfileButton = findViewById(R.id.btn_edit_profile);
        logoutButton = findViewById(R.id.btn_logout);

        // Fetch user data from Firebase
        fetchUserData();

        // Edit Profile Button Action
        editProfileButton.setOnClickListener(v -> {
            // Navigate to Edit Profile activity
            Intent intent = new Intent(Profile.this, EditProfile.class);
            startActivity(intent);
        });

        // Logout Button Action
        logoutButton.setOnClickListener(v -> {
            LogoutConfirmation();
        });
    }

    /**
     * Show a confirmation dialog before logging out.
     */
    private void LogoutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log Out");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", (dialog, which) -> performLogout());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Handle the logout process.
     */
    private void performLogout() {
        // Clear user session or preferences if needed
        clearUserSession();

        // Redirect to the login screen
        Intent intent = new Intent(Profile.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);
        finish(); // Finish the current activity
    }

    /**
     * Clear user session or data.
     * This method should be adapted based on your application's session management logic.
     */
    private void clearUserSession() {
        // Example: Clear shared preferences
        getSharedPreferences("UserSession", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }

    /**
     * Fetch the latest user data from Firebase and populate the UI.
     */
    private void fetchUserData() {
        database.child("users").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        usernameTextView.setText(user.username);
                        jobTextView.setText(user.job);
                        locationTextView.setText(user.location);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Save example user data to Firebase (optional method for testing).
     */
    private void saveUserData(String username, String job, String location) {
        // Create a user object
        User user = new User(username, job, location);

        // Insert into the "users" node in Firebase
        database.child("users").push().setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Create a User class to represent the data structure
    public static class User {
        public String username;
        public String job;
        public String location;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String username, String job, String location) {
            this.username = username;
            this.job = job;
            this.location = location;
        }
    }
}
