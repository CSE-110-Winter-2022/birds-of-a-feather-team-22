
package com.example.birdsofafeather;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.runner.RunWith;
import androidx.test.core.app.ActivityScenario;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.CourseDao;
import com.example.birdsofafeather.db.Profile;
import com.example.birdsofafeather.db.ProfileDao;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.annotation.LooperMode;

@LooperMode(LooperMode.Mode.PAUSED)
@RunWith(AndroidJUnit4.class)
public class TestViewProfileActivity {
    private AppDatabase db;
    private CourseDao courseDao;
    private ProfileDao profileDao;
    private Profile testProfile;
    private Course course1, course2, course3, course4, course5, course6, course7, course8, course9, course10;
    private TextView course_year, course_quarter, course_subject, course_number;
    private View selectedView;

    @Before
    public void setupTestDatabase(){
        course1 = new Course(10, 2,"2019","Fall","CSE","11");
        course2 = new Course(20, 2,"2021","Fall","CSE","100");
        course3 = new Course(30, 2,"2020","Winter","CSE","30");
        course4 = new Course(40, 2,"2020","Winter","MATH","20D");
        course5 = new Course(50, 2,"2019","Spring","CSE","20");
        course6 = new Course(10, 1,"2019","Fall","CSE","11");
        course7 = new Course(20, 1,"2021","Fall","CSE","100");
        course8 = new Course(30, 1,"2020","Winter","CSE","30");
        course9 = new Course(40, 1,"2020","Winter","MATH","20D");
        course10 = new Course(50, 1,"2019","Spring","CSE","20");
        testProfile = new Profile(1,"John","valid_url");
        Profile myProfile = new Profile(2, "Drake", "Valid");

        Context context = ApplicationProvider.getApplicationContext();
        db = AppDatabase.useTestSingleton(context);

        profileDao = db.profileDao();
        courseDao = db.courseDao();

        profileDao.insert(testProfile);
        profileDao.insert(myProfile);
        courseDao.insert(course1);
        courseDao.insert(course2);
        courseDao.insert(course3);
        courseDao.insert(course4);
        courseDao.insert(course5);
        courseDao.insert(course6);
        courseDao.insert(course7);
        courseDao.insert(course8);
        courseDao.insert(course9);
        courseDao.insert(course10);

    }


    @Test
    public void testSharedCoursesDisplayed() {

        /**insert single course1 to database with matching profileId*/

        ActivityScenario<ViewProfileActivity> viewProfile = ActivityScenario.launch(ViewProfileActivity.class);


        viewProfile.onActivity(activity -> {
            /*check match name*/
            onView(withId(R.id.viewprofiile_name)).check(matches(withText("John")));

            RecyclerView recyclerView = activity.findViewById(R.id.viewprofile_shared_courses);

            /*test course1*/
            selectedView = recyclerView.getChildAt(0);
            course_year = (TextView) selectedView.findViewById(R.id.course_year_row_textview);
            course_quarter = (TextView) selectedView.findViewById(R.id.course_quarter_row_textview);
            course_subject = (TextView) selectedView.findViewById(R.id.course_subject_row_textview);
            course_number = (TextView) selectedView.findViewById(R.id.course_number_row_textview);

            assertEquals(course_year.getText(), "2019");
            assertEquals(course_quarter.getText(), "Fall");
            assertEquals(course_subject.getText(), "CSE");
            assertEquals(course_number.getText(), "11");

            /*test course2*/
            selectedView = recyclerView.getChildAt(1);
            course_year = (TextView) selectedView.findViewById(R.id.course_year_row_textview);
            course_quarter = (TextView) selectedView.findViewById(R.id.course_quarter_row_textview);
            course_subject = (TextView) selectedView.findViewById(R.id.course_subject_row_textview);
            course_number = (TextView) selectedView.findViewById(R.id.course_number_row_textview);

            assertEquals(course_year.getText(), "2021");
            assertEquals(course_quarter.getText(), "Fall");
            assertEquals(course_subject.getText(), "CSE");
            assertEquals(course_number.getText(), "100");

            /*test third course*/
            selectedView = recyclerView.getChildAt(2);
            course_year = (TextView) selectedView.findViewById(R.id.course_year_row_textview);
            course_quarter = (TextView) selectedView.findViewById(R.id.course_quarter_row_textview);
            course_subject = (TextView) selectedView.findViewById(R.id.course_subject_row_textview);
            course_number = (TextView) selectedView.findViewById(R.id.course_number_row_textview);

            assertEquals(course_year.getText(), "2020");
            assertEquals(course_quarter.getText(), "Winter");
            assertEquals(course_subject.getText(), "CSE");
            assertEquals(course_number.getText(), "30");

            /*test fourth course*/
            selectedView = recyclerView.getChildAt(3);
            course_year = (TextView) selectedView.findViewById(R.id.course_year_row_textview);
            course_quarter = (TextView) selectedView.findViewById(R.id.course_quarter_row_textview);
            course_subject = (TextView) selectedView.findViewById(R.id.course_subject_row_textview);
            course_number = (TextView) selectedView.findViewById(R.id.course_number_row_textview);

            assertEquals(course_year.getText(), "2020");
            assertEquals(course_quarter.getText(), "Winter");
            assertEquals(course_subject.getText(), "MATH");
            assertEquals(course_number.getText(), "20D");

            /*test fifth course*/
            selectedView = recyclerView.getChildAt(4);
            course_year = (TextView) selectedView.findViewById(R.id.course_year_row_textview);
            course_quarter = (TextView) selectedView.findViewById(R.id.course_quarter_row_textview);
            course_subject = (TextView) selectedView.findViewById(R.id.course_subject_row_textview);
            course_number = (TextView) selectedView.findViewById(R.id.course_number_row_textview);

            assertEquals(course_year.getText(), "2019");
            assertEquals(course_quarter.getText(), "Spring");
            assertEquals(course_subject.getText(), "CSE");
            assertEquals(course_number.getText(), "20");

        });
    }


}


