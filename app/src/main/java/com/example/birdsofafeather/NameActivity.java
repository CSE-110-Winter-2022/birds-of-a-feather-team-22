package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Profile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Refers to the screen where the user can enter and confirm their name
public class NameActivity extends AppCompatActivity {
    private Future<Void> f1;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;

    private TextView name_view;
    private AlertDialog mostRecentDialog = null;
    private String autofillName = "John";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.db = AppDatabase.singleton(this);

        this.f1 = this.backgroundThreadExecutor.submit(() -> {
            System.out.println(db.profileDao().count());
            Profile user = this.db.profileDao().getUserProfile(true);
            System.out.println(user);
            if (user != null) {
                Log.d("<Home>", "User profile created");
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, HomeScreenActivity.class);
                    startActivity(intent);
                });
            }
            Log.d("<Home>", "User profile not created");
            return null;
        });

        setContentView(R.layout.activity_name);
        this.name_view = findViewById(R.id.name_view);
        this.name_view.setText(this.autofillName);
        this.setTitle("Setup: Add Name");
    }

    // When the confirm button is clicked
    public void onConfirmClicked(View view) {
        Log.d("<Name>", "Confirm Name");
        String name = this.name_view.getText().toString().trim();

        // Check if name is valid
        if (isValidName(name)) {
            Intent intent = new Intent(this, PhotoActivity.class);

            // Pass on name to the set profile photo activity
            intent.putExtra("name", name);
            startActivity(intent);
            finish();
        }
        else Log.e("<Name>", "Name is not valid");
    }

    // Checks if a name is valid, otherwise, shows an error
    public boolean isValidName(String name) {
        if (name.length() <= 0) {
            mostRecentDialog = Utilities.showError(this, "Error: Invalid Input", "Please enter a valid name for your profile.");
            return false;
        }

        return true;
    }

    // Overrides back button to clearing all fields
    @Override
    public void onBackPressed() {
        clearFields();
    }

    private void clearFields() {
        this.name_view.setText("");
    }
}