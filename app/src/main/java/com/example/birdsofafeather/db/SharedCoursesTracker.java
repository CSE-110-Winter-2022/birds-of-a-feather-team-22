package com.example.birdsofafeather.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.HashMap;
import java.util.List;

@Entity(tableName = "sct")
public class SharedCoursesTracker {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int trackerId;

    @ColumnInfo(name = "shared_courses")
    private HashMap<Integer, List<Course>> sharedCourses;

    public int getId() {
        return this.trackerId;
    }
}
