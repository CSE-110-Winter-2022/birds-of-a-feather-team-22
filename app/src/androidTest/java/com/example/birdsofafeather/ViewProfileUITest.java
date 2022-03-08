package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ViewProfileUITest {

    @Test
    public void FirstLaunchViewTest() {
        ActivityScenario<MatchProfileActivity> viewProfileScreen = ActivityScenario.launch(MatchProfileActivity.class);
        viewProfileScreen.onActivity(activity -> {
            ImageView viewProfilePhoto = activity.findViewById(R.id.viewprofile_photo);
            TextView nameView = activity.findViewById(R.id.viewprofile_name);
            RecyclerView viewProfileSharedCourses = activity.findViewById(R.id.viewprofile_shared_courses);

            assertEquals(viewProfilePhoto.getVisibility(), View.VISIBLE);
            assertEquals(nameView.getVisibility(), View.VISIBLE);
            assertEquals(viewProfileSharedCourses.getVisibility(), View.VISIBLE);
        });
    }
}
