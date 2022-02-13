package com.example.birdsofafeather;


import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TestSharedCoursesDB {

    public Context context = ApplicationProvider.getApplicationContext();
    public AppDatabase db = AppDatabase.useTestSingleton(context);

    @Test
    public void getSharedCourses(){
        Profile p1 = new Profile(1, "Name1", "test_photo.png");
        db.profileDao().insert(p1);

        assertEquals(db.profileDao().count(), 1);

        Course c1 = new Course(1, 1, "2021", "Fall", "CSE", "11");
        Course c2 = new Course(2, 1 ,"2020", "Spring", "TDDR", "127");
        Course c3 = new Course(3, 1, "2024", "Winter", "COGS", "108");

        db.courseDao().insert(c1);
        db.courseDao().insert(c2);
        db.courseDao().insert(c3);

        Profile p2 = new Profile(2, "Name2", "test_photo_2.png");
        db.profileDao().insert(p2);

        assertEquals(db.profileDao().count(), 2);

        Course c11 = new Course(4, 2, "2021", "Fall", "CSE", "11");
        Course c22 = new Course(5, 2 ,"2019", "Winter", "CSE", "30");
        Course c33 = new Course(6, 2, "2024", "Winter", "COGS", "108");
        Course c44 = new Course(7, 2, "2020", "Spring", "WCWP", "10B");

        db.courseDao().insert(c11);
        db.courseDao().insert(c22);
        db.courseDao().insert(c33);
        db.courseDao().insert(c44);

        assertEquals(db.courseDao().count(), 7);

        List<Course> sharedCourses = getSharedCourses(db, 1, 2);
        assertEquals(sharedCourses.size(), 2);

        assertEquals(sharedCourses.get(0).getYear(), "2021");
        assertEquals(sharedCourses.get(0).getSubject(), "CSE");
        assertEquals(sharedCourses.get(0).getQuarter(), "Fall");
        assertEquals(sharedCourses.get(0).getNumber(), "11");

        assertEquals(sharedCourses.get(1).getYear(), "2024");
        assertEquals(sharedCourses.get(1).getSubject(), "COGS");
        assertEquals(sharedCourses.get(1).getQuarter(), "Winter");
        assertEquals(sharedCourses.get(1).getNumber(), "108");



    }

    public List<Course> getSharedCourses(AppDatabase db, int myProfileId, int otherProfileId) {
        List<Course> myCourses = db.courseDao().getCoursesByProfileId(myProfileId);
        List<Course> theirCourses = db.courseDao().getCoursesByProfileId(otherProfileId);

        List<Course> sharedCourses = new ArrayList<>();

        for (Course myCourse : myCourses) {
            for (Course theirCourse : theirCourses) {
                if (compareCourses(myCourse, theirCourse)) {
                    sharedCourses.add(myCourse);
                }
            }
        }

        return sharedCourses;
    }

    public boolean compareCourses(Course c1, Course c2) {
        return c1.getYear().equals(c2.getYear()) && c1.getQuarter().equals(c2.getQuarter()) &&
                c1.getSubject().equals(c2.getSubject()) && c1.getNumber().equals(c2.getNumber());
    }
}
