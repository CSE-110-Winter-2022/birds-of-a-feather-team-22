package com.example.birdsofafeather;

import android.content.Context;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.CourseDao;
import com.example.birdsofafeather.db.Profile;
import com.example.birdsofafeather.db.ProfileDao;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseTracker {
    private AppDatabase db;
    private CourseDao courseDao;
    private ProfileDao profileDao;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Profile> future;

    public DatabaseTracker() {
        db = AppDatabase.singleton(BoFApplication.getContext());
        courseDao = db.courseDao();
        profileDao = db.profileDao();
    }

    public int getNumSharedCourses(int profileId) {
        return getSharedCourses(profileId).size();
    }

    public List<Course> getSharedCourses(int profileId) {
        List<Course> myCourses = courseDao.getCoursesByProfileId(1);
        List<Course> theirCourses = courseDao.getCoursesByProfileId(profileId);

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

    public void addUsersToDB(Profile profile, List<Course> courses) {
        this.future = backgroundThreadExecutor.submit(() -> {
            int newProfileId = profileDao.maxId() + 1;
            profile.setProfileId(newProfileId);
            profileDao.insert(profile);

            for (int i = 0; i < courses.size(); i++) {
                Course c = courses.get(i);
                c.setCourseId(courseDao.maxId() + 1);
                c.setProfileId(newProfileId);
                courseDao.insert(c);
            }

            return null;
        });
    }

    public Profile getUserProfile() {
        return profileDao.getProfile(1);
    }

    public List<Course> getUserCourses() {
        return courseDao.getCoursesByProfileId(1);
    }
}
