package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface ProfileDao {

    @Insert
    void insert(Profile profile);


}
