package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Profile;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestProfileDB {

    public Context context = ApplicationProvider.getApplicationContext();
    public AppDatabase db = AppDatabase.useTestSingleton(context);

    @Test
    public void testProfileInDB() {
        Profile p1 = new Profile(1, "Name1", "test_photo.png");
        db.profileDao().insert(p1);

        assertEquals(db.profileDao().count(), 1);

        Profile grabP1 = db.profileDao().getProfile(1);

        assertEquals(grabP1.getProfileId(), 1);
        assertEquals(grabP1.getName(), "Name1");
        assertEquals(grabP1.getPhoto(), "test_photo.png");

        Profile p2 = new Profile(db.profileDao().maxId()+1, "Name2", "another_test_photo_png");
        db.profileDao().insert(p2);

        assertEquals(db.profileDao().count(), 2);

        Profile grabP2 = db.profileDao().getProfile(2);

        assertEquals(grabP2.getProfileId(), 2);
        assertEquals(grabP2.getName(), "Name2");
        assertEquals(grabP2.getPhoto(), "another_test_photo_png");
    }

    @Test
    public void deleteProfileInDB(){
        Profile p1 = new Profile(1, "Name1", "test_photo.png");
        Profile p2 = new Profile(2, "Name2", "test_photo_1.png");
        db.profileDao().insert(p1);
        db.profileDao().insert(p2);

        assertEquals(db.profileDao().count(), 2);

        db.profileDao().delete(p1);

        assertEquals(db.profileDao().count(), 1);

        db.profileDao().delete(p2);
        assertEquals(db.profileDao().count(), 0);
    }
}
