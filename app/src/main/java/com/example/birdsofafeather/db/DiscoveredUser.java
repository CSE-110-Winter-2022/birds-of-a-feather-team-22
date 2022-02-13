package com.example.birdsofafeather.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// DiscoveredUser class used to store a previously discovered user
@Entity(tableName = "DISCOVEREDUSER")
public class DiscoveredUser {
    @PrimaryKey
    @ColumnInfo(name = "profileId")
    private int profileId;

    @ColumnInfo(name = "numShared")
    private int numShared;


    public DiscoveredUser(int profileId, int numShared) {
        this.profileId = profileId;
        this.numShared = numShared;
    }

    public int getProfileId() {
        return this.profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getNumShared() {
        return this.numShared;
    }

    public void setNumShared(int numShared) {
        this.numShared = numShared;
    }

}
