package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ProgressBar;

public class BluetoothActivity extends AppCompatActivity {
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        setTitle("Your Birds of a Feather...");
    }
    public void onClickStart(View view) {

        Button stopButton = findViewById(R.id.stopButton);
        TextView findMatches = findViewById(R.id.textView6);
        Button startButton = findViewById(R.id.startButton);

        findMatches.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);
    }

    public void onClickStop(View view) {
        Button stopButton = findViewById(R.id.stopButton);
        TextView findMatches = findViewById(R.id.textView6);
        TextView matchesFound = findViewById(R.id.matchesFound);
        LinearLayout dummy2 = findViewById(R.id.dummy2);

        stopButton.setVisibility(View.GONE);
        findMatches.setVisibility(View.GONE);
        matchesFound.setVisibility(View.VISIBLE);
        dummy2.setVisibility(View.VISIBLE);

    }
}