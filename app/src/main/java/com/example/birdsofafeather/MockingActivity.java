/*
 * This file is capable of allowing the user to being able to mock the functionality of recieving and
 * interacting with matches.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

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
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class refers to the demo and testing screen where the nearby functionality can be mocked and
 * the Database can be cleared.
 */
public class MockingActivity extends AppCompatActivity {
    // Log tag
    private final String TAG = "<Mocking>";

    // DB field
    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<List<String>> f1;
    private Future<Profile> f2;
    private Future<List<Course>> f3;

    // View fields
    private TextView selfProfileIdView;
    private EditText textBox;

    // Self and Session information
    private Profile selfProfile;
    private List<Course> selfCourses;
    private String sessionId;

    // Nearby and Messages fields
    private ArrayList<String> mockedMessages;

    // Nearby fields
    private BoFMessagesClient messagesClient;
    private BoFMessageListener messageListener;

    /**
     * Initializes the screen and activity for MockingActivity.
     *
     * @param savedInstanceState A bundle that contains information regarding layout and data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Setting up mocking screen...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mocking);

        // field initializations
        this.db = AppDatabase.singleton(this);
        this.mockedMessages = getIntent().getStringArrayListExtra("mocked_messages");
        if (this.mockedMessages == null) {
            this.mockedMessages = new ArrayList<>();
        }
        this.sessionId = getIntent().getStringExtra("session_id");

        // Get self profile
        this.f2 = this.backgroundThreadExecutor.submit(() -> this.db.profileDao().getSelfProfile(true));
        try {
            this.selfProfile = this.f2.get();
            Log.d(TAG, "Self profile retrieved!");
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving self profile!");
            e.printStackTrace();
        }

        // Get list of all self courses
        this.f3 = this.backgroundThreadExecutor.submit(() -> this.db.courseDao().getCoursesByProfileId(this.selfProfile.getProfileId()));
        try {
            this.selfCourses = this.f3.get();
            Log.d(TAG, "Self courses retrieved!");
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving self courses!");
            e.printStackTrace();
        }

        // Set self profile id view to show the profile id of self
        String selfProfileId = this.selfProfile.getProfileId();
        this.selfProfileIdView = findViewById(R.id.self_profile_id_view);
        this.selfProfileIdView.setText(selfProfileId);

        this.textBox = findViewById(R.id.mocking_input_view);

        this.messageListener = new BoFMessageListener(this.sessionId, this);
        this.messagesClient = new BoFMessagesClient(Nearby.getMessagesClient(this));
        this.messagesClient.subscribe(this.messageListener);
    }

    /**
     * Destroys MockingActivity.
     */
    @Override
    protected void onDestroy() {
        this.messagesClient.unsubscribe(this.messageListener);
        super.onDestroy();
        Log.d(TAG, "MockingActivity destroyed!");
    }

    /**
     * On click method when the user clicks the enter button.
     * @param view The enter button
     */
    public void onEnterClicked(View view) {
        // Get text box input and add it to the list of mocked Nearby messages
        String input = this.textBox.getText().toString();
        this.mockedMessages.add(input);

        Log.d(TAG, "Published mocked message: " + input);

        // Copy message to clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("last message", input);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Copied message to clipboard!", Toast.LENGTH_SHORT).show();

        // clear text box
        textBox.setText("");
    }

    /**
     * On click method when the user clicks the self profile id textview.
     *
     * @param view The self profile id textview.
     */
    public void onSelfProfileIdClicked(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("self profile id", this.selfProfileIdView.getText());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Copied self profile id to clipboard!", Toast.LENGTH_SHORT).show();
    }

    /**
     * On click method when the user clicks the clear DB button.
     *
     * @param view The clear DB button.
     */
    public void onClearDBClicked(View view) {
        this.db.clearAllTables();
        Log.d(TAG, "DB cleared!");
    }

    /**
     * Override the back button to return to the previous session in MatchActivity.
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "Returning to previous session...");
        Intent intent = new Intent(this, MatchActivity.class);

        // Pass the list of mocked messages back so they can be discovered
        intent.putStringArrayListExtra("mocked_messages", this.mockedMessages);
        startActivity(intent);
        finish();
    }
}