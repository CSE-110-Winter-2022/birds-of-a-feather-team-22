/*
 * This file is capable of providing the means to be able to allow for querying, inserting, and deleting
 * profile data access objects.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

/*
 * This interface is capable of providing the means to be able to allow for searches, insertions,
 * deletion from the database.
 */
@Dao
public interface ProfileDao {

    /**
     * Retrieves the profile with matching profile ID.
     *
     * @param profileId A given profile ID
     * @return A profile object
     */
    @Query("SELECT * FROM PROFILE WHERE profileId=:profileId")
    Profile getProfile(String profileId);

    /**
     * Retrieves the self user's profile.
     *
     * @param isSelf Whether the self user is being used
     * @return Self user's profile
     */
    @Query("SELECT * FROM PROFILE WHERE isSelf=:isSelf")
    Profile getSelfProfile(boolean isSelf);

    /**
     * Retrieves a list of profiles which are favorites.
     *
     * @param isFavorite The status of being a favorite
     * @return List of profiles that are favorites
     */
    @Query("SELECT * FROM PROFILE WHERE isFavorite=:isFavorite")
    List<Profile> getFavoriteProfiles(boolean isFavorite);

    /**
     * Retrieves a list of profiles which are being waved at.
     *
     * @param isWaving The status of waving at
     * @return List of profiles which are being waved at
     */
    @Query("SELECT * FROM PROFILE WHERE isWaving=:isWaving")
    List<Profile> getWavingProfiles(boolean isWaving);

    /**
     * Retrieves a list of IDs of profiles which are waving at user.
     *
     * @param isWaving The status of waving to user
     * @return List of IDs that are waving to user
     */
    @Query("SELECT profileId FROM PROFILE WHERE isWaving=:isWaving")
    List<String> getWavingProfileIds(boolean isWaving);

    /**
     * Retrieves a list of profiles that have been waved at.
     *
     * @param isWaved Status of what was waved at
     * @return List of profiles that have been waved at
     */
    @Query("SELECT * FROM PROFILE WHERE isWaved=:isWaved")
    List<Profile> getWavedProfiles(boolean isWaved);

    /**
     * Retrieves a list of profile IDs that have been waved at.
     *
     * @param isWaved Status of what was waved at
     * @return List of profiles that have been waved at
     */
    @Query("SELECT profileId FROM PROFILE WHERE isWaved=:isWaved")
    List<String> getWavedProfileIds(boolean isWaved);

    /**
     * Retrieves a list of all profiles in database.
     *
     * @return List of all profiles in database
     */
    @Transaction
    @Query("SELECT * FROM PROFILE")
    List<Profile> getAllProfiles();

    /**
     * Retrieves the number of profile objects in database.
     *
     * @return Number of profile
     */
    @Query("SELECT COUNT(*) FROM PROFILE")
    int count();

    /**
     * Inserts a profile object into database.
     *
     * @param profile A profile object
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Profile profile);

    /**
     * Deletes a profile object from database.
     *
     * @param profile A profile object
     */
    @Delete
    void delete(Profile profile);

    /**
     * Updates a profile object in database.
     *
     * @param profile A profile object
     */
    @Update
    void update(Profile profile);
}
