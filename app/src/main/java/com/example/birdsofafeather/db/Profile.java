package com.example.birdsofafeather.db;

import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

/**
 * This class is used to store Profile/User information
 */
@Entity(tableName = "PROFILE")
public class Profile {

    // UUID of user
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "profileId")
    private String profileId;

    // Name of user
    @ColumnInfo(name = "name")
    private String name;

    // Photo of user
    @ColumnInfo(name = "photo")
    private String photo;

    // Is this profile the self user?
    @ColumnInfo(name = "isSelf")
    private boolean isSelf;

    // Is this profile a favorite?
    @ColumnInfo(name = "isFavorite")
    private boolean isFavorite;

    // Did this profile wave at the user?
    @ColumnInfo(name = "isWaving")
    private boolean isWaving;

    // Did user wave at this profile?
    @ColumnInfo(name = "isWaved")
    private boolean isWaved;

    /**
     * Constructor used for testing
     *
     * @param name The name of user
     * @param photo The photo URL of user
     */
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

    /**
     * Constructor used for testing
     *
     * @param profileId The profile id of user
     * @param name The name of user
     * @param isSelf Whether the user is the self user
     * @param isFavorite Whether the user is a favorite user
     * @param isWaving Whether the user is waving
     * @param isWaved Whether the user is waved at by the self user
     */
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

    /**
     * Default constructor for Profile
     *
     * @param profileId The profile id of user
     * @param name The name of user
     * @param photo THe photo URL of user
     */
    public Profile (@NonNull String profileId, String name, String photo) {
        this.profileId = profileId;
        this.name = name;
        // Checks if the photo URL is valid, otherwise uses a placeholder/default photo
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

    /**
     * Gets the profile id
     *
     * @return The profile id
     */
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
     * Gets the name
     *
     * @return The name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name
     *
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the photo URL
     *
     * @return The photo URL
     */
    public String getPhoto() {
        return this.photo;
    }

    /**
     * Sets the photo URL
     *
     * @param photo The new photo URL
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * Gets the isSelf attribute
     *
     * @return The isSelf attribute
     */
    public boolean getIsSelf() {
        return this.isSelf;
    }

    /**
     * Sets the isSelf attribute
     *
     * @param isSelf The new isSelf attribute
     */
    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    /**
     * Gets the isFavorite attribute
     *
     * @return The isFavorite attribute
     */
    public boolean getIsFavorite() {
        return this.isFavorite;
    }

    /**
     * Sets the isFavorite attribute
     *
     * @param isFavorite The new isFavorite attribute
     */
    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    /**
     * Gets the isWaving attribute
     *
     * @return The isWaving attribute
     */
    public boolean getIsWaving() {
        return this.isWaving;
    }

    /**
     * Sets the isWaving attribute
     *
     * @param isWaving The new isWaving attribute
     */
    public void setIsWaving(boolean isWaving) {
        this.isWaving = isWaving;
    }

    /**
     * Gets the isWaved attribute
     *
     * @return The isWaved attribute
     */
    public boolean getIsWaved() {
        return this.isWaved;
    }

    /**
     * Sets the isWaved attribute
     *
     * @param isWaved The new isWaved attribute
     */
    public void setIsWaved(boolean isWaved) {
        this.isWaved = isWaved;
    }

    /**
     * Checks if two Profile objects are equal
     *
     * @param obj The other Profile object
     * @return Whether the two Profile objects are equal
     */
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