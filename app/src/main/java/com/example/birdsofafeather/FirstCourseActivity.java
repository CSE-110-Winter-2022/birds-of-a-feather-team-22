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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FirstCourseActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_first_course);

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
    }

    public void onEnterClicked(View view) {
        String year = year_spinner.getSelectedItem().toString();
        String quarter = quarter_spinner.getSelectedItem().toString();
        String subject = subject_view.getText().toString();
        String number = number_view.getText().toString();


        if (isValidCourse(year, quarter, subject, number)) {
            String name = getIntent().getStringExtra("name");
            String photo = getIntent().getStringExtra("photo");

            Context context = view.getContext();
            Intent intent = new Intent(context, CourseActivity.class);

            this.future = backgroundThreadExecutor.submit(() -> {
                Course course = new Course(1, Utilities.formatString(year), Utilities.formatString(quarter), Utilities.formatString(subject), Utilities.formatString(number));
                db.courseDao().insert(course);
                Profile userProfile = new Profile(1, name, photo);
                db.profileDao().insert(userProfile);
                runOnUiThread(() -> {
//                    intent.putExtra("name", name);
//                    intent.putExtra("photo", photo);
                    intent.putExtra("year", year);
                    intent.putExtra("quarter", quarter);
                    intent.putExtra("subject", subject);
                    intent.putExtra("number", number);
                    context.startActivity(intent);
                    finish();
                });
                return null;
            });
        }
    }

    public boolean isValidCourse(String year, String quarter, String subject, String number) {
        return year.length() > 0 && quarter.length() > 0 && subject.length() > 0 && number.length() > 0;
    }

    @Override
    public void onBackPressed() {
        if (false) {
            super.onBackPressed();
        } else {
        }
    }
}