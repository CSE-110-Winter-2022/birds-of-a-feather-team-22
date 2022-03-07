package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.app.Application;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.LargeTest;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.DiscoveredUser;
import com.example.birdsofafeather.db.Profile;

import org.junit.Test;

@LargeTest
public class MockScreenUITest {

    AppDatabase db = AppDatabase.useTestSingleton(ApplicationProvider.getApplicationContext());

    String EMPTY_STRING = "";
    String UUID_TEST = "a4ca50b6-941b-11ec-b909-0242ac120002,,,,\n";
    String NAME_TEST = "Bill,,,,\n";
    String IMAGE_TEST = "something,,,,\n";
    String COURSE1_TEST = "2021,FA,CSE,110,Large\n";
    String[] PROFILE_TEST = {UUID_TEST, NAME_TEST, IMAGE_TEST, COURSE1_TEST};

    public String outputFormattedCSVString() {
        String csvString = new String();
        for (String content : PROFILE_TEST) {
            csvString.concat(content);
        }

        return csvString;
    }

    public DiscoveredUser createTestProfile() {
        return new DiscoveredUser(UUID_TEST, "Something", 1);
    }

    @Test
    public void onCreateUIScreenTestFromHomeScreenTest() {
        ActivityScenario<HomeScreenActivity> homeScreen = ActivityScenario.launch(HomeScreenActivity.class);
        homeScreen.onActivity(activity -> {
            Button startButton = activity.findViewById(R.id.start_button);
            startButton.performClick();
        });
        ActivityScenario<Mocking> mockScreen = ActivityScenario.launch(Mocking.class);
        mockScreen.onActivity(activity2 -> {
            Button enterButton = activity2.findViewById(R.id.Enter_Button);
            EditText textBox = activity2.findViewById(R.id.inputBox);

            assertEquals(EMPTY_STRING, textBox.getText().toString());
            assertEquals(View.VISIBLE, enterButton.getVisibility());
            assertEquals(View.VISIBLE, textBox.getVisibility());
        });
    }

    @Test
    public void onClickEnterButtonWithSomeTextTest() {
        ActivityScenario<HomeScreenActivity> homeScreen = ActivityScenario.launch(HomeScreenActivity.class);
        homeScreen.onActivity(activity -> {
            Button startButton = activity.findViewById(R.id.start_button);
            startButton.performClick();
        });

        ActivityScenario<Mocking> mockScreen = ActivityScenario.launch(Mocking.class);
        mockScreen.onActivity(activity -> {
            Button enterButton = activity.findViewById(R.id.Enter_Button);
            EditText textBox = activity.findViewById(R.id.inputBox);

            textBox.setText(outputFormattedCSVString());

            assertEquals(outputFormattedCSVString(), textBox.getText().toString());

            enterButton.performClick();

            assertEquals(1, db.discoveredUserDao().getAllDiscoveredUsers().size());
        });
    }
}
