package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

// Dao used to access profiles in the PROFILE table
@Dao
public interface ProfileDao {

    // Retrieves the profile with matching profile id
    @Query("SELECT * FROM PROFILE WHERE profileId=:profileId")
    Profile getProfile(int profileId);

    // Retrieves the maximum profile id among all profile objects
    @Query("SELECT MAX(profileId) FROM PROFILE")
    int maxId();

    // Retrieves the number of profile objects
    @Query("SELECT COUNT(*) FROM PROFILE")
    int count();

    // Inserts a profile object without conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Profile profile);

    // Deletes a profile object
    @Delete
    void delete(Profile profile);
}
