package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Profile profile);

    @Delete
    void delete(Profile profile);

    @Query("SELECT * FROM PROFILE WHERE profile_id=:profileId")
    Profile getProfile(int profileId);

    @Query("SELECT MAX(profile_id) FROM PROFILE")
    int maxId();
}
