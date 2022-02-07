package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
    }

    public void onSubmitClicked(View view) {
        TextView photo_view = findViewById(R.id.photo_view);
        String photo = photo_view.getText().toString();

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
        if (false) {
            super.onBackPressed();
        } else {
        }
    }

    // TODO: Check if photo provided is valid photo, otherwise use placeholder photo
    public boolean isValidPhoto(String photo) {
        return photo.length() > 0;
    }
}