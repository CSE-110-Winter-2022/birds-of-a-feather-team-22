package com.example.birdsofafeather.db;
import java.util.*;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "COURSE")
public class Course {

    @PrimaryKey
    @ColumnInfo(name = "courseId")
    private int courseId;

    @ColumnInfo(name = "year")
    private String year;

    @ColumnInfo (name = "quarter")
    private String quarter;

    @ColumnInfo (name = "subject")
    private String subject;

    @ColumnInfo(name = "number")
    private String number;

    @ColumnInfo(name = "students")
    private List<Integer> students;

    public Course() {
        this.year = "";
        this.quarter = "";
        this.subject = "";
        this.number = "";
    }

    public Course(String year, String quarter, String subject, String number) {
        this.year = year;
        this.quarter = quarter;
        this.subject = subject;
        this.number = number;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//
//        if (o == null || this.getClass() != o.getClass() ) {
//            return false;
//        }
//
//        Course course = (Course) o;
//        return this.year.equals(course.year) &&
//                this.quarter.equals(course.quarter) &&
//                this.subject.equals(course.subject) &&
//                this.number.equals(course.number);
//    }

    public void addUserId(int id) {
        this.students.add(id);
    }

    public int getId() {
        return this.courseId;
    }
}
