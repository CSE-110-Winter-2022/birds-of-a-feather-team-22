package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SetNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);



    }

    public void onNextClicked(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, SetPhotoActivity.class);

        TextView name_view = findViewById(R.id.editTextTextPersonName);

        String name = name_view.getText().toString();


        if (name.length() > 0) {
            intent.putExtra("name", name);
            context.startActivity(intent);
        }

    }


}