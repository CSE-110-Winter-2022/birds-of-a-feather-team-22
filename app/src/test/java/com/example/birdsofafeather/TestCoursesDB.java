package com.example.birdsofafeather;

import android.app.Person;
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


    @Test
    public void testCourseInDB(){
        Profile p1 = new Profile(1, "Name1", "test_photo.png");
        db.profileDao().insert(p1);

        assertEquals(db.profileDao().count(), 1);

        Course c1 = new Course(1, 1, "2021", "Fall", "CSE", "11");
        Course c2 = new Course(2, 1 ,"2020", "Spring", "TDDR", "127");
        Course c3 = new Course(3, 1, "2024", "Winter", "COGS", "108");

        db.courseDao().insert(c1);
        db.courseDao().insert(c2);
        db.courseDao().insert(c3);

        assertEquals(db.courseDao().count(), 3);

        List<Course> courseList = db.courseDao().getCoursesByProfileId(1);

        assertEquals(courseList.get(0).getNumber(),"11");
        assertEquals(courseList.get(0).getQuarter(),"Fall");
        assertEquals(courseList.get(0).getSubject(),"CSE");
        assertEquals(courseList.get(0).getYear(),"2021");

        assertEquals(courseList.get(1).getNumber(),"127");
        assertEquals(courseList.get(1).getQuarter(),"Spring");
        assertEquals(courseList.get(1).getSubject(),"TDDR");
        assertEquals(courseList.get(1).getYear(),"2020");

        assertEquals(courseList.get(2).getNumber(),"108");
        assertEquals(courseList.get(2).getQuarter(),"Winter");
        assertEquals(courseList.get(2).getSubject(),"COGS");
        assertEquals(courseList.get(2).getYear(),"2024");


    }

    @Test
    public void deleteCourseInDB(){
        Profile p1 = new Profile(1, "Name1", "test_photo.png");
        db.profileDao().insert(p1);

        assertEquals(db.profileDao().count(), 1);

        Course c1 = new Course(1, 1, "2021", "Fall", "CSE", "11");
        Course c2 = new Course(2, 1 ,"2020", "Spring", "TDDR", "127");
        Course c3 = new Course(3, 1, "2024", "Winter", "COGS", "108");


        db.courseDao().insert(c1);
        db.courseDao().insert(c2);
        db.courseDao().insert(c3);

        assertEquals(db.courseDao().count(), 3);
        db.courseDao().delete(c1);
        db.courseDao().delete(c2);
        assertEquals(db.courseDao().count(), 1);

    }
}
