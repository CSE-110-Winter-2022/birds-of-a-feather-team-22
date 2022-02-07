package com.example.birdsofafeather.db;
import java.util.*;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

@Entity (tableName = "COURSE")
public class Course {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "course_id")
    private int courseId;

    @ColumnInfo(name = "profile_id")
    private int profileId;

    @ColumnInfo(name = "year")
    private String year;

    @ColumnInfo (name = "quarter")
    private String quarter;

    @ColumnInfo (name = "subject")
    private String subject;

    @ColumnInfo(name = "number")
    private String number;

    public Course(int profileId,String year, String quarter, String subject, String number) {
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

    public void addUserId(int id) {
        // this.students.getStudentIds().add(id);
    }

    public int getId() {
        return this.courseId;
    }
}
