package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Session;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class TestSimpleSessionActivity {
    public Context context = ApplicationProvider.getApplicationContext();
    public AppDatabase db = AppDatabase.useTestSingleton(context);

    @Test
    public void testSession(){
        //create first new session
        Session s = new Session("1", "CSE 110", true);

        db.sessionDao().insert(s);

        assertEquals(1, db.sessionDao().count());
        assertTrue(s.getIsLastSession());

        //create second new session
        Session s1 = new Session("2", "CSE 210", true);


        s.setIsLastSession(false);
        db.sessionDao().update(s);
        db.sessionDao().insert(s1);

        //ensure that previous session is not last session while new session is
        assertEquals(2, db.sessionDao().count());
        assertFalse(s.getIsLastSession());
        assertTrue(s1.getIsLastSession());
    }

    @Test
    public void deleteSession(){
        Session s = new Session("1", "CSE 110", true);
        db.sessionDao().insert(s);

        assertEquals(1, db.sessionDao().count());

        db.sessionDao().delete(s);

        assertEquals(0, db.sessionDao().count());
    }
}
