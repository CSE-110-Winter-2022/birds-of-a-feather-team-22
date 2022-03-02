package com.example.birdsofafeather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

public class MockingActivity extends AppCompatActivity {
    private static final String TAG = "Message";
    private MessageListener messageListener;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mocking);
        setTitle("Birds of a Feather");

        Log.d("<Mocking>", "Setting up Mocking");
        this.db = AppDatabase.singleton(this);
    }

    public void onEnterClicked(View view) {
        EditText textBox = findViewById(R.id.editTextTextPersonName);

        MessageListener realListener = new MessageListener() {
            @Override
            public void onFound(@NonNull Message message) {
                Log.d(TAG, "Found message: " + new String(textBox.getText().toString()));
            }

            @Override
            public void onLost(@NonNull Message message) {
                Log.d(TAG, "Lost sight of message: " + new String(textBox.getText().toString()));

            }
        };
        MockMessageListener listener = new MockMessageListener(realListener, textBox.getText().toString(), this.getApplicationContext());
        //TODO: Adding to home screen 
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        startActivity(intent);
    }

    // For testing purposes, visibility is set to gone for demoing and actual use
    public void onDeleteDBClicked(View view) {
        this.db.clearAllTables();
    }
}
