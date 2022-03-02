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

    //Test shared courses with entities in DB
    @Test
    public void getSharedCourses(){
        Profile p1 = new Profile("Name1", "test_photo.png");
        db.profileDao().insert(p1);

        assertEquals(1, db.profileDao().count());

        Course c1 = new Course(p1.getProfileId(), "2021", "Fall", "CSE", "11", "Small (40-75)");
        Course c2 = new Course(p1.getProfileId(),"2020", "Spring", "TDDR", "127", "Medium (75-150)");
        Course c3 = new Course(p1.getProfileId(), "2024", "Winter", "COGS", "108", "Huge (250-400)");

        db.courseDao().insert(c1);
        db.courseDao().insert(c2);
        db.courseDao().insert(c3);

        Profile p2 = new Profile("Name2", "test_photo_2.png");
        db.profileDao().insert(p2);

        assertEquals(2, db.profileDao().count());

        Course c11 = new Course(p2.getProfileId(), "2021", "Fall", "CSE", "11", "Small (40-75)");
        Course c22 = new Course(p2.getProfileId() ,"2019", "Winter", "CSE", "30", "Gigantic (400+)");
        Course c33 = new Course(p2.getProfileId(), "2024", "Winter", "COGS", "108", "Huge (250-400)");
        Course c44 = new Course(p2.getProfileId(), "2020", "Spring", "WCWP", "10B", "Tiny (<40)");

        db.courseDao().insert(c11);
        db.courseDao().insert(c22);
        db.courseDao().insert(c33);
        db.courseDao().insert(c44);

        assertEquals(7, db.courseDao().count());

        Profile p3 = new Profile("Name3", "test_photo_3.png");
        db.profileDao().insert(p3);

        assertEquals(3, db.profileDao().count());

        Course c111 = new Course(p3.getProfileId(), "2021", "Fall", "CSE", "11", "Small (40-75)");

        db.courseDao().insert(c111);

        assertEquals(8, db.courseDao().count());

        //get list of shared courses between 2 users
        List<Course> sharedCourse1_2 = getSharedCourses(db, p1.getProfileId(), p2.getProfileId());
        assertEquals(2, sharedCourse1_2.size());

        assertEquals("2021", sharedCourse1_2.get(0).getYear());
        assertEquals("CSE", sharedCourse1_2.get(0).getSubject());
        assertEquals("Fall", sharedCourse1_2.get(0).getQuarter());
        assertEquals("11", sharedCourse1_2.get(0).getNumber());
        assertEquals("Small (40-75)", sharedCourse1_2.get(0).getClassSize());

        assertEquals("2024", sharedCourse1_2.get(1).getYear());
        assertEquals("COGS", sharedCourse1_2.get(1).getSubject());
        assertEquals("Winter", sharedCourse1_2.get(1).getQuarter());
        assertEquals("108", sharedCourse1_2.get(1).getNumber());
        assertEquals("Huge (250-400)", sharedCourse1_2.get(1).getClassSize());

        List<Course> sharedCourse1_3 = getSharedCourses(db, p1.getProfileId(), p3.getProfileId());
        assertEquals(1, sharedCourse1_3.size());

        assertEquals("2021", sharedCourse1_3.get(0).getYear());
        assertEquals("CSE", sharedCourse1_3.get(0).getSubject());
        assertEquals("Fall", sharedCourse1_3.get(0).getQuarter());
        assertEquals("11", sharedCourse1_3.get(0).getNumber());
        assertEquals("Small (40-75)", sharedCourse1_3.get(0).getClassSize());

    }

    /******************* Wrapper class to be used with Bluetooth **********************************/
    public List<Course> getSharedCourses(AppDatabase db, String myProfileId, String otherProfileId) {
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
                c1.getSubject().equals(c2.getSubject()) && c1.getNumber().equals(c2.getNumber())
                && c1.getClassSize().equals(c2.getClassSize());
    }
    /******************* Wrapper class to be used with Bluetooth **********************************/
}
