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

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

@RunWith(AndroidJUnit4.class)
public class TestCourseActivity {
    @Rule
    public ActivityScenarioRule<CourseActivity> CourseScenarioRule = new ActivityScenarioRule<>(CourseActivity.class);

    public Context context = ApplicationProvider.getApplicationContext();
    public AppDatabase db = AppDatabase.useTestSingleton(context);

    @Test
    public void testCourseActivity() {
        ActivityScenario<CourseActivity> scenario = CourseScenarioRule.getScenario();


        scenario.onActivity(activity -> {
            EditText subject = activity.findViewById(R.id.subject_view);
            EditText number = activity.findViewById(R.id.number_view);
            Spinner quarter = activity.findViewById(R.id.quarter_spinner);
            Spinner year = activity.findViewById(R.id.year_spinner);
            Button enterButton = activity.findViewById(R.id.enter_button);

            subject.setText("CSE");
            number.setText("100");
            quarter.setSelection(1);
            year.setSelection(1);

            enterButton.performClick();
            List<Course> courses = db.courseDao().getCoursesByProfileId(1);


            assertEquals("CSE", courses.get(0).getSubject());
            assertEquals("100", courses.get(0).getNumber());
            assertEquals("Fall", courses.get(0).getQuarter());
            assertEquals("2022", courses.get(0).getYear());
        });
    }

}
