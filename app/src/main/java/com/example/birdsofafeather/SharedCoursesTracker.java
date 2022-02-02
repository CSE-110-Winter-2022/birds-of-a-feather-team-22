package com.example.birdsofafeather;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;

public class SharedCoursesTracker {


    public List<Course> getSharedCourses(CourseHistory ch1, CourseHistory ch2) {
        List<Course> shared = new ArrayList<>();
        for (Course c1 : ch1.getCourses()) {
            Set<Course> ch2Courses = ch2.getCourseMap().get(c1.hashCode());
            if (ch2Courses != null) {
                for (Course c2 : ch2Courses) {
                    if (c1.equals(c2)) {
                        shared.add(c1);
                    }
                }
            }

        }

        return shared;
    }
}
