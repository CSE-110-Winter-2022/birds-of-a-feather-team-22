package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface CourseDAO {
    @Transaction
    @Query()

}
