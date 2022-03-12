package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TestSharedCoursesComputer {

    @Test
    public void TestComputeSharedCourses() {
        List<Course> courses1 = new ArrayList<>();
        List<Course> courses2 = new ArrayList<>();
        Profile p1 = new Profile("name1", "test_photo1");
        Profile p2 = new Profile("name2", "test_photo1");


        courses1.add(new Course(p1.getProfileId(), "2020", "Fall", "CSE", "110", "Gigantic (400+)"));
        courses1.add(new Course(p1.getProfileId(), "2020", "Fall", "CSE", "105", "Huge (250-400)"));
        courses1.add(new Course(p1.getProfileId(), "2020", "Fall", "CSE", "100", "Large (150-250)"));

        courses2.add(new Course(p2.getProfileId(), "2020", "Fall", "CSE", "110", "Gigantic (400+)"));
        courses2.add(new Course(p2.getProfileId(), "2021", "Fall", "CSE", "105", "Huge (250-400)"));
        courses2.add(new Course(p2.getProfileId(), "2020", "Winter", "CSE", "100", "Large (150-250)"));

        assertEquals(1, Utilities.getSharedCourses(courses1, courses2).size());
        assertEquals(1, Utilities.getNumSharedCourses(courses1, courses2));
        Course shared = Utilities.getSharedCourses(courses1, courses2).get(0);
        assertEquals("CSE", shared.getSubject());
        assertEquals("110", shared.getNumber());
        assertEquals("Fall", shared.getQuarter());
        assertEquals("2020", shared.getYear());
        assertEquals("Gigantic (400+)",shared.getClassSize());
    }


    @Test
    public void TestCompareCourses() {
        Profile p1 = new Profile("name1", "test_photo");
        Course c1 = new Course(p1.getProfileId(), "2020", "Fall", "CSE", "110", "Medium (75-150)");
        Course c2 = new Course(p1.getProfileId(), "2020", "Fall", "CSE", "110", "Medium (75-150)");
        assertTrue(Utilities.compareCourses(c1, c2));

        Course c3 = new Course(p1.getProfileId(), "2020", "Fall", "CSE", "110", "Medium (75-150)");
        assertTrue(Utilities.compareCourses(c1, c3));

        Course c4 = new Course(p1.getProfileId(), "2020", "Fall", "CSE", "110", "Medium (75-150)");
        assertTrue(Utilities.compareCourses(c1, c4));

        Course c5 = new Course(p1.getProfileId(), "2020", "Fall", "CSE", "110", "Medium (75-150)");
        assertTrue(Utilities.compareCourses(c1, c5));

        Course c6 = new Course(p1.getProfileId(), "2021", "Fall", "CSE", "110", "Medium (75-150)");
        assertFalse(Utilities.compareCourses(c1, c6));
    }
}
