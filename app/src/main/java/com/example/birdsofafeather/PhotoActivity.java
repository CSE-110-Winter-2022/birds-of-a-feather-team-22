package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

// Refers to the screen where the user can enter and submit their profile photo URL
public class PhotoActivity extends AppCompatActivity {

    private TextView photo_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        this.photo_view = findViewById(R.id.photo_view);

        this.setTitle("Setup: Add Photo");
    }

    // When the submit button is clicked
    public void onSubmitClicked(View view) {
        Log.d("<Photo>", "Submit photo");
        // Retrieve name from previous activity and photo URL and pass along to the add courses activity
        String photo = this.photo_view.getText().toString().trim();
        String name = getIntent().getStringExtra("name");

        Intent intent = new Intent(this, CourseActivity.class);

        intent.putExtra("photo", photo);
        intent.putExtra("name", name);

        startActivity(intent);
        finish();
    }

    // Overrides back button to clearing all fields
    @Override
    public void onBackPressed() {
        clearFields();
    }

    private void clearFields() {
        this.photo_view.setText("");
    }
}