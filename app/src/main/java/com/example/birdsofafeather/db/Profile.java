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

    @ColumnInfo(name = "isSelf")
    private boolean isSelf;

    @ColumnInfo(name = "isFavorite")
    private boolean isFavorite;

    // Did this profile wave at the user?
    @ColumnInfo(name = "isWaving")
    private boolean isWaving;

    // Did user wave at this profile?
    @ColumnInfo(name = "isWaved")
    private boolean isWaved;

    @Ignore
    public Profile (String name, String photo) {
        this.profileId = UUID.randomUUID().toString();
        this.name = name;
        if (!URLUtil.isValidUrl(photo)) {
            this.photo = "https://i.imgur.com/MZH5yxZ.png";
        }
        else {
            this.photo = photo;
        }
        this.isSelf = false;
        this.isFavorite = false;
        this.isWaving = false;
        this.isWaved = false;
    }

    @Ignore
    public Profile (String profileId, String name, boolean isSelf, boolean isFavorite, boolean isWaving, boolean isWaved) {
        this.profileId = profileId;
        this.name = name;
        this.photo = "https://i.imgur.com/MZH5yxZ.png";
        this.isSelf = isSelf;
        this.isFavorite = isFavorite;
        this.isWaving = isWaving;
        this.isWaved = isWaved;
    }

    // Checks if the photo URL is valid, otherwise uses a placeholder/default photo
    public Profile (@NonNull String profileId, String name, String photo) {
        this.profileId = profileId;
        this.name = name;
        if (!URLUtil.isValidUrl(photo)) {
            this.photo = "https://i.imgur.com/MZH5yxZ.png";
        }
        else {
            this.photo = photo;
        }
        this.isSelf = false;
        this.isFavorite = false;
        this.isWaving = false;
        this.isWaved = false;
    }

    public String getProfileId() {
        return this.profileId;
    }

    public void setProfileId(@NonNull String profileId) {
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

    public boolean getIsSelf() {
        return this.isSelf;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    public boolean getIsFavorite() {
        return this.isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean getIsWaving() {
        return this.isWaving;
    }

    public void setIsWaving(boolean isWaving) {
        this.isWaving = isWaving;
    }

    public boolean getIsWaved() {
        return this.isWaved;
    }

    public void setIsWaved(boolean isWaved) {
        this.isWaved = isWaved;
    }

    @Override
    public boolean equals(Object obj)
    {
        Profile other = (Profile) obj;
        return this.profileId.equals(other.getProfileId()) &&
                this.name.equals(other.getName()) &&
                this.photo.equals(other.getPhoto()) &&
                this.isSelf == other.getIsSelf() &&
                this.isFavorite == other.getIsFavorite() &&
                this.isWaving == other.getIsWaving() &&
                this.isWaved == other.getIsWaved();
    }

}