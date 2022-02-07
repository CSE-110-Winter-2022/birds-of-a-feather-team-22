package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CourseActivity extends AppCompatActivity {
    private Spinner year_spinner;
    private Spinner quarter_spinner;
    private TextView subject_view;
    private TextView number_view;
    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Profile> future;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        year_spinner = findViewById(R.id.year_spinner);
        quarter_spinner = findViewById(R.id.quarter_spinner);
        subject_view = findViewById(R.id.subject_view);
        number_view = findViewById(R.id.number_view);
        db = AppDatabase.singleton(this);

        ArrayAdapter<CharSequence> year_adapter = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> quarter_adapter = ArrayAdapter.createFromResource(this, R.array.quarter_array, android.R.layout.simple_spinner_dropdown_item);

        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quarter_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        year_spinner.setAdapter(year_adapter);
        quarter_spinner.setAdapter(quarter_adapter);

        String year = getIntent().getStringExtra("year");
        String quarter = getIntent().getStringExtra("quarter");
        String subject = getIntent().getStringExtra("subject");
        String number = getIntent().getStringExtra("number");

        for (int i = 0; i < year_spinner.getCount(); i++) {
            if (year_spinner.getItemAtPosition(i).equals(year)) {
                year_spinner.setSelection(i);
            }
        }

        for (int i = 0; i < quarter_spinner.getCount(); i++) {
            if (quarter_spinner.getItemAtPosition(i).equals(quarter)) {
                quarter_spinner.setSelection(i);
            }
        }

        subject_view.setText(subject);
        number_view.setText(number);
    }

    public void onEnterClicked(View view) {
        String year = year_spinner.getSelectedItem().toString();
        String quarter = quarter_spinner.getSelectedItem().toString();
        String subject = subject_view.getText().toString();
        String number = number_view.getText().toString();

//        this.future = backgroundThreadExecutor.submit(() -> {
            int courseId = db.courseDao().getCourseId(1, Utilities.formatString(year), Utilities.formatString(quarter), Utilities.formatString(subject), Utilities.formatString(number));
            if (year.length() > 0 && quarter.length() > 0 && subject.length() > 0 && number.length() > 0 && courseId == 0) {
                Course course = new Course(1, Utilities.formatString(year), Utilities.formatString(quarter), Utilities.formatString(subject), Utilities.formatString(number));
                db.courseDao().insert(course);
            }
//            return null;
//        });

        for (int i = 0; i < year_spinner.getCount(); i++) {
            if (year_spinner.getItemAtPosition(i).equals(year)) {
                year_spinner.setSelection(i);
            }
        }

        for (int i = 0; i < quarter_spinner.getCount(); i++) {
            if (quarter_spinner.getItemAtPosition(i).equals(quarter)) {
                quarter_spinner.setSelection(i);
            }
        }

        subject_view.setText(subject);
        number_view.setText(number);
    }

    public void onDoneClicked(View view) {
        String name = getIntent().getStringExtra("name");
        String photo = getIntent().getStringExtra("photo");

//        this.future = backgroundThreadExecutor.submit(() -> {
            Profile userProfile = new Profile(1, name, photo);
            db.profileDao().insert(userProfile);

//            return null;
//        });

//        this.future.cancel(true);

        finish();
    }

    @Override
    public void onBackPressed() {
        if (false) {
            super.onBackPressed();
        } else {
        }
    }
}