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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        this.name_view = findViewById(R.id.name_view);

        this.setTitle("Setup: Add Name");
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
            Utilities.showError(this, "Error: Invalid Input", "Please enter a valid name for your profile.");
            return false;
        }

        return true;
    }
}