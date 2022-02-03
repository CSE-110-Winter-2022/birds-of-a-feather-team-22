package com.example.birdsofafeather.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.HashMap;
import java.util.List;

@Entity(tableName = "SHAREDCOURSESTRACKER")
public class SharedCoursesTracker {

    @PrimaryKey
    @ColumnInfo(name = "trackerId")
    private int trackerId;

    @ColumnInfo(name = "sharedCourses")
    private HashMap<Integer, List<Course>> sharedCourses;

    public int getId() {
        return this.trackerId;
    }
}
