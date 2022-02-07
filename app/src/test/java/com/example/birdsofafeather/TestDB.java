package com.example.birdsofafeather;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RunWith(AndroidJUnit4.class)
public class TestDB {

    @Test
    public void insert() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase db = AppDatabase.singleton(context);

        db.profileDao().insert(new Profile(1, "Rob", "test"));
        Profile p = db.profileDao().getProfile(1);
        assertNotNull(p);
        assertEquals(1, p.getProfileId());
        assertEquals("Rob", p.getName());
        assertEquals("test", p.getPhoto());

        db.courseDao().insert(new Course(1, "2022", "Winter", "CSE", "110"));
        int courseId = db.courseDao().getCourseId(1, "2022", "Winter", "CSE", "110");
        List<Course> courses = db.courseDao().getCourseByProfileId(1);
        assertNotNull(courses);
        assertEquals(1, courses.size());
        assertEquals(courseId, courses.get(0).getCourseId());
        assertEquals(1 , courses.get(0).getProfileId());
        assertEquals("2022", courses.get(0).getYear());
        assertEquals("Winter", courses.get(0).getQuarter());
        assertEquals("CSE", courses.get(0).getSubject());
        assertEquals("110", courses.get(0).getNumber());
        db.clearAllTables();
    }

    /*
    @Test
    public void delete() {
        this.future = backgroundThreadExecutor.submit(() -> {
            db.profileDao().insert(new Profile(1, "Rob", "test"));
            Profile p = db.profileDao().getProfile(1);

            db.courseDao().insert(new Course(1, "2022", "Winter", "CSE", "110"));
            int courseId = db.courseDao().getCourseId(1, "2022", "Winter", "CSE", "110");
            print(Integer.toString(courseId));
            List<Course> courses = db.courseDao().getCourseByProfileId(1);
            assertNotNull(courses);
            assertEquals(1, courses.size());
            assertEquals(courseId, courses.get(0).getCourseId());
            assertEquals(1 , courses.get(0).getProfileId());
            assertEquals("2022", courses.get(0).getYear());
            assertEquals("Winter", courses.get(0).getQuarter());
            assertEquals("CSE", courses.get(0).getSubject());
            assertEquals("110", courses.get(0).getNumber());
            db.clearAllTables();
            return null;
        });
    }


    @Test
    public void testInsert() {
        this.future = backgroundThreadExecutor.submit(() -> {
            Profile p = new Profile(1, "Rob", "test");
            db.profileDao().insert(p);
            Profile db_p = db.profileDao().getProfile(1);
            assertThat(db_p, equalTo(p));

            Course c = new Course(1, "2022", "Winter", "CSE", "110");
            db.courseDao().insert(c);
            int courseId = db.courseDao().getCourseId(1, "2022", "Winter", "CSE", "110");
            List<Course> courses = db.courseDao().getCourseByProfileId(1);
            assertThat(courses.get(0), equalTo(c));

            return null;
        });
    }
    */
}
