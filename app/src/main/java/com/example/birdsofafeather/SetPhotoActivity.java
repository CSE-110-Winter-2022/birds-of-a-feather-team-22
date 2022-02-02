package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class SetPhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_photo);
    }

    public void onNextClicked(View view){
        Intent intent = new Intent(this, SetCoursesActivity.class);
        startActivity(intent);

    }

    public void onURLClearClicked(View view){
        TextView urlTextView = findViewById(R.id.url_textview);
        urlTextView.setText("");
    }
}