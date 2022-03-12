/*
 * This file is capable of performing operations on the database to add courses for self user and
 * allows the self user to be able to interact with the application in order to added such courses.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

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
import com.example.birdsofafeather.db.Wave;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class refers to the screen where the user can add their previously taken courses and
 * adds such courses to the database.
 */
public class CourseActivity extends AppCompatActivity {
    // Log tag
    private final String TAG = "<Course>";

    // DB/Thread fields
    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<List<String>> f1;
    private Future<Profile> f2;
    private Future<List<Course>> f3;
    private Future<String> f4;
    private Future<Void> f5;

    // Self and session fields
    private Profile selfProfile;
    private List<Course> selfCourses;
    private String name;
    private String photo;
    private String sessionId;

    // For determining if we are coming from MatchActivity or not
    private boolean isBack;

    // UI View fields
    private Spinner year_spinner;
    private Spinner quarter_spinner;
    private Spinner class_size_spinner;
    private TextView subject_view;
    private TextView number_view;
    private Button doneButton;

    // Nearby fields
    private BoFMessagesClient messagesClient;
    private ArrayList<String> mockedMessages;
    private BoFMessageListener messageListener;

    /**
     * Initializes the screen and activity for CourseActivity.
     *
     * @param savedInstanceState A bundle that contains information regarding layout and data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        this.setTitle("Setup: Add Previous Course");

        Log.d(TAG, "Setting up Course Screen");

        // DB-related initializations
        this.db = AppDatabase.singleton(this);
        this.messagesClient = new BoFMessagesClient(Nearby.getMessagesClient(this));

        this.isBack = getIntent().getBooleanExtra("isBack", false);
        Log.d(TAG, "isBack is " + this.isBack);
        this.sessionId = getIntent().getStringExtra("session_id");
        if (this.sessionId == null) {

        }

        // View initializations
        this.year_spinner = findViewById(R.id.year_spinner);
        this.quarter_spinner = findViewById(R.id.quarter_spinner);
        this.class_size_spinner = findViewById(R.id.class_size_spinner);
        this.subject_view = findViewById(R.id.subject_view);
        this.number_view = findViewById(R.id.number_view);
        this.doneButton = findViewById(R.id.done_button);

        this.doneButton.setVisibility(View.GONE);

        setSpinnersWithHint();

        this.name = getIntent().getStringExtra("name");
        this.photo = getIntent().getStringExtra("photo");

        // Get self profile
        this.f2 = this.backgroundThreadExecutor.submit(() -> this.db.profileDao().getSelfProfile(true));
        try {
            this.selfProfile = this.f2.get();
            Log.d(TAG, "Self profile retrieved!");
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving self profile!");
            e.printStackTrace();
        }

        if (isBack) {
            this.doneButton.setVisibility(View.VISIBLE);

            this.messageListener = new BoFMessageListener(this.sessionId, this);
            this.messagesClient = new BoFMessagesClient(Nearby.getMessagesClient(this));
            this.messagesClient.subscribe(this.messageListener);
        }
        // For resuming session with mock data
        this.mockedMessages = getIntent().getStringArrayListExtra("mocked_messages");
    }

    /**
     * Closes/destroys the current activity.
     */
    @Override
    protected void onDestroy() {
        if (this.f1 != null) {
            this.f1.cancel(true);
        }
        if (this.f2 != null) {
            this.f2.cancel(true);
        }
        if (this.f3 != null) {
            this.f3.cancel(true);
        }
        if (this.f4 != null) {
            this.f4.cancel(true);
        }
        if (this.f5 != null) {
            this.f5.cancel(true);
        }
        if (isBack) {
            this.messagesClient.unsubscribe(this.messageListener);
        }
        super.onDestroy();
        Log.d(TAG, "CourseActivity destroyed!");
    }

    /**
     * Allows the user to input a course, that gets processed, into the database .
     *
     * @param view The current view
     */
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

            // Set and insert profile to DB if the self's profile has not been made yet
            if (this.selfProfile == null) {
                Log.d(TAG, "User profile has not been created, creating now");
                this.selfProfile = new Profile(UUID.randomUUID().toString(), name, photo);
                this.selfProfile.setIsSelf(true);

                this.f1 = this.backgroundThreadExecutor.submit(() -> {
                    this.db.profileDao().insert(selfProfile);

                    return null;
                });

                this.doneButton.setVisibility(View.VISIBLE);
            }

            // Get course id
            String userId = this.selfProfile.getProfileId();
            this.f4 = this.backgroundThreadExecutor.submit(() -> this.db.courseDao().getCourseId(userId, year, quarter, subject, number, classSizeType));

            String courseId;
            try {
                courseId = this.f4.get();

                // Insert course to DB if it is not already there (avoid duplicates)
                if (courseId == null) {
                    Log.d(TAG, "Adding course to DB");
                    Course course = new Course(userId, year, quarter, subject, number, classSizeType);
                    this.f5 = this.backgroundThreadExecutor.submit(() -> {
                        this.db.courseDao().insert(course);
                        return null;
                    });

                    // Get list of all self courses
                    this.f3 = this.backgroundThreadExecutor.submit(() -> this.db.courseDao().getCoursesByProfileId(this.selfProfile.getProfileId()));
                    try {
                        this.selfCourses = this.f3.get();
                        Log.d(TAG, "Self courses retrieved!");
                    } catch (Exception e) {
                        Log.e(TAG, "Error retrieving self courses!");
                        e.printStackTrace();
                    }

                    // Update outgoing waves due to added courses
                    if (isBack) {
                        this.f5 = this.backgroundThreadExecutor.submit(() -> {
                            List<Wave> waves = this.db.waveDao().getAllWaves();
                            if (waves != null) {
                                for (Wave wave : waves) {
                                    Log.d(TAG, "Found outgoing wave, updating now...");
                                    Message oldWaveMessage = new Message(wave.getWave().getBytes(StandardCharsets.UTF_8));

                                    this.messagesClient.unpublish(oldWaveMessage);
                                    String newWaveMessageString = Utilities.encodeWaveMessage(this.selfProfile, this.selfCourses, wave.getProfileId());
                                    wave.setWave(newWaveMessageString);
                                    this.db.waveDao().update(wave);


                                    Message newWaveMessage = new Message(newWaveMessageString.getBytes(StandardCharsets.UTF_8));
                                    this.messagesClient.publish(newWaveMessage);
                                }
                            }
                            return null;
                        });
                    }
                }
                else {
                    Log.e(TAG, "Duplicate course in DB!");
                }
            } catch (Exception e) {
                Log.d(TAG, "Unable to retrieve course id!");
            }
            setSpinnersWithoutHint();
        }
        else {
            setSpinnersWithHint();
        }

        autofillFields(subject, number, quarter, year, classSize);



        // Logging current Profile and Course object counts in DB
        f1 = this.backgroundThreadExecutor.submit(() -> {
            Log.d(TAG, "PROFILE Count: " + this.db.profileDao().count());
            Log.d(TAG, "COURSE Count: " + this.db.courseDao().count());
            return null;
        });
    }

    /**
     * Allows user to declare being finished with inputting course.
     *
     * @param view The current view
     */
    public void onDoneClicked(View view) {
        Log.d(TAG, "Done button clicked, moving to Home Screen");

        Intent intent = new Intent(this, MatchActivity.class);

        // Perpetuate mocked messages in the event that the user has mocked messages but decided to add more courses
        if (!isBack) {
            intent.putExtra("session_id", "");
        }
        else {
            intent.putStringArrayListExtra("mocked_messages", this.mockedMessages);
        }

        startActivity(intent);
        finish();
    }

    /**
     * Checks if course information is formatted and inputted correctly.
     *
     * @param year A given year
     * @param quarter A given quarter
     * @param subject A given subject
     * @param number A given number
     * @param classSize A given class size
     * @return True if the entered course information if valid, false otherwise
     */
    public boolean isValidCourse(String year, String quarter, String subject, String number, String classSize) {

        Log.d(TAG, "Checking if course information is valid...");

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


    /**
     * Override the back button to clear all fields.
     */
    @Override
    public void onBackPressed() {
        clearFields();
    }

    /**
     * Clears the course fields whenever the back button is pressed.
     */
    public void clearFields() {
        Log.d(TAG, "Back button pressed, clearing fields");

        setSpinnersWithHint();

        this.subject_view.setText("");
        this.number_view.setText("");
    }

    /**
     * Sets all spinners to the first item with hint items.
     */
    public void setSpinnersWithHint() {
        // Set dynamic quarter spinner
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

        // Set dynamic class size spinner
        List<String> classSizes = new ArrayList<>(Arrays.asList("Class Size", "Tiny (<40)", "Small (40-75)", "Medium (75-150)", "Large (150-250)", "Huge (250-400)", "Gigantic (400+)"));
        ArrayAdapter<String> class_size_adapter = new ArrayAdapter<>(this, R.layout.spinner_item_text, classSizes);
        class_size_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.class_size_spinner.setAdapter(class_size_adapter);
    }

    /**
     * Sets all spinners to the first item without hint items.
     */
    public void setSpinnersWithoutHint() {
        // Set quarter spinner
        List<String> quarters = new ArrayList<>(Arrays.asList("Fall", "Winter", "Spring", "Summer Session 1", "Summer Session 2", "Special Summer Session"));
        ArrayAdapter<String> quarter_adapter = new ArrayAdapter<>(this, R.layout.spinner_item_text, quarters);
        quarter_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.quarter_spinner.setAdapter(quarter_adapter);

        // Set dynamic year spinner
        List<String> years = new ArrayList<>();
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
    }

    /**
     * Autofills all fields to the previous input.
     *
     * @param subject The previous subject.
     * @param number The previous number.
     * @param quarter The previous quarter.
     * @param year The previous year.
     * @param classSize The previous class size.
     */
    public void autofillFields(String subject, String number, String quarter, String year, String classSize) {
        // Autofill subject and number fields for the next screen
        this.subject_view.setText(subject);
        this.number_view.setText(number);

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
    }
}