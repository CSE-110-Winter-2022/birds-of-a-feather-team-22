package com.example.birdsofafeather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

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
