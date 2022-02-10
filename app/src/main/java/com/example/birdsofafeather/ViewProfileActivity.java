package com.example.birdsofafeather;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
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


        stopButton.setVisibility(View.GONE);
        findMatches.setVisibility(View.GONE);
        matchesFound.setVisibility(View.VISIBLE);


    }
}