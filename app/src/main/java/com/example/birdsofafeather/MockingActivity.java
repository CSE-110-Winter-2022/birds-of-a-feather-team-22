package com.example.birdsofafeather;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.birdsofafeather.db.AppDatabase;

import java.util.ArrayList;


public class MockingActivity extends AppCompatActivity {
    private final String TAG = "<Mocking>";

    private AppDatabase db;
    private ArrayList<String> mockedMessages;
    private TextView selfProfileIdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Setting up mocking screen...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mocking);

        this.db = AppDatabase.singleton(this);
        this.mockedMessages = getIntent().getStringArrayListExtra("mocked_messages");

        String selfProfileId = getIntent().getStringExtra("self_profile_id");
        this.selfProfileIdView = findViewById(R.id.self_profile_id_view);
        this.selfProfileIdView.setText(selfProfileId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MockingActivity destroyed!");
    }

    public void onEnterClicked(View view) {
        EditText textBox = findViewById(R.id.mocking_input_view);
        String input = textBox.getText().toString();
        this.mockedMessages.add(input);

        Log.d(TAG, "Published mocked message: " + input);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("last message", input);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Copied message to clipboard!", Toast.LENGTH_SHORT).show();

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

    // For mocking purposes, copies self profile id to clipboard
    public void onSelfProfileIdClicked(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("self profile id", this.selfProfileIdView.getText());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Copied self profile id to clipboard!", Toast.LENGTH_SHORT).show();
    }
}