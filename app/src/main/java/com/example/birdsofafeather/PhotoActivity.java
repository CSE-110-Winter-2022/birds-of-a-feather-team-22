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
        Context context = view.getContext();
        Intent intent = new Intent(context, SetCoursesActivity.class);

        TextView photo_view = findViewById(R.id.url_textview);

        String name = getIntent().getStringExtra("name");
        String url = photo_view.getText().toString();

        if (url.length() > 0) {
            intent.putExtra("photo", url);
            intent.putExtra("name", name);
            context.startActivity(intent);
        }
    }
}