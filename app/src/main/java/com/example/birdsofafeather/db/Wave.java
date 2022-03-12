/*
 * This file is capable of storing and modification in regards to waves in the application.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * This class stores all Wave information
 */
@Entity(tableName = "WAVE")
public class Wave {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "profileId")
    private String profileId;

    @ColumnInfo(name = "wave")
    private String wave;

    /**
     * The default constructor for Wave
     *
     * @param profileId The profile id of the recipient of the wave
     * @param wave The Nearby Message String of the wave
     */
    public Wave(@NonNull String profileId, String wave) {
        this.profileId = profileId;
        this.wave = wave;
    }

    /**
     * Gets the profile id
     *
     * @return The profile id
     */
    @NonNull
    public String getProfileId() {
        return this.profileId;
    }

    /**
     * Sets the profile id
     *
     * @param profileId The new profile id
     */
    public void setProfileId(@NonNull String profileId) {
        this.profileId = profileId;
    }

    /**
     * Gets the wave String
     *
     * @return The wave String
     */
    public String getWave() {
        return this.wave;
    }

    /**
     * Sets the wave String
     *
     * @param wave The new wave String
     */
    public void setWave(String wave) {
        this.wave = wave;
    }
}
