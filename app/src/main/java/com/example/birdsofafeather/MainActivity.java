package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Profile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Birds of a Feather");
        db = AppDatabase.singleton(this);
        this.future = backgroundThreadExecutor.submit(() -> {
            Profile p = db.profileDao().getProfile(1);
            if (p == null) {
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, NameActivity.class);
                    startActivity(intent);
                });
            }

            return null;
        });

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