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
public class TestMimicBluetoothCourses {

    public Context context = ApplicationProvider.getApplicationContext();
    public AppDatabase db = AppDatabase.useTestSingleton(context);

    //Testing bluetooth connectivity functionality
    //(Grabbing data with respect to remote id's and updating accordingly to local db)
    @Test
    public void bluetoothConnectivity() {
        //Local current user profile
        Profile p1 = new Profile("Name1", "test_photo.png");
        db.profileDao().insert(p1);

        assertEquals(1, db.profileDao().count());

        //Adding courses for current user
        Course c1 = new Course(p1.getProfileId(), "2021","Fall", "CSE", "210", "Small (40-75)");
        Course c2 = new Course(p1.getProfileId(), "2020", "Spring", "TDDR", "127", "Tiny (<40)");
        Course c3 = new Course(p1.getProfileId(), "2022", "Winter", "CSE", "110", "Large (150-250)");
        db.courseDao().insert(c1);
        db.courseDao().insert(c2);
        db.courseDao().insert(c3);

        assertEquals(3, db.courseDao().count());

        //Receive nearby message
        String message = "a4ca50b6-941b-11ec-b909-0242ac120002,,,,\n" +
                "Bill,,,,\n" +
                "https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0,,,,\n" +
                "2021,Fall,CSE,210,Small (40-75)\n" +
                "2022,Winter,CSE,110,Large (150-250)";
        parseInfo(message);

        assertEquals(2, db.profileDao().count());
        assertEquals(5, db.courseDao().count());

        message = "b5zw60b6-941b-11ec-b909-0242ad120062,,,,\n" +
                "Jeff,,,,\n" +
                "https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0,,,,\n" +
                "2020,Spring,TDDR,127,Tiny (<40)";

        parseInfo(message);

        assertEquals(3, db.profileDao().count());
        assertEquals(6, db.courseDao().count());

        String p2 = "a4ca50b6-941b-11ec-b909-0242ac120002";
        String p3 = "b5zw60b6-941b-11ec-b909-0242ad120062";
        List<Course> shared_1_2 = getSharedCourses(db, p1.getProfileId(), p2);
        assertEquals(2, shared_1_2.size());

        assertEquals("2021", shared_1_2.get(0).getYear());
        assertEquals("Fall", shared_1_2.get(0).getQuarter());
        assertEquals("CSE", shared_1_2.get(0).getSubject());
        assertEquals("210", shared_1_2.get(0).getNumber());
        assertEquals("Small (40-75)", shared_1_2.get(0).getClassSize());

        assertEquals("2022", shared_1_2.get(1).getYear());
        assertEquals("Winter", shared_1_2.get(1).getQuarter());
        assertEquals("CSE", shared_1_2.get(1).getSubject());
        assertEquals("110", shared_1_2.get(1).getNumber());
        assertEquals("Large (150-250)", shared_1_2.get(1).getClassSize());


        List<Course> shared_1_3 = getSharedCourses(db, p1.getProfileId(), p3);
        assertEquals(1, shared_1_3.size());

        assertEquals("2020", shared_1_3.get(0).getYear());
        assertEquals("Spring", shared_1_3.get(0).getQuarter());
        assertEquals("TDDR", shared_1_3.get(0).getSubject());
        assertEquals("127", shared_1_3.get(0).getNumber());
        assertEquals("Tiny (<40)", shared_1_3.get(0).getClassSize());
    }

/******************* Wrapper class to be used with Bluetooth ************************************/

    //get the list of shared courses
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

    //comparator function for courses
    public boolean compareCourses(Course c1, Course c2) {
        return c1.getYear().equals(c2.getYear()) && c1.getQuarter().equals(c2.getQuarter()) &&
                c1.getSubject().equals(c2.getSubject()) && c1.getNumber().equals(c2.getNumber())
                && c1.getClassSize().equals(c2.getClassSize());
    }

    protected void parseInfo(String info) {
        String[] textBoxSeparated = info.split(",,,,");

        String UUID = textBoxSeparated[0];
        String userName = textBoxSeparated[1];
        String userThumbnail = textBoxSeparated[2];

        Profile profile = new Profile(UUID, userName, userThumbnail);
        db.profileDao().insert(profile);

        String[] classInfo = textBoxSeparated[3].split("\n");
        for (int i = 1; i < classInfo.length; i++) {
            String[] classInfoSeparated = classInfo[i].split(",");

            String year = classInfoSeparated[0];
            String quarter = classInfoSeparated[1];
            String subject = classInfoSeparated[2];
            String number = classInfoSeparated[3];
            String size = classInfoSeparated[4];

            Course course = new Course(UUID, year, quarter, subject, number, size);
            db.courseDao().insert(course);
        }
    }
}
///******************* Wrapper class to be used with Bluetooth ************************************/