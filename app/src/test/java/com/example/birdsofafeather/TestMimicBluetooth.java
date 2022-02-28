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

    //Testing bluetooth connectivity functionality
    //(Grabbing data with respect to remote id's and updating accordingly to local db)
    @Test
    public void bluetoothConnectivity(){
        //Local current user profile
        Profile p1 = new Profile(1, "Name1", "test_photo.png");
        db.profileDao().insert(p1);

        assertEquals(1, db.profileDao().count());

        //Adding courses for current user
        Course c1 = new Course(1, 1, "2021", "Fall", "CSE", "11");
        Course c2 = new Course(2, 1 ,"2020", "Spring", "TDDR", "127");
        Course c3 = new Course(3, 1, "2024", "Winter", "COGS", "108");
        db.courseDao().insert(c1);
        db.courseDao().insert(c2);
        db.courseDao().insert(c3);

        assertEquals(3, db.courseDao().count());

        //Another remote user profile (matching id because it is in respect to the remote users' local db)
        Profile p2 = new Profile(1, "Name1", "test_photo.png");

        List<Course> listPerson2 = new ArrayList<>();
        Course c11 = new Course(1, 1, "2021", "Fall", "CSE", "11");
        Course c22 = new Course(2, 1 ,"2020", "Spring", "TDDR", "127");
        Course c33 = new Course(3, 1, "2024", "Winter", "COGS", "108");
        Course c44 = new Course(4, 1, "2019", "Fall", "COGS", "108");

        listPerson2.add(c11);
        listPerson2.add(c22);
        listPerson2.add(c33);
        listPerson2.add(c44);

        //updates new id for profile and its courses in relation to current DB
        addUsersToDB(db, p2, listPerson2);

        //check if adding other profiles matches with bluetooth implementation methods
        assertEquals(7, db.courseDao().count());
        assertEquals(2,db.profileDao().count());
        assertEquals(2, db.profileDao().maxId());


        //Another remote user profile (matching id because it is in respect to the remote users' local db)
        Profile p3 = new Profile(1, "Name1", "test_photo.png");

        List<Course> listPerson3 = new ArrayList<>();
        Course c111 = new Course(1, 1, "2021", "Fall", "CSE", "11");
        Course c222 = new Course(2, 1 ,"2020", "Spring", "CSE", "12");

        listPerson3.add(c111);
        listPerson3.add(c222);

        //updates new id for profile and its courses in relation to current DB

        addUsersToDB(db, p3, listPerson3);

        //check if new course id correspond to new profile id
        //current user = p1, remote = p2, p3
        List<Course> courses_p1 = db.courseDao().getCoursesByProfileId(1);
        assertEquals(3, courses_p1.size());
        assertEquals(1, courses_p1.get(0).getCourseId());
        assertEquals(2, courses_p1.get(1).getCourseId());
        assertEquals(3, courses_p1.get(2).getCourseId());

        List<Course> courses_p2 = db.courseDao().getCoursesByProfileId(2);
        assertEquals(4, courses_p2.size());
        assertEquals(4, courses_p2.get(0).getCourseId());
        assertEquals(5, courses_p2.get(1).getCourseId());
        assertEquals(6, courses_p2.get(2).getCourseId());
        assertEquals(7, courses_p2.get(3).getCourseId());

        List<Course> courses_p3 = db.courseDao().getCoursesByProfileId(3);
        assertEquals(2, courses_p3.size());
        assertEquals(8, courses_p3.get(0).getCourseId());
        assertEquals(9, courses_p3.get(1).getCourseId());

        //check if total course entities is 9
        assertEquals(9, db.courseDao().count());

        //check if max course id is 9
        assertEquals(9, db.courseDao().maxId());

        //check the number of profiles
        assertEquals(3, db.profileDao().count());

        //check max profile id is 9
        assertEquals(3, db.profileDao().maxId());

        //get shared courses between person 1 and person 2
        List<Course> shared1_2 = getSharedCourses(db, 1, 2);
        assertEquals(3, shared1_2.size());

        //check if profile id's have conflict
        assertEquals(3, db.profileDao().count());
        List<Profile> profileList = db.profileDao().getAllProfiles();

        //check if profile id's were updated
       assertEquals(1, profileList.get(0).getProfileId());
       assertEquals(2, profileList.get(1).getProfileId());
       assertEquals(3, profileList.get(2).getProfileId());
    }

/******************* Wrapper class to be used with Bluetooth ************************************/
    //update each profile id and their courses to avoid id conflict
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

    //get the list of shared courses
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

    //comparator function for courses
    public boolean compareCourses(Course c1, Course c2) {
        return c1.getYear().equals(c2.getYear()) && c1.getQuarter().equals(c2.getQuarter()) &&
                c1.getSubject().equals(c2.getSubject()) && c1.getNumber().equals(c2.getNumber());
    }
}
/******************* Wrapper class to be used with Bluetooth ************************************/