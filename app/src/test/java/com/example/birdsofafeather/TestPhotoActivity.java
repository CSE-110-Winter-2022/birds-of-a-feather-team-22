package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;
import com.example.birdsofafeather.db.ProfileDao_Impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

@RunWith(AndroidJUnit4.class)
public class TestPhotoActivity {
    @Rule
    public ActivityScenarioRule<PhotoActivity> scenarioRule = new ActivityScenarioRule<>(PhotoActivity.class);

    //Test if photo input is being received
    @Test
    public void testPhotoActivityInput() {
        ActivityScenario<PhotoActivity> scenario = scenarioRule.getScenario();

        scenario.onActivity(activity -> {
            EditText photo = activity.findViewById(R.id.photo_view);
            Button submitButton = activity.findViewById(R.id.submit_button);

            photo.setText("test_photo.png");
            assertEquals("test_photo.png", photo.getText().toString());

            photo.setText("another_test_photo.png");
            assertEquals("another_test_photo.png", photo.getText().toString());

            submitButton.performClick();

            scenario.close();
        });
    }

    //test if photo will go to default photo if url not valid
    @Test
    public void testInvalidPhotos() {
        ActivityScenario<PhotoActivity> scenario = scenarioRule.getScenario();
        scenario.onActivity(activity -> {
            EditText photo = activity.findViewById(R.id.photo_view);
            Button submitButton = activity.findViewById(R.id.submit_button);

            photo.setText("not_valid_test_photo.png");
            submitButton.performClick();

            Profile p = new Profile("name", photo.getText().toString());

            //default photo if photo not valid: https://imgur.com/a/vgBKZMN
            assertEquals("https://i.imgur.com/MZH5yxZ.png", p.getPhoto());

            scenario.close();
        });
    }

    //test if photo will be input photo if url is valid
    @Test
    public void testValidPhoto() {
        ActivityScenario<PhotoActivity> scenario = scenarioRule.getScenario();
        scenario.onActivity(activity -> {
            EditText photo = activity.findViewById(R.id.photo_view);
            Button submitButton = activity.findViewById(R.id.submit_button);

            photo.setText("https://cse.ucsd.edu/sites/cse.ucsd.edu/files/faculty/griswold17-115x150.jpg");
            submitButton.performClick();

            Profile p = new Profile("name", photo.getText().toString());

            //default photo if photo not valid: https://imgur.com/a/vgBKZMN
            assertEquals("https://cse.ucsd.edu/sites/cse.ucsd.edu/files/faculty/griswold17-115x150.jpg", p.getPhoto());

            scenario.close();
        });
    }
}
