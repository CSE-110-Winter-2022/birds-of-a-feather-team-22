package com.example.birdsofafeather.db;

import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

// Profile class used to store profile information
@Entity(tableName = "PROFILE")
public class Profile {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "profileId")
    private String profileId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "photo")
    private String photo;

    @ColumnInfo(name = "isUser")
    private boolean isUser;


    @Ignore
    public Profile (String name, String photo) {
        this.profileId = UUID.randomUUID().toString();;
        this.name = name;
        if (!URLUtil.isValidUrl(photo)) {
            this.photo = "https://i.imgur.com/MZH5yxZ.png";
        }
        else {
            this.photo = photo;
        }
        this.isUser = false;

    }

    // Checks if the photo URL is valid, otherwise uses a placeholder/default photo
    public Profile (String profileId, String name, String photo) {
        this.profileId = profileId;
        this.name = name;
        if (!URLUtil.isValidUrl(photo)) {
            this.photo = "https://i.imgur.com/MZH5yxZ.png";
        }
        else {
            this.photo = photo;
        }
        this.isUser = false;
    }

    public String getProfileId() {
        return this.profileId;
    }

    public void setProfileId(String profileId) {
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

    public boolean getIsUser() {
        return this.isUser;
    }
    public void setIsUser(boolean isUser) {
        this.isUser = isUser;
    }

}