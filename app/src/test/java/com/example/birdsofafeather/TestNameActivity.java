package com.example.birdsofafeather;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.jar.Attributes;


@RunWith(AndroidJUnit4.class)
public class TestNameActivity {

    @Rule
    public ActivityScenarioRule<NameActivity> scenarioRule = new ActivityScenarioRule<>(NameActivity.class);

    @Test
    public void testNameEmpty(){
        ActivityScenario<NameActivity> scenario = scenarioRule.getScenario();

        scenario.onActivity(activity -> {

            onView(withId(R.id.name_view)).perform(typeText(""));
            closeSoftKeyboard(); //close android keyboard that pops up
            onView(withId(R.id.confirm_button)).perform(click());//click submit with empty input

            //check if error dialog box gets displayed
            //onView(withText("Please enter a valid name for your profile.")).check(matches(isDisplayed()));
            assertEquals(activity.getClass(), NameActivity.class); //tests to make sure you
                                                                   // are on the same activity
        });
    }

    @Test
    public void testNameValid() {
        ActivityScenario<NameActivity> scenario = scenarioRule.getScenario();

        scenario.onActivity(activity -> {

            onView(withId(R.id.name_view)).perform(typeText("John"));
            closeSoftKeyboard(); //close android keyboard that pops up
            onView(withId(R.id.confirm_button)).perform(click());
            onView(withId(R.id.name_view)).check(matches(withText("John")));
            assertEquals(activity.getClass(), PhotoActivity.class);
        });
    }

    /*
    @Test
    public void testPassedIntent(){
        ActivityScenario<NameActivity> scenario = scenarioRule.getScenario();

        scenario.onActivity(activity -> {
            onView(withId(R.id.name_view)).perform(typeText("drake"));
            closeSoftKeyboard(); //close android keyboard that pops up
            onView(withId(R.id.confirm_button)).perform(click());//click submit with empty input

            Intent resultData = new Intent();
            resultData.putExtra("name", "drake");
            ActivityResult result = new ActivityResult(Activity.RESULT_OK, resultData);

            //intending(toPackage(HomeScreenActivity.class.getName())).respondWith(result));

            // Perform action that throws the Intent
            //onView(withId(R.id.imagebutton_tag)).perform(click());
        });

    }*/
}





