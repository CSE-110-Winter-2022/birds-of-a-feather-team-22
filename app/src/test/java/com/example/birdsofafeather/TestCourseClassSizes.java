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

@RunWith(AndroidJUnit4.class)
public class TestCourseClassSizes {

    @Rule
    public ActivityScenarioRule<CourseActivity> CourseScenarioRule = new ActivityScenarioRule<>(CourseActivity.class);

    public Context context = ApplicationProvider.getApplicationContext();
    public AppDatabase db = AppDatabase.useTestSingleton(context);

    @Test
    public void testTinyCourse() {
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
            number.setText("110");
            quarter.setSelection(2);
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

            assertEquals(db.courseDao().count(), 1);

            //get courses by profile id
            List<Course> courses = db.courseDao().getCoursesByProfileId(p1.getProfileId());

            assertEquals(courses.get(0).getSubject(), "CSE");
            assertEquals(courses.get(0).getNumber(), "110");
            assertEquals(courses.get(0).getQuarter(), "Winter");
            assertEquals(courses.get(0).getYear(), "2022");
            assertEquals(courses.get(0).getClassSize(), "Tiny (<40)");

            scenario.close();
        });
    }

    @Test
    public void testGiganticCourse() {
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
            number.setText("110");
            quarter.setSelection(2);
            year.setSelection(1);
            size.setSelection(6);

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

            assertEquals(db.courseDao().count(), 1);

            //get courses by profile id
            List<Course> courses = db.courseDao().getCoursesByProfileId(p1.getProfileId());

            assertEquals(courses.get(0).getSubject(), "CSE");
            assertEquals(courses.get(0).getNumber(), "110");
            assertEquals(courses.get(0).getQuarter(), "Winter");
            assertEquals(courses.get(0).getYear(), "2022");
            assertEquals(courses.get(0).getClassSize(), "Gigantic (400+)");

            scenario.close();
        });
    }
}
