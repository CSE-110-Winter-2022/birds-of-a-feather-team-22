package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

// Dao used to access session information in the SESSION table
@Dao
public interface SessionDao {

    // Retrieves the sessionId with unique matching session name
    @Query("SELECT sessionId FROM SESSION WHERE name=:name")
    String getSessionId(String name);

    @Query("SELECT * FROM SESSION WHERE sessionId=:sessionId")
    Session getSession(String sessionId);

    @Query("SELECT * FROM SESSION WHERE isLastSession=:isLastSession")
    Session getLastSession(boolean isLastSession);

    // Retrieves list of sessions
    @Transaction
    @Query("SELECT * FROM SESSION")
    List<Session> getAllSessions();



    // Retrieves the number of session objects
    @Query("SELECT COUNT(*) FROM SESSION")
    int count();

    // Inserts a session object without conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Session session);

    // Deletes a session object
    @Delete
    void delete(Session session);

    @Update
    void update(Session session);
}