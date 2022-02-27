package com.example.birdsofafeather.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

// Course class used to store course information
@Entity (tableName = "COURSE")
public class Course {

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

    public Course (String profileId, String year, String quarter, String subject, String number, String classSize) {
        this.courseId = UUID.randomUUID().toString();
        this.profileId = profileId;
        this.year = year;
        this.quarter = quarter;
        this.subject = subject;
        this.number = number;
        this.classSize = classSize;
    }

    public String getCourseId() {
        return this.courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getProfileId() {
        return this.profileId;
    }

    public void setProfileId(String profileId) {
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

    public String getClassSize() {
        return this.classSize;
    }

    public void setClassSize(String classSize) {
        this.classSize = classSize;
    }

    // For testing purposes
    @Override
    public String toString() {
        return "" + this.courseId + " " + this.profileId + " " +  this.year + " " + this.quarter + " " + this.subject + " " + this.number;
    }

}
