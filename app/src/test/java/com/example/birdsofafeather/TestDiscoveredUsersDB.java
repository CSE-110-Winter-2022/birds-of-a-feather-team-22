package com.example.birdsofafeather;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.DiscoveredUser;
import com.example.birdsofafeather.db.Profile;

import static org.junit.Assert.assertEquals;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TestDiscoveredUsersDB {

    public Context context = ApplicationProvider.getApplicationContext();
    public AppDatabase db = AppDatabase.useTestSingleton(context);

    //Testing if discoveredUsers are inserted into DB
    @Test
    public void testDiscoveredUsersInDB() {
        DiscoveredUser du1 = new DiscoveredUser("2", "1", 5);
        DiscoveredUser du2 = new DiscoveredUser("3", "1", 3);

        db.discoveredUserDao().insert(du1);
        db.discoveredUserDao().insert(du2);

        assertEquals(2, db.discoveredUserDao().count());

        List<DiscoveredUser> s1 = db.discoveredUserDao().getDiscoveredUsersFromSession("1");

        assertEquals(2, s1.size());

        assertEquals("2",s1.get(0).getProfileId());
        assertEquals("1",s1.get(0).getSessionId());
        assertEquals(5,s1.get(0).getNumShared());

        assertEquals("3",s1.get(1).getProfileId());
        assertEquals("1",s1.get(1).getSessionId());
        assertEquals(3,s1.get(1).getNumShared());

        DiscoveredUser du3 = new DiscoveredUser("5", "2", 5);
        db.discoveredUserDao().insert(du3);

        assertEquals(3, db.discoveredUserDao().count());

        List<DiscoveredUser> all = db.discoveredUserDao().getAllDiscoveredUsers();

        assertEquals(3, all.size());

    }

    //Testing if discoveredUsers are deleted from DB (used to check if status of course is correct in db)
    @Test
    public void deleteDiscoveredUsersInDB() {
        DiscoveredUser du1 = new DiscoveredUser("2", "1", 5);
        db.discoveredUserDao().insert(du1);

        assertEquals(1, db.discoveredUserDao().count());

        db.discoveredUserDao().delete(du1);

        assertEquals(0, db.discoveredUserDao().count());
    }
}
