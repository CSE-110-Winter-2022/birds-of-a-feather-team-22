package com.example.birdsofafeather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.birdsofafeather.db.AppDatabase;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.MessageListener;


public class Mocking extends AppCompatActivity {
    private static final String TAG = "Message";
    private MessageListener messageListener;
    private AppDatabase db;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mocking);
        this.sessionId = getIntent().getExtras().getString("session_id");
        this.db = AppDatabase.singleton(this);
    }

    public void onEnterClicked(View view) {
            EditText textBox = findViewById(R.id.inputBox);


            MessageListener realListener = new MessageListener() {
               // @Override
                public void onFound(@NonNull Message message) {
                    Log.d(TAG, "Found message: " + new String(textBox.getText().toString()));
                }

               // @Override
                public void onLost(@NonNull Message message) {
                    Log.d(TAG, "Lost sight of message: " + new String(textBox.getText().toString()));

                }
            };
            MockMessageListener listener = new MockMessageListener(realListener, textBox.getText().toString(), this.getApplicationContext(), this.sessionId);

            this.messageListener = listener;
            Nearby.getMessagesClient(this).subscribe(this.messageListener);

            listener.parseInfo(textBox.getText().toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Nearby.getMessagesClient(this).unsubscribe(this.messageListener);
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