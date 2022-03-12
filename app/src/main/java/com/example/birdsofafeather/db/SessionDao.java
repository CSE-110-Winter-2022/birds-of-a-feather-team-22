/*
 * This file is capable of providing the means to be able to allow for querying, inserting, and
 * deleting session data access objects.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */
package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

/*
 * This interface is capable of providing the means to be able to allow for searches, insertions, and
 * deletions from the database.
 */
@Dao
public interface SessionDao {

    /**
     * Retrieves the session ID with unique matching session name.
     *
     * @param name A given name
     * @return ID of session with matching name
     */
    @Query("SELECT sessionId FROM SESSION WHERE name=:name")
    String getSessionId(String name);

    /**
     * Retrieves a session object from an ID.
     *
     * @param sessionId A given ID
     * @return Session object with corresponding ID
     */
    @Query("SELECT * FROM SESSION WHERE sessionId=:sessionId")
    Session getSession(String sessionId);

    /**
     * Retrieves a session that was last used.
     *
     * @param isLastSession Status of session being last used
     * @return Session object that was last used
     */
    @Query("SELECT * FROM SESSION WHERE isLastSession=:isLastSession")
    Session getLastSession(boolean isLastSession);

    /**
     * Retrieves list of all sessions in database.
     *
     * @return List of all session objects
     */
    @Transaction
    @Query("SELECT * FROM SESSION")
    List<Session> getAllSessions();

    /**
     * Retrieves a list of all session names in database.
     *
     * @return List of all session names
     */
    @Transaction
    @Query("SELECT name FROM SESSION")
    List<String> getAllSessionNames();

    /**
     * Retrieves the number of session objects in database.
     *
     * @return Number of session objects in database
     */
    @Query("SELECT COUNT(*) FROM SESSION")
    int count();

    /**
     * Inserts a session objects without any conflicts.
     *
     * @param session A given session object
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Session session);

    /**
     * Deletes a session object from the database.
     *
     * @param session A given session object
     */
    @Delete
    void delete(Session session);

    /**
     * Updates a session object from the database.
     * @param session
     */
    @Update
    void update(Session session);
}