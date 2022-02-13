package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CourseActivity extends AppCompatActivity {
    private Spinner year_spinner;
    private Spinner quarter_spinner;
    private TextView subject_view;
    private TextView number_view;
    private Button done_button;
    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Profile> future;
    private int numCourses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        this.year_spinner = findViewById(R.id.year_spinner);
        this.quarter_spinner = findViewById(R.id.quarter_spinner);
        this.subject_view = findViewById(R.id.subject_view);
        this.number_view = findViewById(R.id.number_view);
        this.done_button = findViewById(R.id.done_button);
        this.done_button.setVisibility(View.GONE);

        this.db = AppDatabase.singleton(this);

        ArrayAdapter<CharSequence> quarter_adapter = ArrayAdapter.createFromResource(this, R.array.quarter_array, android.R.layout.simple_spinner_dropdown_item);
        quarter_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.quarter_spinner.setAdapter(quarter_adapter);

        ArrayList<String> years = new ArrayList<>();
        years.add("Year");
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = thisYear; year >= 1960; year--) {
            years.add(Integer.toString(year));
        }
        ArrayAdapter<String> year_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.year_spinner.setAdapter(year_adapter);

        this.numCourses = 0;

        this.setTitle("Setup: Add Previous Course");
    }

    public void onEnterClicked(View view) {
        String year = this.year_spinner.getSelectedItem().toString().trim();
        String quarter = this.quarter_spinner.getSelectedItem().toString().trim();
        String subject = this.subject_view.getText().toString().trim().toUpperCase();
        String number = this.number_view.getText().toString().trim();

        if (isValidCourse(year, quarter, subject, number)) {
            if (this.numCourses == 0) {
                String name = getIntent().getStringExtra("name");
                String photo = getIntent().getStringExtra("photo");
                Profile userProfile = new Profile(1, name, photo);
                this.db.profileDao().insert(userProfile);

                done_button.setVisibility(View.VISIBLE);
                this.numCourses++;
            }


            this.future = this.backgroundThreadExecutor.submit(() -> {
                int courseId = this.db.courseDao().getCourseId(1, year, quarter, subject, number);
                if (!isExistingCourse(courseId)) {
                    Course course = new Course(db.courseDao().maxId()+1,1,  year, quarter, subject, number);
                    this.db.courseDao().insert(course);
                }
                return null;
            });

            // Autofill year field for the next screen
            for (int i = 0; i < this.year_spinner.getCount(); i++) {
                if (this.year_spinner.getItemAtPosition(i).equals(year)) {
                    this.year_spinner.setSelection(i);
                }
            }

            // Autofill quarter field for the next screen
            for (int i = 0; i < this.quarter_spinner.getCount(); i++) {
                if (this.quarter_spinner.getItemAtPosition(i).equals(quarter)) {
                    this.quarter_spinner.setSelection(i);
                }
            }

            // Autofill subject and number fields for the next screen
            this.subject_view.setText(subject);
            this.number_view.setText(number);
        }


    }

    public void onDoneClicked(View view) {
        this.future.cancel(true);
        finish();
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

    public boolean isExistingCourse(int courseId) {
        return courseId != 0;
    }
}