package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.widget.Button;
import android.widget.EditText;

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

    //Test if name input contains correct string
    @Test
    public void testDummy() {
        ActivityScenario<NameActivity> scenario = scenarioRule.getScenario();

        scenario.onActivity(activity -> {
            EditText name = activity.findViewById(R.id.name_view);
            Button confirmButton = activity.findViewById(R.id.confirm_button);

            //Test by changing text name
            name.setText("Rob");
            assertEquals("Rob", name.getText().toString());

            name.setText("Alvin");
            assertEquals("Alvin", name.getText().toString());

            name.setText("Drake");
            assertEquals("Drake", name.getText().toString());

            name.setText("Stephen");
            assertEquals("Stephen", name.getText().toString());

            name.setText("Fernando");
            assertEquals("Fernando", name.getText().toString());

            name.setText("Raul");
            assertEquals("Raul", name.getText().toString());

            confirmButton.performClick();

            scenario.close();
        });
    }


}





