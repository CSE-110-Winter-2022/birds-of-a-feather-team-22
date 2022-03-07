package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

// Dao used to access courses in the COURSE table
@Dao
public interface CourseDao {

    // Retrieves a list of courses with matching profile id
    @Transaction
    @Query("SELECT * FROM COURSE WHERE profileId=:profileId")
    List<Course> getCoursesByProfileId(String profileId);

    // Retrieves the course id of a course object with specific course information and profile id
    @Query("SELECT courseId FROM COURSE WHERE profileId=:profileId AND year=:year AND quarter=:quarter AND subject=:subject AND number=:number AND classSize=:classSize")
    String getCourseId(String profileId, String year, String quarter, String subject, String number, String classSize);

    @Query("SELECT * FROM COURSE WHERE quarter=:quarter AND year=:year")
    Course getCourseFromQuarter(String quarter, String year);

    // Retrieves the number of course objects
    @Query("SELECT COUNT(*) FROM COURSE")
    int count();

    // Inserts a course object without conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Course course);

    // Deletes a course object
    @Delete
    void delete(Course course);
}
