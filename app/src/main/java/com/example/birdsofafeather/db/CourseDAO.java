package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface CourseDAO {
    @Transaction
    @Query("SELECT * FROM COURSE WHERE  year=:year AND quarter=:quarter AND subject=:subject AND number=:number")
    Course getCourse(int year, int quarter, int subject, int number);
}
