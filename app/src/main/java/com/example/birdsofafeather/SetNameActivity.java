package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class SetNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);

        setTitle("What's Your Name?");

    }

    public void onNextClicked(View view) {
        Intent intent = new Intent(this, SetPhotoActivity.class);
        startActivity(intent);
    }


}