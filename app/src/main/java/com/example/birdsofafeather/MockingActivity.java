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

/**
 * This class refers to the demo and testing screen where the nearby functionality can be mocked and
 * the Database can be cleared.
 */
public class MockingActivity extends AppCompatActivity {
    // Log tag
    private final String TAG = "<Mocking>";

    // DB field
    private AppDatabase db;

    // View fields
    private TextView selfProfileIdView;
    private EditText textBox;

    // Mocked messages field
    private ArrayList<String> mockedMessages;

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

        // Set self profile id view to show the profile id of self
        String selfProfileId = getIntent().getStringExtra("self_profile_id");
        this.selfProfileIdView = findViewById(R.id.self_profile_id_view);
        this.selfProfileIdView.setText(selfProfileId);

        this.textBox = findViewById(R.id.mocking_input_view);
    }

    /**
     * Destroys MockingActivity.
     */
    @Override
    protected void onDestroy() {
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
    }
}