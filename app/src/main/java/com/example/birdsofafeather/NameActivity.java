package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class NameActivity extends AppCompatActivity {

    private TextView name_view;
    private final static int MAX_NAME_LENGTH = 20;
    private AlertDialog mostRecentDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        this.name_view = findViewById(R.id.name_view);

        this.setTitle("Setup: Add Name");

        /**TESTING PURPOSES ONLY-- REMOVE ONCE FINISHED*/
        Intent intent = new Intent(this, ViewProfileActivity.class);
        startActivity(intent);
    }

    public void onConfirmClicked(View view) {
        String name = this.name_view.getText().toString().trim();

        if (isValidName(name)) {
            Context context = view.getContext();
            Intent intent = new Intent(context, PhotoActivity.class);

            intent.putExtra("name", name);
            context.startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        clearFields();
    }

    private void clearFields() {
        this.name_view.setText("");
    }

    // TODO: Check if name provided is a valid name
    public boolean isValidName(String name) {
        if (name.length() <= 0) {
            mostRecentDialog = Utilities.showError(this, "Error: Invalid Input", "Please enter a valid name for your profile.");
            return false;
        }

        if (name.length() >= MAX_NAME_LENGTH){
            mostRecentDialog = Utilities.showError(this, "Error: Invalid Input",
                    String.format("Please enter a name less than %d characters", MAX_NAME_LENGTH));
        }
        return true;
    }
}