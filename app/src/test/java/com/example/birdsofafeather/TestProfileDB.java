package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Profile;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TestProfileDB {

    public Context context = ApplicationProvider.getApplicationContext();
    public AppDatabase db = AppDatabase.useTestSingleton(context);
    private String default_photo = "https://i.imgur.com/MZH5yxZ.png";

    //Testing if profiles are inserted into DB
    @Test
    public void testProfileInDB() {
        Profile p1 = new Profile("Name1", "invalid_url");
        db.profileDao().insert(p1);

        assertEquals(1, db.profileDao().count());

        Profile grabP1 = db.profileDao().getProfile(p1.getProfileId());

        //check if p1 fields are correct
        assertEquals(p1.getProfileId(), grabP1.getProfileId());
        assertEquals("Name1", grabP1.getName());
        assertEquals(default_photo, grabP1.getPhoto());

        String gary_meme_photo = "https://i.redd.it/2j60p7c3nt301.png";
        Profile p2 = new Profile("Name2", gary_meme_photo);
        db.profileDao().insert(p2);

        assertEquals(2, db.profileDao().count());

        Profile grabP2 = db.profileDao().getProfile(p2.getProfileId());

        //check if p2 fields are correct
        assertEquals(p2.getProfileId(), grabP2.getProfileId());
        assertEquals("Name2", grabP2.getName());
        assertEquals(gary_meme_photo, grabP2.getPhoto());

        //check if profile dao methods are functional
        List<Profile> profileList = db.profileDao().getAllProfiles();
        assertEquals(2, profileList.size());

    }

    //Testing if courses are deleted from DB (used to check if status of course is correct in db)
    @Test
    public void deleteProfileInDB(){
        Profile p1 = new Profile("Name1", "test_photo.png");
        Profile p2 = new Profile("Name2", "test_photo_1.png");
        db.profileDao().insert(p1);
        db.profileDao().insert(p2);

        assertEquals(2, db.profileDao().count());

        db.profileDao().delete(p1);

        assertEquals(1, db.profileDao().count());

        db.profileDao().delete(p2);
        assertEquals(0, db.profileDao().count());
    }
}
