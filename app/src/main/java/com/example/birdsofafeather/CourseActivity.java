package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Refers to the screen where the user can add their previously taken courses
public class CourseActivity extends AppCompatActivity {
    private final String TAG = "<Course>";

    // DB-related fields
    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> f1;

    // UI View fields
    private Spinner year_spinner;
    private Spinner quarter_spinner;
    private Spinner class_size_spinner;
    private TextView subject_view;
    private TextView number_view;
    private Button doneButton;

    // For resuming lastSession
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        this.setTitle("Setup: Add Previous Course");

        Log.d(TAG, "Setting up Course Screen");

        // DB-related initializations
        this.db = AppDatabase.singleton(this);

        // View initializations
        this.year_spinner = findViewById(R.id.year_spinner);
        this.quarter_spinner = findViewById(R.id.quarter_spinner);
        this.class_size_spinner = findViewById(R.id.class_size_spinner);
        this.subject_view = findViewById(R.id.subject_view);
        this.number_view = findViewById(R.id.number_view);
        this.doneButton = findViewById(R.id.done_button);

        this.doneButton.setVisibility(View.GONE);

        // Set quarter spinner
        List<String> quarters = new ArrayList<>(Arrays.asList("Quarter", "Fall", "Winter", "Spring", "Summer Session 1", "Summer Session 2", "Special Summer Session"));
        ArrayAdapter<String> quarter_adapter = new ArrayAdapter<>(this, R.layout.spinner_item_text, quarters);
        quarter_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.quarter_spinner.setAdapter(quarter_adapter);

        // Set dynamic year spinner
        List<String> years = new ArrayList<>();
        years.add("Year");
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = thisYear; y >= 1960; y--) {
            years.add(Integer.toString(y));
        }
        ArrayAdapter<String> year_adapter = new ArrayAdapter<>(this, R.layout.spinner_item_text, years);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.year_spinner.setAdapter(year_adapter);

        // Set class size spinner
        List<String> classSizes = new ArrayList<>(Arrays.asList("Class Size", "Tiny (<40)", "Small (40-75)", "Medium (75-150)", "Large (150-250)", "Huge (250-400)", "Gigantic (400+)"));
        ArrayAdapter<String> class_size_adapter = new ArrayAdapter<>(this, R.layout.spinner_item_text, classSizes);
        class_size_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.class_size_spinner.setAdapter(class_size_adapter);

        // Check if user is going back to add more courses, and change UI accordingly
        this.f1 = this.backgroundThreadExecutor.submit(() -> {
            Profile user = this.db.profileDao().getUserProfile(true);
            if (user != null) {
                runOnUiThread(() -> {
                    this.doneButton.setVisibility(View.VISIBLE);
                });
            }
            return null;
        });

        // For resuming previous session
        this.sessionId = getIntent().getStringExtra("session_id");
        if (this.sessionId == null) {
            this.sessionId = "";
        }
    }

    public void onEnterClicked(View view) {

        Log.d(TAG, "Enter button clicked");

        String year = this.year_spinner.getSelectedItem().toString().trim();
        String quarter = this.quarter_spinner.getSelectedItem().toString().trim();
        String subject = this.subject_view.getText().toString().trim().toUpperCase();
        String number = this.number_view.getText().toString().trim().toUpperCase();
        String classSize = this.class_size_spinner.getSelectedItem().toString().trim();
        String classSizeType = this.class_size_spinner.getSelectedItem().toString().trim().split(" ")[0];

        // If the course information is valid
        if (isValidCourse(year, quarter, subject, number, classSize)) {

            Log.d(TAG, "Course information is valid!");

            this.f1 = this.backgroundThreadExecutor.submit(() -> {

                Profile user = this.db.profileDao().getUserProfile(true);

                // Set and insert profile to DB if the user's profile has not been made yet
                if (user == null) {
                    Log.d(TAG, "User profile has not been created, creating now");

                    String name = getIntent().getStringExtra("name");
                    String photo = getIntent().getStringExtra("photo");

                    user = new Profile(UUID.randomUUID().toString(), name, photo);
                    user.setIsUser(true);
                    this.db.profileDao().insert(user);

                    runOnUiThread(() -> {
                        this.doneButton.setVisibility(View.VISIBLE);
                    });
                }

                // Insert course to DB if it is not already there (avoid duplicates)
                String userId = this.db.profileDao().getUserProfile(true).getProfileId();
                String courseId = this.db.courseDao().getCourseId(userId, year, quarter, subject, number, classSizeType);

                if (!isExistingCourse(courseId)) {
                    Log.d(TAG, "Course not already in DB, adding now");
                    Course course = new Course(userId, year, quarter, subject, number, classSizeType);
                    this.db.courseDao().insert(course);
                }
                else Log.e(TAG, "Duplicate course, will not be added");

                Log.d(TAG, "Autofilling course information");
                return null;
            });

            // Set quarter spinner
            List<String> quarters = new ArrayList<>(Arrays.asList("Fall", "Winter", "Spring", "Summer Session 1", "Summer Session 2", "Special Summer Session"));
            ArrayAdapter<String> quarter_adapter = new ArrayAdapter<>(this, R.layout.spinner_item_text, quarters);
            quarter_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.quarter_spinner.setAdapter(quarter_adapter);

            // Set dynamic year spinner
            List<String> years = new ArrayList<>();
            years.add("Year");
            int thisYear = Calendar.getInstance().get(Calendar.YEAR);
            for (int y = thisYear; y >= 1960; y--) {
                years.add(Integer.toString(y));
            }
            ArrayAdapter<String> year_adapter = new ArrayAdapter<>(this, R.layout.spinner_item_text, years);
            year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.year_spinner.setAdapter(year_adapter);

            // Set class size spinner
            List<String> classSizes = new ArrayList<>(Arrays.asList("Tiny (<40)", "Small (40-75)", "Medium (75-150)", "Large (150-250)", "Huge (250-400)", "Gigantic (400+)"));
            ArrayAdapter<String> class_size_adapter = new ArrayAdapter<>(this, R.layout.spinner_item_text, classSizes);
            class_size_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.class_size_spinner.setAdapter(class_size_adapter);

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

            // Autofill class size field for the next screen
            for (int i = 0; i < this.class_size_spinner.getCount(); i++) {
                if (this.class_size_spinner.getItemAtPosition(i).equals(classSize)) {
                    this.class_size_spinner.setSelection(i);
                }
            }

            // Autofill subject and number fields for the next screen
            this.subject_view.setText(subject);
            this.number_view.setText(number);
        }

        // Logging current Profile and Course object counts in DB
        f1 = this.backgroundThreadExecutor.submit(() -> {
            Log.d(TAG, "PROFILE Count: " + this.db.profileDao().count());
            Log.d(TAG, "COURSE Count: " + this.db.courseDao().count());
            return null;
        });
    }

    // When the done button is clicked
    public void onDoneClicked(View view) {
        Log.d(TAG, "Done button clicked, moving to Home Screen");

        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra("session_id", this.sessionId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "CourseActivity being destroyed");
        super.onDestroy();
        if (this.f1 != null) {
            this.f1.cancel(true);
        }
    }

    // Checks if course information is formatted and inputted correctly
    public boolean isValidCourse(String year, String quarter, String subject, String number, String classSize) {

        Log.d(TAG, "Checking if course information is valid");

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
            for (int i = 0; i < number.trim().length() - 1; i++) {
                if (!Character.isDigit(number.trim().charAt(i))) {
                    Utilities.showError(this, "Error: Invalid Input", "Please enter a valid number for your course.");
                    return false;
                }
            }
            if (!Character.isDigit(number.trim().charAt(number.trim().length() - 1)) && !Character.isLetter(number.trim().charAt(number.trim().length() - 1))) {
                Utilities.showError(this, "Error: Invalid Input", "Please enter a valid number for your course.");
                return false;
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

        if (classSize.equals("Class Size")) {
            Utilities.showError(this, "Error: Invalid Selection", "Please select a class size for your course.");
            return false;
        }

        // Check if entered course is later than the current quarter and year
        int currentQuarter = Utilities.enumerateQuarter(Utilities.getCurrentQuarter());
        int inputQuarter = Utilities.enumerateQuarter(quarter);
        int currentYear = Integer.parseInt(Utilities.getCurrentYear());
        int inputYear = Integer.parseInt(year);
        if (currentYear < inputYear ||
            (currentYear == inputYear && currentQuarter < inputQuarter)) {
            Utilities.showError(this, "Error: Invalid Input", "Please input a present or past course.");
            return false;
        }


        return true;
    }

    // Checks if a course already exists in the DB
    public boolean isExistingCourse(String courseId) {
        Log.d(TAG, "Checking if course is already in DB");
        return courseId != null;
    }

    // Overrides back button to clearing all fields
    @Override
    public void onBackPressed() {
        clearFields();
    }

    public void clearFields() {
        Log.d(TAG, "Back button pressed, clearing fields");

        // Set quarter spinner
        List<String> quarters = new ArrayList<>(Arrays.asList("Quarter", "Fall", "Winter", "Spring", "Summer Session 1", "Summer Session 2", "Special Summer Session"));
        ArrayAdapter<String> quarter_adapter = new ArrayAdapter<>(this, R.layout.spinner_item_text, quarters);
        quarter_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.quarter_spinner.setAdapter(quarter_adapter);

        // Set dynamic year spinner
        List<String> years = new ArrayList<>();
        years.add("Year");
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = thisYear; y >= 1960; y--) {
            years.add(Integer.toString(y));
        }
        ArrayAdapter<String> year_adapter = new ArrayAdapter<>(this, R.layout.spinner_item_text, years);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.year_spinner.setAdapter(year_adapter);

        // Set class size spinner
        List<String> classSizes = new ArrayList<>(Arrays.asList("Class Size", "Tiny (<40)", "Small (40-75)", "Medium (75-150)", "Large (150-250)", "Huge (250-400)", "Gigantic (400+)"));
        ArrayAdapter<String> class_size_adapter = new ArrayAdapter<>(this, R.layout.spinner_item_text, classSizes);
        class_size_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.class_size_spinner.setAdapter(class_size_adapter);

        this.year_spinner.setSelection(0);
        this.quarter_spinner.setSelection(0);
        this.class_size_spinner.setSelection(0);

        this.subject_view.setText("");
        this.number_view.setText("");
    }
}