package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

// Dao used to access profiles in the PROFILE table
@Dao
public interface ProfileDao {

    // Retrieves the profile with matching profile id
    @Query("SELECT * FROM PROFILE WHERE profileId=:profileId")
    Profile getProfile(String profileId);

    // Retrieves the user's (self) profile
    @Query("SELECT * FROM PROFILE WHERE isUser=:isUser")
    Profile getUserProfile(boolean isUser);

    @Query("SELECT * FROM PROFILE WHERE isFavorite=:isFavorite")
    List<Profile> getFavoriteProfiles(boolean isFavorite);

    @Query("SELECT * FROM PROFILE WHERE isWaving=:isWaving")
    List<Profile> getWavingProfiles(boolean isWaving);

    @Query("SELECT profileId FROM PROFILE WHERE isWaving=:isWaving")
    List<String> getWavingProfileIds(boolean isWaving);

    @Query("SELECT * FROM PROFILE WHERE isWaved=:isWaved")
    List<Profile> getWavedProfiles(boolean isWaved);

    @Query("SELECT profileId FROM PROFILE WHERE isWaved=:isWaved")
    List<String> getWavedProfileIds(boolean isWaved);

    // Retrieves list of profiles
    @Transaction
    @Query("SELECT * FROM PROFILE")
    List<Profile> getAllProfiles();

    // Retrieves the number of profile objects
    @Query("SELECT COUNT(*) FROM PROFILE")
    int count();

    // Inserts a profile object without conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Profile profile);

    // Deletes a profile object
    @Delete
    void delete(Profile profile);

    @Update
    void update(Profile profile);
}
