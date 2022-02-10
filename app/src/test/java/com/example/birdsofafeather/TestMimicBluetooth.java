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
public class TestMimicBluetooth {

    public Context context = ApplicationProvider.getApplicationContext();
    public AppDatabase db = AppDatabase.useTestSingleton(context);

    @Test
    public void bluetoothConnectivity(){
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

        Profile p2 = new Profile(1, "Name1", "test_photo.png");

        List<Course> listPerson2 = new ArrayList<>();
        Course c11 = new Course(1, 1, "2021", "Fall", "CSE", "11");
        Course c22 = new Course(2, 1 ,"2020", "Spring", "TDDR", "127");
        Course c33 = new Course(3, 1, "2024", "Winter", "COGS", "108");

        listPerson2.add(c11);
        listPerson2.add(c22);
        listPerson2.add(c33);

        addUsersToDB(db, p2, listPerson2);

        assertEquals(db.courseDao().count(), 6);
        assertEquals(db.profileDao().count(),2);
        assertEquals(db.profileDao().maxId(), 2);

        List<Course> shared = getSharedCourses(db, 1, 2);
        assertEquals(shared.size(), 3);

        Profile p3 = new Profile(1, "Name1", "test_photo.png");

        List<Course> listPerson3 = new ArrayList<>();
        Course c111 = new Course(1, 1, "2021", "Fall", "CSE", "11");
        Course c222 = new Course(2, 1 ,"2020", "Spring", "CSE", "12");
        Course c333 = new Course(3, 1, "2019", "Fall", "COGS", "108");

        listPerson3.add(c111);
        listPerson3.add(c222);
        listPerson3.add(c333);

        addUsersToDB(db, p3, listPerson3);
        assertEquals(db.courseDao().count(), 9);
        assertEquals(db.profileDao().count(),3);
        assertEquals(db.profileDao().maxId(), 3);

        List<Course> shared1_3 = getSharedCourses(db, 1, 3);
        assertEquals(shared1_3.size(), 1);

    }

    public void addUsersToDB(AppDatabase db, Profile profile, List<Course> courses) {
        int newProfileId = db.profileDao().maxId() + 1;
        profile.setProfileId(newProfileId);
        db.profileDao().insert(profile);

        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            c.setCourseId(db.courseDao().maxId() + 1);
            c.setProfileId(newProfileId);
            db.courseDao().insert(c);
        }
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
