//
//package com.example.birdsofafeather;
//
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.test.core.app.ApplicationProvider;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//import org.junit.runner.RunWith;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//import static org.hamcrest.MatcherAssert.assertThat;
//
//import androidx.test.espresso.*;
//import androidx.test.core.app.ActivityScenario;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.action.ViewActions.typeText;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//
//import android.content.Context;
//
//import android.view.View;
//
//import com.example.birdsofafeather.db.AppDatabase;
//import com.example.birdsofafeather.db.Course;
//import com.example.birdsofafeather.db.CourseDao;
//import com.example.birdsofafeather.db.Profile;
//import com.example.birdsofafeather.db.ProfileDao;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//
//@RunWith(AndroidJUnit4.class)
//public class TestViewProfileActivity {
//    private AppDatabase db;
//    private CourseDao courseDao;
//    private ProfileDao profileDao;
//    private Profile testProfile;
//    private Course course1, course2, course3, course4, course5;
//
//    @Rule
//    public ActivityScenarioRule<ViewProfileActivity> scenarioRule =
//            new ActivityScenarioRule<>(ViewProfileActivity.class);
//
//    @Before
//    public void setupTestDatabase(){
//        course1 = new Course(10, 2,"2019","Fall","CSE","11");
//        course1 = new Course(20, 2,"2021","Fall","CSE","100");
//        course1 = new Course(30, 2,"2020","Winter","CSE","30");
//        course1 = new Course(40, 2,"2020","Winter","MATH","20D");
//        course1 = new Course(50, 2,"2019","Spring","CSE","20");
//        testProfile = new Profile(2,"John","valid_url");
//
//        Context context = ApplicationProvider.getApplicationContext();
//        db = AppDatabase.useTestSingleton(context);
//    }
//
//    @Test
//    public void testViewProfileActivitySingleMatchDisplayed() {
//
//        /**insert single course1 to database with matching profileId*/
//
//        try (ActivityScenario<ViewProfileActivity> scenario = scenarioRule.getScenario()) {
//
//            scenario.onActivity(activity -> {
//                RecyclerView rv = activity.findViewById(R.id.viewprofile_shared_courses);
//
//                    RecyclerView.Adapter adapter = rv.getAdapter();
//                    adapter.getItemCount();
//                    View course1View = rv.getChildAt(0);
//
//                    //examine and assert that each item is true
//
//
//                });
//        }
//    }
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
//    public void testCoursesScroll(){
//
//    }
//
//
//}
