package com.example.birdsofafeather.db;

import android.webkit.URLUtil;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Profile class used to store profile information
@Entity(tableName = "PROFILE")
public class Profile {

    @PrimaryKey
    @ColumnInfo(name = "profileId")
    private int profileId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "photo")
    private String photo;


    // Checks if the photo URL is valid, otherwise uses a placeholder/default photo
    public Profile(int profileId, String name, String photo) {
        this.profileId = profileId;
        this.name = name;
        if (!URLUtil.isValidUrl(photo)) {
            this.photo = "https://imgur/.com/a/vgBKZMN";
        }
        else {
            this.photo = photo;
        }

    }

    public int getProfileId() {
        return this.profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}