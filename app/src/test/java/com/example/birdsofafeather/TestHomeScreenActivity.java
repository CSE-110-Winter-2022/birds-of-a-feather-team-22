package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Pair;

import androidx.test.core.app.ApplicationProvider;

import com.example.birdsofafeather.db.Profile;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class TestHomeScreenActivity {
    @Test
    public void matchesViewAdapterTest()  {
        Context context = ApplicationProvider.getApplicationContext();
        List<Pair<Profile, Integer>> matches = new ArrayList<>();
        Profile profile1 = new Profile(1, "Bob", "test");
        Profile profile2 = new Profile(2, "Sam", "test");
        Profile profile3 = new Profile(3, "Paul", "test");

        matches.add(new Pair<Profile, Integer>(profile1, 1));
        matches.add(new Pair<Profile, Integer>(profile2, 1));
        matches.add(new Pair<Profile, Integer>(profile3, 1));
        MatchesViewAdapter adapter = new MatchesViewAdapter(matches, context);

        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void sessionsAdapterTest(){

    }

    @Test
    public void sessionCoursesAdapterTest(){

    }
}
