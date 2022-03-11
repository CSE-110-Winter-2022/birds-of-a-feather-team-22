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

import org.junit.Test;

import java.util.ArrayList;

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
        StringBuilder csvString = new StringBuilder();
        for (String content : PROFILE_TEST) {
            csvString.append(content);
        }

        return csvString.toString();
    }

    public DiscoveredUser createTestProfile() {
        return new DiscoveredUser(UUID_TEST, "Something", 1);
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
    public void onClickEnterButtonWithSomeTextTest() {
        ActivityScenario<MockingActivity> mockScreen = ActivityScenario.launch(MockingActivity.class);
        mockScreen.onActivity(activity -> {
            Button enterButton = activity.findViewById(R.id.mock_enter_button);
            EditText textBox = activity.findViewById(R.id.mocking_input_view);

            textBox.setText(outputFormattedCSVString());

            assertEquals(outputFormattedCSVString(), textBox.getText().toString());

            enterButton.performClick();

            assertEquals(EMPTY_STRING, textBox.getText().toString());
            assertEquals(1, db.discoveredUserDao().count());
        });
    }

    @Test
    public void enterInvalidProfileInfoIntoTextBoxTest() {
        ActivityScenario<MockingActivity> mockScreen = ActivityScenario.launch(MockingActivity.class);
        mockScreen.onActivity(activity -> {
            Button enterButton = activity.findViewById(R.id.mock_enter_button);
            EditText textBox = activity.findViewById(R.id.mocking_input_view);

            textBox.setText("Something invalid");

            enterButton.performClick();

            assertEquals(EMPTY_STRING, textBox.getText().toString());
            assertEquals(0, db.discoveredUserDao().count());
        });
    }
}
