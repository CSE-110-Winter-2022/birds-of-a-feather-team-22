package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.LargeTest;

import com.example.birdsofafeather.db.Profile;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@LargeTest
public class HomeScreenUITest {
//
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
    public void matchesViewAdapterTest() {
        Context context = ApplicationProvider.getApplicationContext();
        List<Pair<Profile, Integer>> matches = new ArrayList<>();
        matches.add(new Pair(new Profile(1, "Bob", "test"), 1));
        matches.add(new Pair(new Profile(2, "Sam", "test"), 1));
        matches.add(new Pair(new Profile(3, "Paul", "test"), 1));

        MatchesViewAdapter adapter = new MatchesViewAdapter(matches, context);

        assertEquals(3, adapter.getItemCount());
    }
}
