/*
 * This file is capable of organizing the contents found within a course, allowing
 * for setting of fields, modification, and retrieval of data.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

/*
 * This class is capable of storing and retrieving course information.
 */
@Entity (tableName = "COURSE")
public class Course {

    // Columns for DB usage
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "courseId")
    private String courseId;

    @ColumnInfo(name = "profileId")
    private String profileId;

    @ColumnInfo(name = "year")
    private String year;

    @ColumnInfo (name = "quarter")
    private String quarter;

    @ColumnInfo (name = "subject")
    private String subject;

    @ColumnInfo(name = "number")
    private String number;

    @ColumnInfo(name = "classSize")
    private String classSize;

    /**
     * Constructor for class.
     *
     * @param profileId A given profile ID
     * @param year A given year
     * @param quarter A given quarter
     * @param subject A given subject
     * @param number A given number
     * @param classSize A given class size
     * @return none
     */
    public Course (String profileId, String year, String quarter, String subject, String number, String classSize) {
        this.courseId = UUID.randomUUID().toString();
        this.profileId = profileId;
        this.year = year;
        this.quarter = quarter;
        this.subject = subject;
        this.number = number;
        this.classSize = classSize;
    }

    /**
     * Returns the course ID.
     *
     * @param
     * @return A course ID
     */
    public String getCourseId() {
        return this.courseId;
    }

    /**
     * Sets the ID of a course object
     *
     * @param courseId A given course ID
     * @return none
     */
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    /**
     * Returns the ID of a profile associated with the course.
     *
     * @param
     * @return The profile ID associated with course
     */
    public String getProfileId() {
        return this.profileId;
    }

    /**
     * Sets the ID of the course associated with a profile.
     *
     * @param profileId A given profile ID
     * @return none
     */
    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    /**
     * Returns the year of the course.
     *
     * @param
     * @return Year of course
     */
    public String getYear() {
        return this.year;
    }

    /**
     * Sets the year of the course.
     *
     * @param year A given year
     * @return none
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Returns the quarter of the course.
     *
     * @param
     * @return Quarter of course
     */
    public String getQuarter() {
        return this.quarter;
    }

    /**
     * Sets the quarter of the course.
     *
     * @param quarter A given quarter
     * @return none
     */
    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    /**
     * Returns the subject of the course.
     *
     * @param
     * @return Subject of course
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * Sets the subject of the course.
     *
     * @param subject A given subject
     * @return none
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Returns the number of the course.
     *
     * @param
     * @return Number of course
     */
    public String getNumber() {
        return this.number;
    }

    /**
     * Sets the number of the course.
     * @param number A given number
     * @return none
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Returns the class size of the course.
     *
     * @param
     * @return Class size of course
     */
    public String getClassSize() {
        return this.classSize;
    }

    /**
     * Sets the class size of the course.
     *
     * @param classSize A given class size
     * @return none
     */
    public void setClassSize(String classSize) {
        this.classSize = classSize;
    }

    /**
     * Returns all appropriate information of a course. Used for testing.
     *
     * @param
     * @return Representation of fields in course
     */
    @Override
    public String toString() {
        return "" + this.courseId + " " + this.profileId + " " +  this.year + " " + this.quarter + " " + this.subject + " " + this.number;
    }

}
