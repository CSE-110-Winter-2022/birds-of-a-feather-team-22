//package com.example.birdsofafeather;
//
//import static androidx.test.espresso.Espresso.closeSoftKeyboard;
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.clearText;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.action.ViewActions.typeText;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static org.junit.Assert.*;
//
//import android.content.Intent;
//
//import androidx.test.core.app.ActivityScenario;
//import androidx.test.core.app.ApplicationProvider;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//import com.example.birdsofafeather.db.AppDatabase;
//import com.example.birdsofafeather.db.Profile;
//
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//@RunWith(AndroidJUnit4.class)
//public class TestFavorites {
//
//    static Intent intent;
//    static {
//        intent = new Intent(ApplicationProvider.getApplicationContext(), MatchActivity.class);
//        intent.putExtra("isTesting", true);
//    }
//
//    @Rule
//    public ActivityScenarioRule<MatchActivity> scenarioRule = new ActivityScenarioRule<>(intent);
//    public AppDatabase db = AppDatabase.useTestSingleton(ApplicationProvider.getApplicationContext());
//
//    @Test
//    public void testClickFavorite() {
//        ActivityScenario<MatchActivity> scenario = scenarioRule.getScenario();
//
//        scenario.onActivity(activity -> {
//
//        });
//        assertEquals(0, db.profileDao().count());
//        db.profileDao().insert(new Profile("1", "Me", true, false, false, false));
//
//            onView(withId(R.id.star)).perform(click());
//
//    }
//}
