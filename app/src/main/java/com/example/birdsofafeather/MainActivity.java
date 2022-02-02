package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** begin profile setup: goes to SetNameActivity*/
    public void onSetupProfileClicked(View view) {
        Intent intent = new Intent(this, SetNameActivity.class);
        startActivity(intent);
    }
}