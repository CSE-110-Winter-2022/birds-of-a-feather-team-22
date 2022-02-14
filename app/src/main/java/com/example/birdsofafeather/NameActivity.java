package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

// Refers to the screen where the user can enter and confirm their name
public class NameActivity extends AppCompatActivity {

    private TextView name_view;
    private AlertDialog mostRecentDialog = null;
    private String autofillName = "John";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        this.name_view = findViewById(R.id.name_view);
        this.name_view.setText(this.autofillName);
        this.setTitle("Setup: Add Name");
    }

    // When the confirm button is clicked
    public void onConfirmClicked(View view) {
        String name = this.name_view.getText().toString().trim();

        // Check if name is valid
        if (isValidName(name)) {
            Context context = view.getContext();
            Intent intent = new Intent(context, PhotoActivity.class);

            // Pass on name to the set profile photo activity
            intent.putExtra("name", name);
            context.startActivity(intent);
            finish();
        }
    }

    // Checks if a name is valid, otherwise, shows an error
    public boolean isValidName(String name) {
        if (name.length() <= 0) {
            mostRecentDialog = Utilities.showError(this, "Error: Invalid Input", "Please enter a valid name for your profile.");
            return false;
        }

        return true;
    }

    // Overrides back button to clearing all fields
    @Override
    public void onBackPressed() {
        clearFields();
    }

    private void clearFields() {
        this.name_view.setText("");
    }
}