package com.example.birdsofafeather;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;

import static org.junit.Assert.assertEquals;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TestCoursesDB {

    public Context context = ApplicationProvider.getApplicationContext();
    public AppDatabase db = AppDatabase.useTestSingleton(context);

    //Testing if courses are inserted into DB
    @Test
    public void testCourseInDB() {
        //insert user profile
        Profile p1 = new Profile("Name1", "test_photo.png");
        db.profileDao().insert(p1);

        assertEquals(1, db.profileDao().count());

        //insert 6 courses
        Course c1 = new Course(p1.getProfileId(), "2021", "Fall", "CSE", "11", "Tiny (<40)");
        Course c2 = new Course(p1.getProfileId(), "2020", "Spring", "TDDR", "127", "Small (40-75)");
        Course c3 = new Course(p1.getProfileId(), "2024", "Winter", "COGS", "108", "Medium (75-150)");
        Course c4 = new Course(p1.getProfileId(), "1999", "Special Summer Session", "MATH", "20C", "Large (150-250)");
        Course c5 = new Course(p1.getProfileId(), "1971", "Summer Session 1", "CSE", "110", "Huge (250-400)");
        Course c6 = new Course(p1.getProfileId(), "2013", "Summer Session 2", "POLI", "27", "Gigantic (400+)");

        db.courseDao().insert(c1);
        db.courseDao().insert(c2);
        db.courseDao().insert(c3);

        //ensure that number of courses is correct
        assertEquals(3, db.courseDao().count());

        db.courseDao().insert(c4);
        db.courseDao().insert(c5);
        db.courseDao().insert(c6);

        assertEquals(6, db.courseDao().count());

        //verify that each course in the db has the correct fields information
        List<Course> courseList = db.courseDao().getCoursesByProfileId(p1.getProfileId());


        assertEquals("11", courseList.get(0).getNumber());
        assertEquals("Fall", courseList.get(0).getQuarter());
        assertEquals("CSE", courseList.get(0).getSubject());
        assertEquals("2021", courseList.get(0).getYear());
        assertEquals("Tiny (<40)", courseList.get(0).getClassSize());

        assertEquals("127", courseList.get(1).getNumber());
        assertEquals("Spring", courseList.get(1).getQuarter());
        assertEquals("TDDR", courseList.get(1).getSubject());
        assertEquals("2020", courseList.get(1).getYear());
        assertEquals("Small (40-75)", courseList.get(1).getClassSize());

        assertEquals("108", courseList.get(2).getNumber());
        assertEquals("Winter", courseList.get(2).getQuarter());
        assertEquals("COGS", courseList.get(2).getSubject());
        assertEquals("2024", courseList.get(2).getYear());
        assertEquals("Medium (75-150)", courseList.get(2).getClassSize());

        assertEquals("20C", courseList.get(3).getNumber());
        assertEquals("Special Summer Session", courseList.get(3).getQuarter());
        assertEquals("MATH", courseList.get(3).getSubject());
        assertEquals("1999", courseList.get(3).getYear());
        assertEquals("Large (150-250)", courseList.get(3).getClassSize());

        assertEquals("110", courseList.get(4).getNumber());
        assertEquals("Summer Session 1", courseList.get(4).getQuarter());
        assertEquals("CSE", courseList.get(4).getSubject());
        assertEquals("1971", courseList.get(4).getYear());
        assertEquals("Huge (250-400)", courseList.get(4).getClassSize());

        assertEquals("27", courseList.get(5).getNumber());
        assertEquals("Summer Session 2", courseList.get(5).getQuarter());
        assertEquals("POLI", courseList.get(5).getSubject());
        assertEquals("2013", courseList.get(5).getYear());
        assertEquals("Gigantic (400+)", courseList.get(5).getClassSize());

        //check if course dao methods are functional
        assertEquals(c5.getCourseId(), db.courseDao().getCourseId(p1.getProfileId(), "1971", "Summer Session 1", "CSE", "110", "Huge (250-400)"));
        assertEquals(c1.getCourseId(), db.courseDao().getCourseId(p1.getProfileId(), "2021", "Fall", "CSE", "11", "Tiny (<40)"));
        assertEquals(c3.getCourseId(), db.courseDao().getCourseId(p1.getProfileId(), "2024", "Winter", "COGS", "108", "Medium (75-150)"));
    }

    //Testing if courses are deleted from DB (used to check if status of course is correct in db)
    @Test
    public void deleteCourseInDB() {
        Profile p1 = new Profile("Name1", "test_photo.png");
        db.profileDao().insert(p1);

        assertEquals(1, db.profileDao().count());

        Course c1 = new Course(p1.getProfileId(), "2021", "Fall", "CSE", "11", "Tiny (<40)");
        Course c2 = new Course(p1.getProfileId(), "2020", "Spring", "TDDR", "127", "Small (40-75)");
        Course c3 = new Course(p1.getProfileId(), "2024", "Winter", "COGS", "108", "Medium (75-150)");


        db.courseDao().insert(c1);
        db.courseDao().insert(c2);
        db.courseDao().insert(c3);

        assertEquals(3, db.courseDao().count());
        db.courseDao().delete(c1);
        db.courseDao().delete(c2);
        assertEquals(1, db.courseDao().count());

    }
}
