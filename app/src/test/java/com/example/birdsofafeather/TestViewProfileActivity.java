
package com.example.birdsofafeather;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.content.Intent;
import org.junit.runner.RunWith;

import androidx.test.core.app.ActivityScenario;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import android.os.Looper;
import android.view.View;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.JMock1Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.CourseDao;
import com.example.birdsofafeather.db.Profile;
import com.example.birdsofafeather.db.ProfileDao;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.robolectric.annotation.LooperMode;

import java.util.Map;

@LooperMode(LooperMode.Mode.PAUSED)
@RunWith(AndroidJUnit4.class)
public class TestViewProfileActivity {
    private AppDatabase db;
    private CourseDao courseDao;
    private ProfileDao profileDao;
    private Profile testProfile;
    private Course course1, course2, course3, course4, course5, course6, course7, course8, course9, course10;


    @Before
    public void setupTestDatabase(){
        course1 = new Course(10, 2,"2019","Fall","CSE","11");
        course2 = new Course(20, 2,"2021","Fall","CSE","100");
        course3 = new Course(30, 2,"2020","Winter","CSE","30");
        course4 = new Course(40, 2,"2020","Winter","MATH","20D");
        course5 = new Course(50, 2,"2019","Spring","CSE","20");
        course6 = new Course(10, 1,"2019","Fall","CSE","11");
        course7 = new Course(20, 1,"2021","Fall","CSE","100");
        course8 = new Course(30, 1,"2020","Winter","CSE","30");
        course9 = new Course(40, 1,"2020","Winter","MATH","20D");
        course10 = new Course(50, 1,"2019","Spring","CSE","20");
        testProfile = new Profile(1,"John","valid_url");
        Profile myProfile = new Profile(2, "Drake", "Valid");

        Context context = ApplicationProvider.getApplicationContext();
        db = AppDatabase.useTestSingleton(context);

        profileDao = db.profileDao();
        courseDao = db.courseDao();

        profileDao.insert(testProfile);
        profileDao.insert(myProfile);
        courseDao.insert(course1);

    }


    @Test
    public void testViewProfileActivitySingleMatchDisplayed() {

        /**insert single course1 to database with matching profileId*/

        ActivityScenario<ViewProfileActivity> viewProfile = ActivityScenario.launch(ViewProfileActivity.class);


        viewProfile.onActivity(activity -> {

            onView(withId(R.id.viewprofiile_name)).check(matches(withText("John")));
            //onView(withText("CSE")).check(matches(isDisplayed()));

            RecyclerView recyclerView = activity.findViewById(R.id.viewprofile_shared_courses);
            View v = recyclerView.getLayoutManager().findViewByPosition(0);
            assertEquals(v, null);
        });
    }


}
//
//    @Test
//    public void testViewProfileActivityMultipleMatchesDisplayed(){
//
//        ActivityScenario<ViewProfileActivity> scenario = scenarioRule.getScenario()) {
//
//            scenario.onActivity(activity -> {
//
//            });
//        }
//    }
//
//    @Test
//    public void testCoursesExistInDatabase(){
//
//    }
//
//    @Test
//   public void testCoursesScroll(){

//    }



