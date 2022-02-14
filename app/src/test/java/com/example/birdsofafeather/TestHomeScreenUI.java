/*
package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.db.Profile;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TestHomeScreenUI {
    @Rule
    public ActivityScenarioRule<HomeScreenActivity> scenarioRule = new ActivityScenarioRule<>(HomeScreenActivity.class);
    private List<Profile> data;
    private MatchesViewAdapter match;
    @Test
    public void testURLChecker(){
        ActivityScenario<HomeScreenActivity> scenario = scenarioRule.getScenario();
        Context context = ApplicationProvider.getApplicationContext();
        scenario.onActivity(activity -> {
            Profile TestPersonValid = new Profile(1,"Fernando","https://stthomassource.com/wp-content/uploads/sites/2/2019/05/Fernando-Melchior.jpg");
            Profile TestPersonEmpty = new Profile(2,"Fernando","");
            Profile TestPersonNonPhoto = new Profile(3,"Fernando","https://www.google.com/");
            Profile TestPersonNonURL = new Profile(4,"Fernando","i.really.hope.this.is.a.pic");
            Profile TestPersonMissingScheme = new Profile(5,"Fernando","https:stthomassource.com/wp-content/uploads/sites/2/2019/05/Fernando-Melchior.jpg");
            data = new ArrayList<>();
            data.add(TestPersonValid);
            data.add(TestPersonEmpty);
            data.add(TestPersonNonURL);
            data.add(TestPersonNonPhoto);
            data.add(TestPersonMissingScheme);
            match = new MatchesViewAdapter(data,context);
            MatchesViewAdapter.ViewHolder view = match.onCreateViewHolder(this,0);


            
            
        });
    }
}
*/