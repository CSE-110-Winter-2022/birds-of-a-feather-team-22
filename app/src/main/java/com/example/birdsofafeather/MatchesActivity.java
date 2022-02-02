package com.example.birdsofafeather;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MatchesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_matches);
    }

    public void onClickStart(View view) {
        Button stopButton = findViewById(R.id.button8);
        TextView findMatches = findViewById(R.id.textView6);

        stopButton.setVisibility(View.VISIBLE);
        findMatches.setVisibility(View.VISIBLE);
    }

    public void onClickStop(View view) {

    }
}
