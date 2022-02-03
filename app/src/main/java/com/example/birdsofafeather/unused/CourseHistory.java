package com.example.birdsofafeather.unused;

import androidx.annotation.NonNull;

import com.example.birdsofafeather.db.Course;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CourseHistory {
    private HashMap<Integer, Set<Course>> courseMap;
    private Set<Course> courses;

    public CourseHistory() {
        this.courseMap = new HashMap<>();
        this.courses = new HashSet<>();
    }

    public void addCourse(Course course) {
        if (this.courseMap.containsKey(course.hashCode())) {
            Set<Course> set = this.courseMap.get(course.hashCode());
            if (set == null) {
                set = new HashSet<>();
            }
            set.add(course);
        }
        else {
            Set<Course> set = new HashSet<>();
            set.add(course);
            this.courseMap.put(course.hashCode(), set);
        }
        this.courses.add(course);
    }

    public int size() {
        return this.courses.size();
    }

    public Set<Course> getCourses() {
        return this.courses;
    }
    
    public HashMap<Integer, Set<Course>> getCourseMap() {
        return this.courseMap;
    }

    @NonNull
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Integer key : this.courseMap.keySet()) {
            Set<Course> set = this.courseMap.get(key);
            if (set != null) {
                for (Course c : set) {
                    str.append(c.toString()).append(",");
                }
            }

        }

        return str.toString();
    }

    /*
    public Course deleteCourse(Course course) {
        if (!this.courseMap.containsKey(course.hashCode())) {
            return null;
        }
        else {
            Set<Course> courses = this.courseMap.get(course.hashCode());
            Course removed = null;
            for (Course c : courses) {
                if (course.equals(c)) {
                    size--;
                    removed = c;
                    courses.remove(c);
                }
            }

            return removed;
        }
    }
    */
}
