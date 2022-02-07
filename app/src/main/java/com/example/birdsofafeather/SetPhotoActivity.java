package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SetPhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_photo);

<<<<<<< HEAD
=======
        setTitle("Set a Profile Picture");
>>>>>>> 24ebf21a91d5dfa243f89f1bace7ef76c6eb8c7d

    }


    public void onNextClicked(View view){
<<<<<<< HEAD
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


=======
        Intent intent = new Intent(this, SetCoursesActivity.class);
        startActivity(intent);
>>>>>>> 24ebf21a91d5dfa243f89f1bace7ef76c6eb8c7d


    }

}