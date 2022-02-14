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

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

@RunWith(AndroidJUnit4.class)
public class TestPhotoActivity {
    @Rule
    public ActivityScenarioRule<PhotoActivity> scenarioRule = new ActivityScenarioRule<>(PhotoActivity.class);

    @Test
    public void testPhotoActivity() {
        ActivityScenario<PhotoActivity> scenario = scenarioRule.getScenario();

        scenario.onActivity(activity -> {
            EditText photo = activity.findViewById(R.id.photo_view);
            Button submitButton = activity.findViewById(R.id.submit_button);

            photo.setText("test_photo.png");
            submitButton.performClick();

            assertEquals("test_photo.png", photo.getText().toString());
        });
    }
}
