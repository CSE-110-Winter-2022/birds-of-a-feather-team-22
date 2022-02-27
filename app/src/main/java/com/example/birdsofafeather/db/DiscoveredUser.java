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

    @ColumnInfo(name = "numShared")
    private int numShared;


    public DiscoveredUser(String profileId, int numShared) {
        this.profileId = profileId;
        this.numShared = numShared;
    }

    public String getProfileId() {
        return this.profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public int getNumShared() {
        return this.numShared;
    }

    public void setNumShared(int numShared) {
        this.numShared = numShared;
    }

}
