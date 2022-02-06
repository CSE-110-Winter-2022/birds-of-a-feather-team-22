package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class NameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
    }

    public void onConfirmClicked(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, PhotoActivity.class);

        TextView name_view = findViewById(R.id.name_view);

        String name = name_view.getText().toString();


        if (name.length() > 0) {
            intent.putExtra("name", name);
            context.startActivity(intent);
        }
    }
}