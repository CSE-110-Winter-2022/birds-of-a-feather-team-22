/*
 * This file is capable of enabling interactions between the application and the database to allow
 * for processing of course data access objects.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

/*
 * This interface is capable of providing the means to be able to allow for querying, inserting, and
 * deleting from the database.
 */
@Dao
public interface CourseDao {

    /**
     * Retrieves a list of courses
     * @param profileId A profile ID
     * @return List of courses corresponding to the profile ID
     */
    @Transaction
    @Query("SELECT * FROM COURSE WHERE profileId=:profileId")
    List<Course> getCoursesByProfileId(String profileId);

    /**
     * Retrieves the course ID of a course objects with specific course information and profile
     * ID.
     * @param profileId A given profile ID
     * @param year A given year
     * @param quarter A given quarter
     * @param subject A given subject
     * @param number A given number
     * @param classSize A given number
     * @return Course ID with exact passed fields
     */
    @Query("SELECT courseId FROM COURSE WHERE profileId=:profileId AND year=:year AND quarter=:quarter AND subject=:subject AND number=:number AND classSize=:classSize")
    String getCourseId(String profileId, String year, String quarter, String subject, String number, String classSize);


    /**
     * Retrieves the course object with specific course information.
     * @param profileId A given profile ID
     * @param year A given year
     * @param quarter A given quarter
     * @param subject A given subject
     * @param number A given number
     * @param classSize A given classSize
     * @return Course object with specific course information, otherwise null
     */
    @Query("SELECT * FROM COURSE WHERE profileId=:profileId AND year=:year AND quarter=:quarter AND subject=:subject AND number=:number AND classSize=:classSize")
    Course getCourse(String profileId, String year, String quarter, String subject, String number, String classSize);

    /**
     * Retruns the number of course objects stored into the database.
     *
     * @return Number of course objects in database
     */
    @Query("SELECT COUNT(*) FROM COURSE")
    int count();

    /**
     * Inserts course without any conflict.
     * @param course A given course object
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Course course);

    /**
     * Deletes course from the database.
     * @param course A given course object
     */
    @Delete
    void delete(Course course);

    /**
     * Updates course in the database.
     * @param course A given course object
     */
    @Update
    void update(Course course);
}
