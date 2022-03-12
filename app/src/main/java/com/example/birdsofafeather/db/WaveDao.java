/*
 * This file is capable of enabling interactions with respect to the wave data access object.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */
package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

/*
 * This interface is capable of providing the means to be able to allow for searches, insertions, and
 * modifications to waves.
 */
@Dao
public interface WaveDao {
    /**
     * Retrieves list of all Waves in database.
     *
     * @return List of all Wave objects
     */
    @Transaction
    @Query("SELECT * FROM WAVE")
    List<Wave> getAllWaves();


    /**
     * Gets a Wave with matching profileId
     *
     * @param profileId The profile id
     * @return The Wave with matching profileId
     */
    @Query("SELECT * FROM WAVE WHERE profileId=:profileId")
    Wave getWave(String profileId);

    /**
     * Inserts a Wave into the database.
     *
     * @param wave A Wave object
     */
    @Insert
    void insert(Wave wave);

    /**
     * Deletes a Wave from the database.
     *
     * @param wave A Wave object
     */
    @Delete
    void delete(Wave wave);

    /**
     * Updates a Wave in the database
     *
     * @param wave A Wave object
     */
    @Update
    void update(Wave wave);
}
