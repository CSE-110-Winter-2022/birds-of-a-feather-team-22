package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.birdsofafeather.db.Profile;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

@LargeTest
public class HomeScreenUITest {
//
    @Test
    public void startButtonPressWithStopFirstTimeTest() {
        ActivityScenario<HomeScreenActivity> homeScreen = ActivityScenario.launch(HomeScreenActivity.class);
        homeScreen.onActivity(activity -> {
            Button startButton = activity.findViewById(R.id.startButton);
            TextView findingMatchesView = activity.findViewById(R.id.textView6);
            TextView matchesFound = activity.findViewById(R.id.matchesFound);
            Button stopButton = activity.findViewById(R.id.stopButton);

            startButton.performClick();

            assertEquals(View.GONE, startButton.getVisibility());
            assertEquals(View.VISIBLE, findingMatchesView.getVisibility());
            assertEquals(View.GONE, matchesFound.getVisibility());
            assertEquals(View.VISIBLE, stopButton.getVisibility());
        });
    }

    @Test
    public void matchesViewAdapterTest() {
        Context context = ApplicationProvider.getApplicationContext();
        List<Profile> matches = new ArrayList<>();
        matches.add(new Profile(1, "Bob", "test"));
        matches.add(new Profile(2, "Sam", "test"));
        matches.add(new Profile(3, "Paul", "test"));

        MatchesViewAdapter adapter = new MatchesViewAdapter(matches, context);

        assertEquals(3, adapter.getItemCount());
    }
}
