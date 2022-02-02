package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BluetoothActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
    }
    public void onClickStart(View view) {
        Button stopButton = findViewById(R.id.stopButton);
        TextView findMatches = findViewById(R.id.textView6);

        stopButton.setVisibility(View.VISIBLE);
        findMatches.setVisibility(View.VISIBLE);
        findViewById(R.id.startButton).setVisibility(View.INVISIBLE);
    }

    public void onClickStop(View view) {

    }
}