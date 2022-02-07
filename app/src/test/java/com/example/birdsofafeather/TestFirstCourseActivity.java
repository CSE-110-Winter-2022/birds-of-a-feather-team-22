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
public class TestFirstCourseActivity {
    @Rule
    public ActivityScenarioRule<FirstCourseActivity> scenarioRule = new ActivityScenarioRule<>(FirstCourseActivity.class);

    @Test
    public void testDummy() {
        ActivityScenario<FirstCourseActivity> scenario = scenarioRule.getScenario();
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase db = AppDatabase.singleton(context);

        scenario.onActivity(activity -> {
            EditText subject = activity.findViewById(R.id.subject_view);
            EditText number = activity.findViewById(R.id.number_view);
            Spinner quarter = activity.findViewById(R.id.quarter_spinner);
            Spinner year = activity.findViewById(R.id.year_spinner);
            Button enterButton = activity.findViewById(R.id.enter_button);

            subject.setText("CSE");
            number.setText("110");
            quarter.setSelection(0);
            year.setSelection(0);

            enterButton.performClick();
            List<Course> courses = db.courseDao().getCourseByProfileId(1);


            assertEquals("cse", courses.get(0).getSubject());
            assertEquals("110", courses.get(0).getNumber());
            assertEquals("fall", courses.get(0).getQuarter());
            assertEquals("2022", courses.get(0).getYear());
        });
    }
}
