package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.LargeTest;

import org.junit.Test;


@LargeTest
public class HomeScreenUITest {

    @Test
    public void startButtonPressWithStopFirstTimeTest() {
        ActivityScenario<HomeScreenActivity> homeScreen = ActivityScenario.launch(HomeScreenActivity.class);
        homeScreen.onActivity(activity -> {
            Button startButton = activity.findViewById(R.id.start_button);
            Button stopButton = activity.findViewById(R.id.stop_button);

            startButton.performClick();

            assertEquals(View.GONE, startButton.getVisibility());
            assertEquals(View.VISIBLE, stopButton.getVisibility());
        });
    }
    @Test
    public void stopButtonPressAfterStartTest() {
        ActivityScenario<HomeScreenActivity> homeScreen = ActivityScenario.launch(HomeScreenActivity.class);
        homeScreen.onActivity(activity -> {
            RecyclerView matchesList = activity.findViewById(R.id.matches_view);
            Button startButton = activity.findViewById(R.id.start_button);
            Button stopButton = activity.findViewById(R.id.stop_button);

            stopButton.performClick();

            assertEquals(View.VISIBLE,matchesList.getVisibility());
            assertEquals(View.VISIBLE, startButton.getVisibility());
            assertEquals(View.GONE, stopButton.getVisibility());
        });
    }

    @Test
    public void FirstLaunchViewTest() {
        ActivityScenario<HomeScreenActivity> homeScreen = ActivityScenario.launch(HomeScreenActivity.class);
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