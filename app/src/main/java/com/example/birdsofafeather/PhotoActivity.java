/*
 * This file is capable of taking in a photo URL to allow for the creation of the self user profile.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Refers to the screen where the user can enter and submit their profile photo URL.
 */
public class PhotoActivity extends AppCompatActivity {
    // Log tag
    private final String TAG = "<Photo>";

    // UI/View fields
    private TextView photo_view;

    /**
     * Initializes the activity and screen for PhotoActivity.
     *
     * @param savedInstanceState A bundle that contains information regarding layout and data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        this.setTitle("Setup: Add Photo");

        Log.d(TAG, "Setting up Photo Screen");

        // View initialization
        this.photo_view = findViewById(R.id.photo_view);
    }

    /**
     * Destroys PhotoActivity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "PhotoActivity Destroyed!");
    }

    /**
     * On click method for when the user clicks on the submit button.
     *
     * @param view The submit button.
     */
    public void onSubmitClicked(View view) {
        Log.d(TAG, "Submit button pressed");
        Log.d(TAG, "Saving photo");

        // Retrieve name from previous activity and photo URL
        String photo = this.photo_view.getText().toString().trim();
        String name = getIntent().getStringExtra("name");

        Intent intent = new Intent(this, CourseActivity.class);

        // Pass on the name and photo to CourseActivity
        intent.putExtra("photo", photo);
        intent.putExtra("name", name);

        startActivity(intent);
        finish();
    }

    /**
     * Overrides the back button to clear all fields.
     */
    @Override
    public void onBackPressed() {
        clearFields();
    }

    /**
     * Clears the photo URL input field.
     */
    private void clearFields() {
        Log.d(TAG, "Back button pressed, clearing fields");
        this.photo_view.setText("");
    }
}