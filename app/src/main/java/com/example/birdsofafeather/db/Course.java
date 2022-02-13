package com.example.birdsofafeather.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Course class used to store course information
@Entity (tableName = "COURSE")
public class Course {

    @PrimaryKey
    @ColumnInfo(name = "courseId")
    private int courseId;

    @ColumnInfo(name = "profileId")
    private int profileId;

    @ColumnInfo(name = "year")
    private String year;

    @ColumnInfo (name = "quarter")
    private String quarter;

    @ColumnInfo (name = "subject")
    private String subject;

    @ColumnInfo(name = "number")
    private String number;

    public Course(int courseId, int profileId,String year, String quarter, String subject, String number) {
        this.courseId = courseId;
        this.profileId = profileId;
        this.year = year;
        this.quarter = quarter;
        this.subject = subject;
        this.number = number;
    }

    public int getCourseId() {
        return this.courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getProfileId() {
        return this.profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getQuarter() {
        return this.quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    // For testing purposes
    @Override
    public String toString() {
        return "" + this.courseId + " " + this.profileId + " " +  this.year + " " + this.quarter + " " + this.subject + " " + this.number;
    }

}
