package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@LargeTest
public class MockScreenUITest {

    AppDatabase db = AppDatabase.useTestSingleton(ApplicationProvider.getApplicationContext());

    String EMPTY_STRING = "";

    String UUID_TEST1 = "a4ca50b6-941b-11ec-b909-0242ac120002,,,,\n";
    String NAME_TEST1 = "Bill,,,,\n";
    String IMAGE_TEST1 = "something,,,,\n";
    String COURSE1_TEST1 = "2021,FA,CSE,110,Large\n";

    String UUID_TEST2 = "a4ca50b6-941b-11ec-b909-0242ac120002,,,,\n";
    String NAME_TEST2 = "Bill,,,,\n";
    String IMAGE_TEST2 = "something,,,,\n";
    String COURSE1_TEST2 = "2021,FA,CSE,110,Large\n";

    String UUID_TEST3 = "a4ca50b6-941b-11ec-b909-0242ac120002,,,,\n";
    String NAME_TEST3 = "Bill,,,,\n";
    String IMAGE_TEST3 = "something,,,,\n";
    String COURSE1_TEST3 = "2021,FA,CSE,110,Large\n";

    String[] PROFILE_TEST1 = {UUID_TEST1, NAME_TEST1, IMAGE_TEST1, COURSE1_TEST1};
    String[] PROFILE_TEST2 = {UUID_TEST2, NAME_TEST2, IMAGE_TEST2, COURSE1_TEST2};
    String[] PROFILE_TEST3 = {UUID_TEST3, NAME_TEST3, IMAGE_TEST3, COURSE1_TEST3};

    public String outputFormattedCSVString(int num) {
        StringBuilder csvString = new StringBuilder();
        switch(num) {
            case 1:
                for (String content : PROFILE_TEST1) {
                    csvString.append(content);
                }
                return csvString.toString();
            case 2:
                for (String content : PROFILE_TEST2) {
                    csvString.append(content);
                }
                return csvString.toString();
            case 3:
                for (String content : PROFILE_TEST3) {
                    csvString.append(content);
                }
                return csvString.toString();
            default:
                return new StringBuilder().toString();
        }
    }

    @Before
    public void addSelfUser() {
        db.profileDao().insert(new Profile("1", "Bill", true, false, false, false));
    }

    @Test
    public void onCreateUIScreenTest() {
        ActivityScenario<MockingActivity> mockScreen = ActivityScenario.launch(MockingActivity.class);
        mockScreen.onActivity(activity -> {
            Button enterButton = activity.findViewById(R.id.mock_enter_button);
            EditText textBox = activity.findViewById(R.id.mocking_input_view);

            assertEquals(EMPTY_STRING, textBox.getText().toString());
            assertEquals(View.VISIBLE, enterButton.getVisibility());
            assertEquals(View.VISIBLE, textBox.getVisibility());
        });
    }

    @Test
    public void enteringOnePersonMockTest() {
        ActivityScenario<MockingActivity> mockScreen = ActivityScenario.launch(MockingActivity.class);
        mockScreen.onActivity(activity -> {
            Button enterButton = activity.findViewById(R.id.mock_enter_button);
            EditText textBox = activity.findViewById(R.id.mocking_input_view);
            textBox.setText(outputFormattedCSVString(1));

            assertEquals(outputFormattedCSVString(1), textBox.getText().toString());

            enterButton.performClick();

            assertEquals(EMPTY_STRING, textBox.getText().toString());
        });
    }

    @Test
    public void enteringMultipleMockUITest() {
        ActivityScenario<MockingActivity> mockScreen = ActivityScenario.launch(MockingActivity.class);
        mockScreen.onActivity(activity -> {
            Button enterButton = activity.findViewById(R.id.mock_enter_button);
            EditText textBox = activity.findViewById(R.id.mocking_input_view);

            textBox.setText(outputFormattedCSVString(1));
            assertEquals(outputFormattedCSVString(1), textBox.getText().toString());
            enterButton.performClick();
            assertEquals(EMPTY_STRING, textBox.getText().toString());

            textBox.setText(outputFormattedCSVString(2));
            assertEquals(outputFormattedCSVString(2), textBox.getText().toString());
            enterButton.performClick();
            assertEquals(EMPTY_STRING, textBox.getText().toString());

            textBox.setText(outputFormattedCSVString(3));
            assertEquals(outputFormattedCSVString(3), textBox.getText().toString());
            enterButton.performClick();
            assertEquals(EMPTY_STRING, textBox.getText().toString());
        });
    }
}
