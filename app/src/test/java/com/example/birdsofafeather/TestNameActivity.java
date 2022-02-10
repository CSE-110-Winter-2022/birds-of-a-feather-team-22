package com.example.birdsofafeather;
/*
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
public class TestNameActivity {

    @Rule
    public ActivityScenarioRule<NameActivity> scenarioRule = new ActivityScenarioRule<>(NameActivity.class);

    @Test
    public void testDummy() {
        ActivityScenario<NameActivity> scenario = scenarioRule.getScenario();
//        Context context = ApplicationProvider.getApplicationContext();
//        AppDatabase db = AppDatabase.singleton(context);

        scenario.onActivity(activity -> {
            EditText name = activity.findViewById(R.id.name_view);
            Button confirmButton = activity.findViewById(R.id.confirm_button);

            name.setText("Rob");
            confirmButton.performClick();

            assertEquals("Rob", name.getText().toString());
        });
    }
}
*/

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import androidx.test.espresso.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import androidx.test.espresso.action.*;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestNameActivity {

    //@Rule
    //public ActivityScenarioRule rule = new ActivityScenarioRule<>(MainActivity.class);

    /*test whether or not the 'Show My Name' button works properly*/
    @Test
    public void testNameEntered() {

        /*launch scenario for MainActivity*/
        try(ActivityScenario<NameActivity> scenario =
                    ActivityScenario.launch(NameActivity.class)) {
            scenario.onActivity(activity -> {
                closeSoftKeyboard();
                onView(withId(R.id.profile_name_textview))
                        .perform(typeText("Drake"),
                                ViewActions.closeSoftKeyboard());

                activity.findViewById(R.id.set_name_button).performClick();
                //onView(withId(R.id.set_name_button)).perform(click());

                /*grab string from TextView object and check to make sure it is the correct string*/
                TextView profileNameTextView =
                        activity.findViewById(R.id.profile_name_textview);
                String profileName = profileNameTextView.getText().toString();
                assertEquals("Drake", profileName);

            });
        }

    }

    @Test
    public void testNameIsTooLong() {
        try(ActivityScenario<NameActivity> scenario =
                    ActivityScenario.launch(SetNameActivity.class)) {
            scenario.onActivity(activity -> {

                closeSoftKeyboard();
                onView(withId(R.id.profile_name_textview)).perform(typeText(
                        "DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD"));
                closeSoftKeyboard();
                activity.findViewById(R.id.set_name_button).performClick();

                assertTrue(activity.getLastDialog() != null);
                assertTrue(activity.getLastDialog().isShowing());
                assertTrue(activity.getClass().getSimpleName().equals("SetNameActivity"));

            });
        }
    }

    /*test whether or not the 'hide my name' button works*/
    @Test
    public void testNameIsEmpty() {

        try(ActivityScenario<NameActivity> scenario = ActivityScenario.launch(SetNameActivity.class)) {
            scenario.onActivity(activity -> {

                closeSoftKeyboard();
                activity.findViewById(R.id.set_name_button).performClick();

                assertTrue(activity.getLastDialog() != null);
                assertTrue(activity.getLastDialog().isShowing());
                assertTrue(activity.getClass().getSimpleName().equals("SetNameActivity"));

            });
        }
    }
}



