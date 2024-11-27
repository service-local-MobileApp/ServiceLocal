package com.example.servicelocal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class home extends AppCompatActivity {
    private SearchView searchBar;
    private RecyclerView recyclerView;
    private Button profileButton;
    private Button filterButton, addPostButton;
    private List<String> items; // Simulated data source
    private ItemsAdapter adapter; // Adapter for RecyclerView
    private DatabaseReference database;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        database = FirebaseDatabase.getInstance().getReference("posts");

        // Initialize UI elements
        searchBar = findViewById(R.id.searchBar);
        recyclerView = findViewById(R.id.recyclerView);
        profileButton = findViewById(R.id.btn_profile);
        filterButton = findViewById(R.id.btn_filter);
        // Initialize the "Add Post" button
        addPostButton = findViewById(R.id.add);
        addPostButton.setOnClickListener(v -> openAddPostDialog());

        // Configure RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        items = getSampleData();
        adapter = new ItemsAdapter(items);
        recyclerView.setAdapter(adapter);


        // Search bar functionality
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterResults(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterResults(newText);
                return true;
            }
        });

        // Profile button click event
        profileButton.setOnClickListener(v -> {
            Intent profileIntent = new Intent(home.this, Profile.class);
            startActivity(profileIntent);
        });

        // Filter button functionality
        setupFilterButton();
    }

    // Sample data for RecyclerView
    private List<String> getSampleData() {
        List<String> data = new ArrayList<>();
        data.add("Service A");
        data.add("Service B");
        data.add("Service C");
        data.add("Service D");
        return data;
    }

    // Setup filter button click functionality
    private void setupFilterButton() {
        filterButton.setOnClickListener(v -> {
            // Inflate the filter layout
            View filterView = getLayoutInflater().inflate(R.layout.filter, null);

            // Build a dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(filterView);

            AlertDialog dialog = builder.create();

            // Set functionality for Apply Filter button inside popup
            Button applyFilterButton = filterView.findViewById(R.id.apply_filter);
            applyFilterButton.setOnClickListener(view -> {
                EditText locationInput = filterView.findViewById(R.id.filter_location);
                EditText priceInput = filterView.findViewById(R.id.filter_price);

                String location = locationInput.getText().toString();
                String price = priceInput.getText().toString();

                // Use the filter data (send it to your adapter or perform filtering logic)
                filterItems(location, price);

                // Close the dialog
                dialog.dismiss();
            });

            // Show the dialog
            dialog.show();
        });
    }

    // Filter items based on location and price
    private void filterItems(String location, String price) {
        List<String> filteredList = new ArrayList<>();
        for (String item : items) {
            // Example filtering logic (customize as needed)
            if (item.toLowerCase().contains(location.toLowerCase())) {
                filteredList.add(item); // Add only items matching location
            }
        }

        // Update RecyclerView with filtered data
        adapter.updateData(filteredList);
    }

    // Filter results from search bar
    private void filterResults(String query) {
        List<String> filteredList = new ArrayList<>();
        for (String item : items) {
            if (item.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateData(filteredList);
    }

    private void openAddPostDialog() {
        // Inflate the add_post layout
        View view = LayoutInflater.from(this).inflate(R.layout.addpost, null);
        EditText postTitle = view.findViewById(R.id.post_title);
        EditText postContent = view.findViewById(R.id.post_content);
        Button submitPostButton = view.findViewById(R.id.submit_post);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle the "Submit" button click
        submitPostButton.setOnClickListener(v -> {
            String title = postTitle.getText().toString().trim();
            String content = postContent.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            } else {
                savePostToDatabase(title, content);
                dialog.dismiss();
            }
        });
    }

    private void savePostToDatabase(String title, String content) {
        // Create a unique ID for the post
        String postId = database.push().getKey();

        if (postId != null) {
            Post post = new Post(postId, title, content);
            database.child(postId).setValue(post)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Post added successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to add post.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Post class to represent post structure
    public static class Post {
        public String postId;
        public String title;
        public String content;

        public Post(String postId, String title, String content) {
            this.postId = postId;
            this.title = title;
            this.content = content;
        }
    }
}
