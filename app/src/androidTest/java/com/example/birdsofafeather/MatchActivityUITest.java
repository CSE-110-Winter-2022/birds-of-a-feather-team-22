package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.LargeTest;

import org.junit.Test;


@LargeTest
public class MatchActivityUITest {

    @Test
    public void startButtonPressForFirstTimeTest() {
        ActivityScenario<MatchActivity> homeScreen = ActivityScenario.launch(MatchActivity.class);
        homeScreen.onActivity(activity -> {
            Button startButton = activity.findViewById(R.id.start_button);
            Button stopButton = activity.findViewById(R.id.stop_button);

            startButton.performClick();

            assertEquals(View.INVISIBLE, stopButton.getVisibility());
            assertEquals(View.VISIBLE, startButton.getVisibility());
        });
    }

    @Test
    public void FirstLaunchViewTest() {
        ActivityScenario<MatchActivity> homeScreen = ActivityScenario.launch(MatchActivity.class);
        homeScreen.onActivity(activity -> {
            RecyclerView matchesView = activity.findViewById(R.id.matches_view);
            Button startButton = activity.findViewById(R.id.start_button);
            Button stopButton = activity.findViewById(R.id.stop_button);

            assertEquals(matchesView.getVisibility(), View.VISIBLE);
            assertEquals(startButton.getVisibility(), View.VISIBLE);
            assertEquals(stopButton.getVisibility(), View.INVISIBLE);

        });
    }
}