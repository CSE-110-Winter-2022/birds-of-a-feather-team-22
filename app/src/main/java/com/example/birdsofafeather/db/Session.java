/*
 * This file is capable of organizing the contents found within a course, allowing for setting of
 * fields, modification, and retrieval of data.
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
 * This class is capable of storing and retrieving session information.
 */
@Entity(tableName = "SESSION")
public class Session {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sessionId")
    private String sessionId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "isLastSession")
    private boolean isLastSession;

    @ColumnInfo(name = "sortFilter")
    private String sortFilter;

    /**
     * Constructor for class.
     *
     * @param sessionId A given session ID
     * @param name A given name
     * @param isLastSession Whether the session was the last used session
     */
    public Session(@NonNull String sessionId, String name, boolean isLastSession) {
        this.sessionId = sessionId;
        this.name = name;
        this.isLastSession = isLastSession;
        this.sortFilter = "No Sort/Filter";
    }

    /**
     * Returns the ID of the session.
     *
     * @return ID of session
     */
    @NonNull
    public String getSessionId() {
        return this.sessionId;
    }

    /**
     * Sets the ID of the session.
     *
     * @param sessionId A given session ID
     */
    public void setSessionId(@NonNull String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Returns the name of the session.
     *
     * @return Name of session
     */
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns whether the session was the last session being used.
     *
     * @return Whether session was last used or not
     */
    public boolean getIsLastSession() {
        return this.isLastSession;
    }

    /**
     * Set whether the session was last used or not.
     *
     * @param isLastSession Status of session being last used or not
     */
    public void setIsLastSession(boolean isLastSession) {
        this.isLastSession = isLastSession;
    }

    /**
     * Returns the sort filter of the session.
     *
     * @return Sort filter of session
     */
    public String getSortFilter() {
        return this.sortFilter;
    }

    /**
     * Sets the sort filter of the session.
     *
     * @param sortFilter A given sort filter
     */
    public void setSortFilter(String sortFilter) {
        this.sortFilter = sortFilter;
    }
}
