package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.HashMap;
import java.util.List;

@Dao
public interface SharedCoursesTrackerDao {

    @Transaction
    @Query("SELECT sharedCourses FROM SHAREDCOURSESTRACKER")
    HashMap<Integer, List<Course>> getSharedCoursesMap(int profileId);
}
