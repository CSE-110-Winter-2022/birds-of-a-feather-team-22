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
    private ArrayList<String> mockedMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Setting up mocking screen...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mocking);

        this.db = AppDatabase.singleton(this);
        this.mockedMessages = new ArrayList<>();
    }

    public void onEnterClicked(View view) {
        EditText textBox = findViewById(R.id.mocking_input_view);
        this.mockedMessages.add(textBox.getText().toString());

        Log.d(TAG, "Published mocked message: " + textBox.getText().toString());
        textBox.setText("");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Returning to previous session...");
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putStringArrayListExtra("mocked_messages", this.mockedMessages);
        startActivity(intent);
    }

    // For testing purposes, visibility is set to gone for demoing and actual use
    public void onClearDBClicked(View view) {
        this.db.clearAllTables();
        Log.d(TAG, "DB cleared!");
    }
}