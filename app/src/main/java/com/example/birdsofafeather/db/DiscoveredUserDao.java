/*
 * This file is capable of providing the means to be able to allow for querying, inserting, and deleting
 * discovered user data access objects.
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
 * This interface is capable of providing the means to be able to allow for searches, inserting, and
 * deletion from the database.
 */
@Dao
public interface DiscoveredUserDao {

    /**
     * Retrieves a list of all DiscoveredUser objects across all sessions (may contain duplicates).
     *
     * @return List of all DiscoveredUser objects
     */
    @Transaction
    @Query("SELECT * FROM DISCOVEREDUSER")
    List<DiscoveredUser> getAllDiscoveredUsers();

    /**
     * Retrieves all DiscoveredUser objects with a matching session ID.
     *
     * @param sessionId A given session ID
     * @return List of discovered users that having matching session ID
     */
    @Transaction
    @Query("SELECT * FROM DISCOVEREDUSER WHERE sessionId=:sessionId")
    List<DiscoveredUser> getDiscoveredUsersFromSession(String sessionId);

    /**
     * Checks to see if a given profile ID has already been discovered.
     *
     * @param profileId A given profile ID
     * @return A profile ID if profile with given ID exists, null otherwise
     */
    @Query("SELECT profileId FROM DISCOVEREDUSER where profileId=:profileId")
    String getProfileId(String profileId);

    /**
     * Retrieves the discovered user from a given profile ID and session ID.
     *
     * @param profileId A given profile ID
     * @param sessionId A given session ID
     * @return Discovered user corresponding with profile and sessions IDs
     */
    @Query("SELECT * FROM DISCOVEREDUSER where profileId=:profileId AND sessionId=:sessionId")
    DiscoveredUser getDiscoveredUserFromSession(String profileId, String sessionId);

    /**
     * Retrieves the number of DiscoveredUser objects in database.
     *
     * @return Number of DiscoveredUser objects
     */
    @Query("SELECT COUNT(*) FROM DISCOVEREDUSER")
    int count();

    /**
     * Inserts du into the database without conflicts.
     *
     * @param du A given DiscoveredUser object
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DiscoveredUser du);

    /**
     * Deletes du from the database.
     *
     * @param du A given DiscoveredUser object
     */
    @Delete
    void delete(DiscoveredUser du);

    /**
     * Updates du in the database.
     *
     * @param du A given DiscoveredUser object
     */
    @Update
    void update(DiscoveredUser du);
}
