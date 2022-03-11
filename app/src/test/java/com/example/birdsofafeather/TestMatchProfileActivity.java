package com.example.birdsofafeather;

        import androidx.test.core.app.ApplicationProvider;
        import androidx.test.ext.junit.runners.AndroidJUnit4;
        import org.junit.runner.RunWith;

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

        import java.util.ArrayList;
        import java.util.List;

@LooperMode(LooperMode.Mode.PAUSED)
@RunWith(AndroidJUnit4.class)
public class TestMatchProfileActivity {
    private AppDatabase db;
    private CourseDao courseDao;
    private ProfileDao profileDao;
    private Profile testProfile;
    private Course course1, course2, course3, course4, course5, course6, course7, course8, course9, course10;
    private TextView course_year, course_quarter, course_subject, course_number, class_size;
    private View selectedView;

    @Before
    public void setupTestDatabase(){

        testProfile = new Profile("John","valid_url");
        Profile myProfile = new Profile("Drake", "Valid");

        course1 = new Course("UUID1","2019","Fall","CSE","11", "Tiny (<40)");
        course2 = new Course("UUID2","2021","Fall","CSE","100", "Tiny (<40)");
        course3 = new Course("UUID3","2020","Winter","CSE","30", "Small (40-75)");
        course4 = new Course("UUID4","2020","Winter","MATH","20D", "Small (40-75)");
        course5 = new Course("UUID5","2019","Spring","CSE","20", "Medium (75-150)");
        course6 = new Course("UUID6","2019","Fall","CSE","11", "Medium (75-150)");
        course7 = new Course("UUID7","2021","Fall","CSE","100", "Large (150-250)");
        course8 = new Course("UUID8","2020","Winter","CSE","30", "Huge (250-400)");
        course9 = new Course("UUID9","2020","Winter","MATH","20D", "Gigantic (400+)");
        course10 = new Course("UUID10","2019","Spring","CSE","20", "Gigantic (400+)");


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
    public void testMatchesDisplayed() {

        /**insert single course1 to database with matching profileId*/
        // Needs to be updated with class size

//        ActivityScenario<ViewProfileActivity> viewProfile = ActivityScenario.launch(ViewProfileActivity.class);
//
//        viewProfile.onActivity(activity -> {
//            /*check match name*/
//            onView(withId(R.id.viewprofile_name)).check(matches(withText("John")));
//
//            RecyclerView recyclerView = activity.findViewById(R.id.viewprofile_shared_courses);
//
//            /*test course1*/
//            selectedView = recyclerView.getChildAt(0);
//            course_year = (TextView) selectedView.findViewById(R.id.course_year_row_textview);
//            course_quarter = (TextView) selectedView.findViewById(R.id.course_quarter_row_textview);
//            course_subject = (TextView) selectedView.findViewById(R.id.course_subject_row_textview);
//            course_number = (TextView) selectedView.findViewById(R.id.course_number_row_textview);
//
//            assertEquals(course_year.getText(), "2019");
//            assertEquals(course_quarter.getText(), "Fall");
//            assertEquals(course_subject.getText(), "CSE");
//            assertEquals(course_number.getText(), "11");
//
//            /*test course2*/
//            selectedView = recyclerView.getChildAt(1);
//            course_year = (TextView) selectedView.findViewById(R.id.course_year_row_textview);
//            course_quarter = (TextView) selectedView.findViewById(R.id.course_quarter_row_textview);
//            course_subject = (TextView) selectedView.findViewById(R.id.course_subject_row_textview);
//            course_number = (TextView) selectedView.findViewById(R.id.course_number_row_textview);
//
//            assertEquals(course_year.getText(), "2021");
//            assertEquals(course_quarter.getText(), "Fall");
//            assertEquals(course_subject.getText(), "CSE");
//            assertEquals(course_number.getText(), "100");
//
//            /*test third course*/
//            selectedView = recyclerView.getChildAt(2);
//            course_year = (TextView) selectedView.findViewById(R.id.course_year_row_textview);
//            course_quarter = (TextView) selectedView.findViewById(R.id.course_quarter_row_textview);
//            course_subject = (TextView) selectedView.findViewById(R.id.course_subject_row_textview);
//            course_number = (TextView) selectedView.findViewById(R.id.course_number_row_textview);
//
//            assertEquals(course_year.getText(), "2020");
//            assertEquals(course_quarter.getText(), "Winter");
//            assertEquals(course_subject.getText(), "CSE");
//            assertEquals(course_number.getText(), "30");
//
//            /*test fourth course*/
//            selectedView = recyclerView.getChildAt(3);
//            course_year = (TextView) selectedView.findViewById(R.id.course_year_row_textview);
//            course_quarter = (TextView) selectedView.findViewById(R.id.course_quarter_row_textview);
//            course_subject = (TextView) selectedView.findViewById(R.id.course_subject_row_textview);
//            course_number = (TextView) selectedView.findViewById(R.id.course_number_row_textview);
//
//            assertEquals(course_year.getText(), "2020");
//            assertEquals(course_quarter.getText(), "Winter");
//            assertEquals(course_subject.getText(), "MATH");
//            assertEquals(course_number.getText(), "20D");
//
//            /*test fifth course*/
//            selectedView = recyclerView.getChildAt(4);
//            course_year = (TextView) selectedView.findViewById(R.id.course_year_row_textview);
//            course_quarter = (TextView) selectedView.findViewById(R.id.course_quarter_row_textview);
//            course_subject = (TextView) selectedView.findViewById(R.id.course_subject_row_textview);
//            course_number = (TextView) selectedView.findViewById(R.id.course_number_row_textview);
//
//            assertEquals(course_year.getText(), "2019");
//            assertEquals(course_quarter.getText(), "Spring");
//            assertEquals(course_subject.getText(), "CSE");
//            assertEquals(course_number.getText(), "20");
//
//        });
    }

    @Test
    public void matchesViewAdapterTest() {
        List<Course> sharedCourses = new ArrayList<>();
        sharedCourses.add(new Course("UUID1", "2020", "Fall", "CSE", "110", "Large (150-250)"));
        sharedCourses.add(new Course("UUID2", "2020", "Fall", "CSE", "110", "Large (150-250)"));
        sharedCourses.add(new Course("UUID3", "2020", "Fall", "CSE", "110", "Large (150-250)"));

        SharedCoursesViewAdapter adapter = new SharedCoursesViewAdapter(sharedCourses);
        assertEquals(3, adapter.getItemCount());
    }


}
