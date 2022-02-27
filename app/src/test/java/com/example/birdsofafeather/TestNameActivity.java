package com.example.birdsofafeather;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.app.AlertDialog;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class TestNameActivity {

    @Rule
    public ActivityScenarioRule<NameActivity> scenarioRule = new ActivityScenarioRule<>(NameActivity.class);

    @Test
    public void testNameEmpty(){
        ActivityScenario<NameActivity> scenario = scenarioRule.getScenario();

        scenario.onActivity(activity -> {

            onView(withId(R.id.name_view)).perform(clearText());
            onView(withId(R.id.name_view)).perform(typeText(""));
            closeSoftKeyboard(); //close android keyboard that pops up
            onView(withId(R.id.confirm_button)).perform(click());//click submit with empty input

            //check if error dialog box gets displayed
            assertNotEquals(Utilities.mostRecentDialog, null);

        });
    }

    @Test
    public void testNameValid() {
        ActivityScenario<NameActivity> scenario = scenarioRule.getScenario();

        scenario.onActivity(activity -> {

            onView(withId(R.id.name_view)).perform(clearText());
            onView(withId(R.id.name_view)).perform(typeText("John"));
            closeSoftKeyboard(); //close android keyboard that pops up
            onView(withId(R.id.confirm_button)).perform(click());
            onView(withId(R.id.name_view)).check(matches(withText("John")));

        });
    }

}