package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface CourseDao {
    @Transaction
    @Query("SELECT * FROM COURSE WHERE year=:year AND quarter=:quarter AND subject=:subject AND number=:number")
    Course getCourseByInfo(int year, int quarter, int subject, int number);

    @Transaction
    @Query("SELECT * FROM COURSE WHERE courseId=:courseId")
    Course getCourseById(int courseId);

    @Insert
    void insert(Course course);

}
