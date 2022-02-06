package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface CourseDao {

    @Transaction
    @Query("SELECT profile_id FROM COURSE WHERE year=:year AND quarter=:quarter AND subject=:subject AND number=:number")
    List<Integer> getCourseByInfo(int year, int quarter, int subject, int number);

    @Transaction
    @Query("SELECT * FROM COURSE WHERE profile_id=:profileId")
    List<Course> getCourseByProfileId(int profileId);

    @Insert
    void insert(Course course);

}
