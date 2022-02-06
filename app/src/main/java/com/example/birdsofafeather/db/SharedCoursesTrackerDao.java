/*
package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.HashMap;
import java.util.List;

@Dao
public interface SharedCoursesTrackerDao {

    @Transaction
    @Query("SELECT shared_courses FROM sct")
    HashMap<Integer, List<Course>> getSharedCoursesMap(int profileId);

    @Insert
    void insert(SharedCoursesTracker sct);

    @Query("SELECT COUNT(*) from sct")
    int count();

}
*/