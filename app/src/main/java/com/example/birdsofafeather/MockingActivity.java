package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.birdsofafeather.db.AppDatabase;

import java.util.ArrayList;


public class MockingActivity extends AppCompatActivity {
    private final String TAG = "<Mocking>";

    private AppDatabase db;
    private String sessionId;
    private ArrayList<String> mockedMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Setting up mocking screen...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mocking);

        this.db = AppDatabase.singleton(this);
        this.sessionId = getIntent().getExtras().getString("session_id");
        this.mockedMessages = new ArrayList<>();
    }

    public void onEnterClicked(View view) {
        EditText textBox = findViewById(R.id.mocking_input_view);
        this.mockedMessages.add(textBox.getText().toString());

        Log.d(TAG, "Mocked message published!");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Returning to previous session...");
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra("session_id", this.sessionId);
        intent.putStringArrayListExtra("mocked_messages", this.mockedMessages);
        startActivity(intent);
    }

    // For testing purposes, visibility is set to gone for demoing and actual use
    public void onDeleteDBClicked(View view) {
        this.db.clearAllTables();
        Log.d(TAG, "DB deleted!");
    }
}