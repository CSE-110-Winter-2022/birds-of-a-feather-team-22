package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Profile;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Refers to the screen where the user can enter and confirm their name
public class NameActivity extends AppCompatActivity {
    private final String TAG = "<Name>";
    
    // DB-related fields
    private Future<Void> f1;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;

    // UI View fields
    private TextView name_view;
    private AlertDialog mostRecentDialog = null;
    private String autofillName = "John";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        this.setTitle("Setup: Add Name");

        Log.d(TAG, "Setting up Name Screen");

        // DB-related initializations
        this.db = AppDatabase.singleton(this);

        // View initializations
        this.name_view = findViewById(R.id.name_view);

        Log.d(TAG, "Autofilling name field");

        // Autofill name View
        this.name_view.setText(this.autofillName);

        Log.d(TAG, "Checking if user profile already created");
        // Check if user profile has already been created or if one needs to be created
        this.f1 = this.backgroundThreadExecutor.submit(() -> {
            Profile user = this.db.profileDao().getUserProfile(true);
            if (user != null) {
                Log.d(TAG, "User profile already created, launching Home Screen");
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, MatchActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
            Log.d(TAG, "No user profile, setup profile now");
            return null;
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "NameActivity destroyed!");
    }

    // When the confirm button is clicked
    public void onConfirmClicked(View view) {
        Log.d(TAG, "Confirm button pressed");
        String name = this.name_view.getText().toString().trim();

        // Check if name is valid
        if (isValidName(name)) {
            Log.d(TAG, "Confirming name");
            Intent intent = new Intent(this, PhotoActivity.class);

            // Pass on name to the set profile photo activity
            intent.putExtra("name", name);
            startActivity(intent);
            finish();
        }
    }

    // Checks if a name is valid, otherwise, shows an error
    public boolean isValidName(String name) {
        Log.d(TAG, "Checking if name is valid");
        if (name.length() <= 0) {
            this.mostRecentDialog = Utilities.showError(this, "Error: Invalid Input", "Please enter a valid name for your profile.");

            Log.d(TAG, "Name is invalid!");
            return false;
        }

        Log.d(TAG, "Name is valid!");
        return true;
    }

    // Overrides back button to clearing all fields
    @Override
    public void onBackPressed() {
        clearFields();
    }

    private void clearFields() {
        Log.d(TAG, "Back button pressed, clearing fields");
        this.name_view.setText("");
    }
}