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
    // Retrieves a list of DiscoveredUsers
    @Transaction
    @Query("SELECT profileId, numShared FROM DISCOVEREDUSER")
    List<DiscoveredUser> getDiscoveredUsers();

    // Checks if a user has already been discovered
    @Query("SELECT profileId FROM DISCOVEREDUSER where profileId=:profileId")
    int exists(int profileId);

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
