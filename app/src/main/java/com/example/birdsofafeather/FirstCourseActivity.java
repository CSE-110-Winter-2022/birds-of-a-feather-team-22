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

        this.year_spinner = findViewById(R.id.year_spinner);
        this.quarter_spinner = findViewById(R.id.quarter_spinner);
        this.subject_view = findViewById(R.id.subject_view);
        this.number_view = findViewById(R.id.number_view);
        this.db = AppDatabase.singleton(this);

        ArrayAdapter<CharSequence> year_adapter = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> quarter_adapter = ArrayAdapter.createFromResource(this, R.array.quarter_array, android.R.layout.simple_spinner_dropdown_item);

        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quarter_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.year_spinner.setAdapter(year_adapter);
        this.quarter_spinner.setAdapter(quarter_adapter);
    }

    public void onEnterClicked(View view) {
        String year = this.year_spinner.getSelectedItem().toString().trim();
        String quarter = this.quarter_spinner.getSelectedItem().toString().trim();
        String subject = this.subject_view.getText().toString().trim().toUpperCase();
        String number = this.number_view.getText().toString().trim();


        if (isValidCourse(year, quarter, subject, number)) {
            String name = getIntent().getStringExtra("name");
            String photo = getIntent().getStringExtra("photo");

            Context context = view.getContext();
            Intent intent = new Intent(context, CourseActivity.class);

            this.future = this.backgroundThreadExecutor.submit(() -> {
                Course course = new Course(1, year, quarter, subject, number);
                this.db.courseDao().insert(course);
                Profile userProfile = new Profile(1, name, photo);
                this.db.profileDao().insert(userProfile);
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

        if (subject.trim().length() <= 0) {
            Utilities.showError(this, "Error: Invalid Input", "Please enter a valid subject for your course.");
            return false;
        }

        else {
            for (char c : subject.trim().toCharArray()) {
                if (!Character.isLetter(c)) {
                    Utilities.showError(this, "Error: Invalid Input", "Please enter a valid subject for your course.");
                    return false;
                }
            }
        }

        if (number.trim().length() <= 0 ) {
            Utilities.showError(this, "Error: Invalid Input", "Please enter a valid number for your course.");
            return false;
        }
        else {
            for (char c : number.trim().toCharArray()) {
                if (!Character.isDigit(c)) {
                    Utilities.showError(this, "Error: Invalid Input", "Please enter a valid number for your course.");
                    return false;
                }
            }
        }

        if (quarter.equals("Quarter")) {
            Utilities.showError(this, "Error: Invalid Selection", "Please select a quarter for your course.");
            return false;
        }

        if (year.equals("Year")) {
            Utilities.showError(this, "Error: Invalid Selection", "Please select a year for your course.");
            return false;
        }

        return true;
    }

    public void clearFields() {
        this.year_spinner.setSelection(0);
        this.quarter_spinner.setSelection(0);
        this.subject_view.setText("");
        this.number_view.setText("");
    }

    @Override
    public void onBackPressed() {
        clearFields();
    }
}