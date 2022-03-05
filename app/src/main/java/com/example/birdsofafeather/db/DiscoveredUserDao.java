package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

// Dao used to access discovered users in the DISCOVEREDUSER table
@Dao
public interface DiscoveredUserDao {
    // Retrieves a list of DiscoveredUser objects with a matching profile id
    @Query("SELECT * FROM DISCOVEREDUSER WHERE profileId=:profileId")
    List<DiscoveredUser> getDiscoveredUser(String profileId);

    // Retrieves a list of all DiscoveredUser objects across all sessions (may contain duplicates)
    @Transaction
    @Query("SELECT * FROM DISCOVEREDUSER")
    List<DiscoveredUser> getAllDiscoveredUsers();

    // Retrieves all DiscoveredUser objects with a matching session id
    @Transaction
    @Query("SELECT * FROM DISCOVEREDUSER WHERE sessionId=:sessionId")
    List<DiscoveredUser> getDiscoveredUsersFromSession(String sessionId);

    @Query("SELECT isFavorite FROM DISCOVEREDUSER WHERE profileId=:profileId")
    boolean getFavoriteStatus(String profileId);

    // Checks if a user has already been discovered
    @Query("SELECT profileId FROM DISCOVEREDUSER where profileId=:profileId")
    String getProfileId(String profileId);

    // Retrieves the number of DiscoveredUsers objects
    @Query("SELECT COUNT(*) FROM DISCOVEREDUSER")
    int count();

    // Inserts a DiscoveredUser object without conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DiscoveredUser du);

    // Deletes a DiscoveredUser object
    @Delete
    void delete(DiscoveredUser du);
}
