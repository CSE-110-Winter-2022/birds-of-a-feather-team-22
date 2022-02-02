package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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

        //testing
        ProgressBar progressBar = findViewById(R.id.progressBar2);

        ValueAnimator animator = ValueAnimator.ofInt(0, progressBar.getMax());
        animator.setDuration(1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation){
                progressBar.setProgress((Integer)animation.getAnimatedValue());
            }
        });

        animator.start();
    }


    public void onNextClicked(View view){
        Intent intent = new Intent(this, SetCoursesActivity.class);

        //testing
        ProgressBar progressBar = findViewById(R.id.progressBar2);

        ValueAnimator animator = ValueAnimator.ofInt(0, progressBar.getMax());
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation){
                progressBar.setProgress((Integer)animation.getAnimatedValue());
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                startActivity(intent);

            }
        });
        animator.start();


    }

    public void onURLClearClicked(View view){
        TextView urlTextView = findViewById(R.id.url_textview);
        urlTextView.setText("");
    }
}