package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PhotoActivity extends AppCompatActivity {

    private TextView photo_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        this.photo_view = findViewById(R.id.photo_view);
    }

    public void onSubmitClicked(View view) {

        String photo = this.photo_view.getText().toString().trim();

        if (isValidPhoto(photo)) {
            String name = getIntent().getStringExtra("name");

            Context context = view.getContext();
            Intent intent = new Intent(context, FirstCourseActivity.class);

            intent.putExtra("photo", photo);
            intent.putExtra("name", name);

            context.startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        clearFields();
    }

    private void clearFields() {
        this.photo_view.setText("");
    }

    // TODO: Check if photo provided is valid photo, otherwise use placeholder photo
    public boolean isValidPhoto(String photo) {
        if (photo.length() <= 0) {
            Utilities.showError(this, "Error: Invalid Input", "Please enter a valid photo URL for your profile.");
            return false;
        }

        return true;
    }
}