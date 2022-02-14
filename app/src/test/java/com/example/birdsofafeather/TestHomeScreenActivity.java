package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.CourseDao;
import com.example.birdsofafeather.db.Profile;
import com.example.birdsofafeather.db.ProfileDao;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.LooperMode;

import java.util.ArrayList;
import java.util.List;

@LooperMode(LooperMode.Mode.PAUSED)
@RunWith(AndroidJUnit4.class)
public class TestHomeScreenActivity {
    private AppDatabase db;
    private CourseDao courseDao;
    private ProfileDao profileDao;
    private Profile testProfile;
    private Course course1, course2, course3, course4, course5, course6, course7, course8, course9, course10;
    private TextView course_year, course_quarter, course_subject, course_number;
    private View selectedView;

    @Before
    public void setUp(){
        Context context = ApplicationProvider.getApplicationContext();
        db = AppDatabase.useTestSingleton(context);
        testProfile = new Profile(1,"Drake","valid");

        profileDao = db.profileDao();
        courseDao = db.courseDao();
        profileDao.insert(testProfile);
    }

    @Test
    public void testDummy(){
        ActivityScenario<HomeScreenActivity> homeScreen = ActivityScenario.launch(HomeScreenActivity.class);


        homeScreen.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.matches_view);

            /*test course1*/
            selectedView = recyclerView.getChildAt(0);
            View test = selectedView.findViewById(R.id.match_layout);


        });
    }
}
