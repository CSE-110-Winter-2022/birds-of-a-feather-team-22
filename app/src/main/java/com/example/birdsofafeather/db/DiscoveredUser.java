/*
 * This file is capable of organizing the contents found within a discovered user, allowing
 * for setting of fields, modification, and retrieval of data.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/*
 * This class is used to store a previously discovered user.
 */
@Entity(tableName = "DISCOVEREDUSER")
public class DiscoveredUser {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "profileId")
    private String profileId;

    @ColumnInfo(name = "sessionId")
    private String sessionId;

    @ColumnInfo(name = "numShared")
    private int numShared;

    /**
     * Constructor for class.
     *
     * @param profileId A given profile ID
     * @param sessionId A given session ID
     * @param numShared The number of courses shared with the user
     */
    public DiscoveredUser(String profileId, String sessionId, int numShared) {
        this.profileId = profileId;
        this.sessionId = sessionId;
        this.numShared = numShared;
    }

    /**
     * Returns the profile ID of the discovered user.
     *
     * @return Profile ID of discovered user
     */
    public String getProfileId() {
        return this.profileId;
    }

    /**
     * Sets the profile ID of the discovered user.
     *
     * @param profileId A given profile ID
     */
    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    /**
     * Returns the session ID of the discovered user.
     *
     * @return Session ID of discovered user
     */
    public String getSessionId() {
        return this.sessionId;
    }

    /**
     * Sets the session ID of the discovered user.
     *
     * @param sessionId A given session ID
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Returns the number of courses that is shared between a discovered user and self user.
     *
     * @return Number of shared courses between discovered user and self user
     */
    public int getNumShared() {
        return this.numShared;
    }

    /**
     * Sets the number of courses shared between a discovered user and self user.
     *
     * @param numShared Number of shared courses
     */
    public void setNumShared(int numShared) {
        this.numShared = numShared;
    }
}