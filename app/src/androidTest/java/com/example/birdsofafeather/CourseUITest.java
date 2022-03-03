package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CourseUITest {

    @Test
    public void InputCourseAndEnterTest() {
        ActivityScenario<CourseActivity> courseScreen = ActivityScenario.launch(CourseActivity.class);
        courseScreen.onActivity(activity -> {
            EditText subject = activity.findViewById(R.id.subject_view);
            EditText number = activity.findViewById(R.id.number_view);
            Spinner quarter = activity.findViewById(R.id.quarter_spinner);
            Spinner year = activity.findViewById(R.id.year_spinner);
            Spinner size = activity.findViewById(R.id.class_size_spinner);
            Button enterButton = activity.findViewById(R.id.enter_button);
            Button doneButton = activity.findViewById(R.id.done_button);


            subject.setText("CSE");
            number.setText("110");
            quarter.setSelection(1);
            year.setSelection(3);
            size.setSelection(1);

            enterButton.performClick();

            assertEquals(subject.getText().toString(), "CSE");
            assertEquals(number.getText().toString(), "110");
            assertEquals(quarter.getSelectedItem(), "Fall");
            assertEquals(year.getSelectedItem(), "2020");
            assertEquals(size.getSelectedItem(), "(Tiny <40)");
            assertEquals(doneButton.getVisibility(), View.VISIBLE);
        });
    }

    public void FirstLaunchViewTest() {
        ActivityScenario<CourseActivity> courseScreen = ActivityScenario.launch(CourseActivity.class);
        courseScreen.onActivity(activity -> {
            EditText subject = activity.findViewById(R.id.subject_view);
            EditText number = activity.findViewById(R.id.number_view);
            Spinner quarter = activity.findViewById(R.id.quarter_spinner);
            Spinner year = activity.findViewById(R.id.year_spinner);
            Button enterButton = activity.findViewById(R.id.enter_button);
            Spinner size = activity.findViewById(R.id.class_size_spinner);
            Button doneButton = activity.findViewById(R.id.done_button);

            assertEquals(subject.getText().toString(), "");
            assertEquals(number.getText().toString(), "");
            assertEquals(quarter.getVisibility(), View.VISIBLE);
            assertEquals(quarter.getSelectedItemPosition(), 0);
            assertEquals(quarter.getSelectedItem(), "Quarter");
            assertEquals(year.getVisibility(), View.VISIBLE);
            assertEquals(year.getSelectedItemPosition(), 0);
            assertEquals(year.getSelectedItem(), "Year");
            assertEquals(size.getVisibility(), View.VISIBLE);
            assertEquals(size.getSelectedItemPosition(), 0);
            assertEquals(size.getSelectedItem(), "Size");
            assertEquals(enterButton.getVisibility(), View.VISIBLE);
            assertEquals(doneButton.getVisibility(), View.GONE);

        });
    }
}

