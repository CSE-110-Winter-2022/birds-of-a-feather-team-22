package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface CourseDao {

    @Transaction
    @Query("SELECT profile_id FROM COURSE WHERE year=:year AND quarter=:quarter AND subject=:subject AND number=:number")
    List<Integer> getProfileIdsByInfo(String year, String quarter, String subject, String number);

    @Transaction
    @Query("SELECT * FROM COURSE WHERE profile_id=:profileId")
    List<Course> getCoursesByProfileId(int profileId);

    @Query("SELECT course_id FROM COURSE WHERE profile_id=:profileId AND year=:year AND quarter=:quarter AND subject=:subject AND number=:number")
    int getCourseId(int profileId, String year, String quarter, String subject, String number);

    @Insert
    void insert(Course course);

    @Delete
    void delete(Course course);

    @Query("SELECT COUNT(*) FROM COURSE")
    int count();

    @Query("SELECT MAX(course_id) FROM COURSE")
    int maxId();

}
