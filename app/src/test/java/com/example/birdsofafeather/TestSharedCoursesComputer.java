package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.db.Course;

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

        courses1.add(new Course(1, 1, "2020", "Fall", "CSE", "110"));
        courses1.add(new Course(2, 1, "2020", "Fall", "CSE", "105"));
        courses1.add(new Course(3, 1, "2020", "Fall", "CSE", "100"));

        courses2.add(new Course(4, 2, "2020", "Fall", "CSE", "110"));
        courses2.add(new Course(5, 2, "2021", "Fall", "CSE", "105"));
        courses2.add(new Course(6, 2, "2020", "Winter", "CSE", "100"));

        assertEquals(Utilities.getSharedCourses(courses1, courses2).size(), 1);
        assertEquals(Utilities.getNumSharedCourses(courses1, courses2), 1);
        Course shared = Utilities.getSharedCourses(courses1, courses2).get(0);
        assertEquals(shared.getSubject(), "CSE");
        assertEquals(shared.getNumber(), "110");
        assertEquals(shared.getQuarter(), "Fall");
        assertEquals(shared.getYear(), "2020");
    }


    @Test
    public void TestCompareCourses() {
        Course c1 = new Course(1, 1, "2020", "Fall", "CSE", "110");
        Course c2 = new Course(1, 1, "2020", "Fall", "CSE", "110");
        assertTrue(Utilities.compareCourses(c1, c2));

        Course c3 = new Course(2, 1, "2020", "Fall", "CSE", "110");
        assertTrue(Utilities.compareCourses(c1, c3));

        Course c4 = new Course(1, 2, "2020", "Fall", "CSE", "110");
        assertTrue(Utilities.compareCourses(c1, c4));

        Course c5 = new Course(2, 2, "2020", "Fall", "CSE", "110");
        assertTrue(Utilities.compareCourses(c1, c5));

        Course c6 = new Course(1, 1, "2021", "Fall", "CSE", "110");
        assertFalse(Utilities.compareCourses(c1, c6));
    }
}
