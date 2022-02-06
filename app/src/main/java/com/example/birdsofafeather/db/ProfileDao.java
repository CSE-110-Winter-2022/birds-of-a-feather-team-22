package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface ProfileDao {

    @Insert
    void insert(Profile profile);

    @Query("SELECT * FROM PROFILE WHERE profileId=:profileId")
    Profile getProfile(int profileId);

}
