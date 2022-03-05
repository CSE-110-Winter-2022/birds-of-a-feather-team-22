package com.example.birdsofafeather.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// DiscoveredUser class used to store a previously discovered user
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

    @ColumnInfo(name = "isFavorite")
    private boolean isFavorite;

    public DiscoveredUser(String profileId, String sessionId, int numShared, boolean isFavorite) {
        this.profileId = profileId;
        this.sessionId = sessionId;
        this.numShared = numShared;
        this.isFavorite = isFavorite;
    }

    public String getProfileId() {
        return this.profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getNumShared() {
        return this.numShared;
    }

    public void setNumShared(int numShared) {
        this.numShared = numShared;
    }

    public boolean getIsFavorite() {
        return this.isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

}
