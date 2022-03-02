package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;


import android.content.Context;
import android.util.Log;
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

@RunWith(AndroidJUnit4.class)
public class TestCourseActivity {
    @Rule
    public ActivityScenarioRule<CourseActivity> CourseScenarioRule = new ActivityScenarioRule<>(CourseActivity.class);

    public Context context = ApplicationProvider.getApplicationContext();
    public AppDatabase db = AppDatabase.useTestSingleton(context);

    //Test UI input courses
    @Test
    public void testCourseActivity() {
        ActivityScenario<CourseActivity> scenario = CourseScenarioRule.getScenario();

        //Scenario of inputting courses on the UI
        scenario.onActivity(activity -> {
            EditText subject = activity.findViewById(R.id.subject_view);
            EditText number = activity.findViewById(R.id.number_view);
            Spinner quarter = activity.findViewById(R.id.quarter_spinner);
            Spinner year = activity.findViewById(R.id.year_spinner);
            Spinner size = activity.findViewById(R.id.class_size_spinner);
            Button enterButton = activity.findViewById(R.id.enter_button);

            //setting fields
            subject.setText("CSE");
            number.setText("100");
            quarter.setSelection(1);
            year.setSelection(1);
            size.setSelection(1);

            //mimic entering course into db (can't actually do it since we are using new local test db)
            //enterButton.performClick();

            Profile p1 = new Profile ("test_name", "test_photo");
            Course c1 = new Course(p1.getProfileId(), year.getSelectedItem().toString(),
                    quarter.getSelectedItem().toString(), subject.getText().toString(),number.getText().toString()
                    ,size.getSelectedItem().toString());

            db.profileDao().insert(p1);
            db.courseDao().insert(c1);

            assertEquals(1, db.profileDao().count());
            assertEquals(1, db.courseDao().count());

            subject.setText("MATH");
            number.setText("183");
            quarter.setSelection(6);
            year.setSelection(10);
            size.setSelection(2);

            //enterButton.performClick();

            Course c2 = new Course(p1.getProfileId(), year.getSelectedItem().toString(),
                    quarter.getSelectedItem().toString(), subject.getText().toString(),number.getText().toString()
                    , size.getSelectedItem().toString());

            db.courseDao().insert(c2);

            assertEquals(2, db.courseDao().count());

            subject.setText("COGS");
            number.setText("108");
            quarter.setSelection(3);
            year.setSelection(3);
            size.setSelection(4);

            //enterButton.performClick();

            Course c3 = new Course(p1.getProfileId(), year.getSelectedItem().toString(),
                    quarter.getSelectedItem().toString(), subject.getText().toString(),number.getText().toString()
                    , size.getSelectedItem().toString());

            db.courseDao().insert(c3);

            assertEquals(db.courseDao().count(), 3);

            //get courses by profile id
            List<Course> courses = db.courseDao().getCoursesByProfileId(p1.getProfileId());

            assertEquals(courses.get(0).getSubject(), "CSE");
            assertEquals(courses.get(0).getNumber(), "100");
            assertEquals(courses.get(0).getQuarter(), "Fall");
            assertEquals(courses.get(0).getYear(), "2022");
            assertEquals(courses.get(0).getClassSize(), "Tiny (<40)");


            assertEquals(courses.get(1).getSubject(), "MATH");
            assertEquals(courses.get(1).getNumber(), "183");
            assertEquals(courses.get(1).getQuarter(), "Special Summer Session");
            assertEquals(courses.get(1).getYear(), "2013");
            assertEquals(courses.get(1).getClassSize(), "Small (40-75)");

            assertEquals(courses.get(2).getSubject(), "COGS");
            assertEquals(courses.get(2).getNumber(), "108");
            assertEquals(courses.get(2).getQuarter(), "Spring");
            assertEquals(courses.get(2).getYear(), "2020");
            assertEquals(courses.get(2).getClassSize(), "Large (150-250)");


            scenario.close();
        });

    }


}
