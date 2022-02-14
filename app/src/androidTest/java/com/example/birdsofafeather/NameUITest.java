package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NameUITest {

    @Test
    public void EnterNameAndConfirmTest() {
        ActivityScenario<NameActivity> nameScreen = ActivityScenario.launch(NameActivity.class);
        nameScreen.onActivity(activity -> {
            TextView nameView = activity.findViewById(R.id.name_view);
            Button confirmButton = activity.findViewById(R.id.confirm_button);

            nameView.setText("Rick");
            confirmButton.performClick();

            assertEquals(nameView.getText().toString(), "Rick");
        });
    }

    public void FirstLaunchViewTest() {
        ActivityScenario<NameActivity> nameScreen = ActivityScenario.launch(NameActivity.class);
        nameScreen.onActivity(activity -> {
            TextView nameView = activity.findViewById(R.id.name_view);
            Button confirmButton = activity.findViewById(R.id.confirm_button);

            assertEquals(nameView.getText().toString(), "John Doe");
            assertEquals(confirmButton.getVisibility(), View.VISIBLE);
        });
    }
}

