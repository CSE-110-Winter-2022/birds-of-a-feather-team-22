/*
 * This file is capable of organizing the contents found within a profile, allowing
 * for setting of fields, modification, and retrieval of data.
 *
 *
 */

package com.example.birdsofafeather.db;

import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

/*
 * This class is capable of storing and retrieving profile information.
 */
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

    @ColumnInfo(name = "isFavorite")
    private boolean isFavorite;

    // Did this profile wave at the user?
    @ColumnInfo(name = "isWaving")
    private boolean isWaving;

    // Did user wave at this profile?
    @ColumnInfo(name = "isWaved")
    private boolean isWaved;

    /**
     * Constructor for class.
     *
     * @param name A given name
     * @param photo A given photo url
     * @return none
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
        this.isUser = false;
        this.isFavorite = false;
        this.isWaving = false;
        this.isWaved = false;
    }

    /**
     * Constructor for class.
     * @param profileId A given profile ID
     * @param name A given name
     * @param isUser Whether the profile is the self user
     * @param isFavorite Whether the profile is a favorite
     * @param isWaving Whether the self user is waving at profile
     * @param isWaved Whether the profile is waving at the self user
     * @return none
     */
    @Ignore
    public Profile (String profileId, String name, boolean isUser, boolean isFavorite, boolean isWaving, boolean isWaved) {
        this.profileId = profileId;
        this.name = name;
        this.photo = "https://i.imgur.com/MZH5yxZ.png";
        this.isUser = isUser;
        this.isFavorite = isFavorite;
        this.isWaving = isWaving;
        this.isWaved = isWaved;
    }

    /**
     * Constructor for class.
     *
     * @param profileId A given profile ID
     * @param name A given name
     * @param photo A given photo url
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
        this.isUser = false;
        this.isFavorite = false;
        this.isWaving = false;
        this.isWaved = false;
    }

    /**
     * Returns the ID of the profile.
     *
     * @param
     * @return The ID of profile
     */
    public String getProfileId() {
        return this.profileId;
    }

    /**
     * Sets the ID of the profile.
     *
     * @param profileId A given profile ID
     * @return none
     */
    public void setProfileId(@NonNull String profileId) {
        this.profileId = profileId;
    }

    /**
     * Returns the name under the profile.
     *
     * @param
     * @return Name under profile
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name under the profile.
     *
     * @param name A given name
     * @return none
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the photo url of the profile.
     *
     * @param
     * @return Url of photo under profile
     */
    public String getPhoto() {
        return this.photo;
    }

    /**
     * Sets the url of the photo under the profile.
     *
     * @param photo A url to a photo
     * @return none
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * Returns whether or not the profile is the self user.
     *
     * @param
     * @return Whether the profile is a self user
     */
    public boolean getIsUser() {
        return this.isUser;
    }

    /**
     * Sets whether the profile is the self user.
     *
     * @param isUser Whether it is a self user
     * @return none
     */
    public void setIsUser(boolean isUser) {
        this.isUser = isUser;
    }

    /**
     * Returns whether the profile is a favorite.
     *
     * @param
     * @return Whether the profile is a favorite
     */
    public boolean getIsFavorite() {
        return this.isFavorite;
    }

    /**
     * Sets whether the profile is a favorite or not.
     *
     * @param isFavorite The status of being a favorite
     * @return none
     */
    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    /**
     * Returns whether the profile is waving.
     *
     * @param
     * @return Returns whether the profile is waving
     */
    public boolean getIsWaving() {
        return this.isWaving;
    }

    /**
     * Sets whether or not the profile is waving.
     *
     * @param isWaving The status of waving
     * @return none
     */
    public void setIsWaving(boolean isWaving) {
        this.isWaving = isWaving;
    }

    /**
     * Returns whether the profile has been waved at.
     *
     * @param
     * @return Whether the profile has been waved at
     */
    public boolean getIsWaved() {
        return this.isWaved;
    }

    /**
     * Sets whether or not the profile has been waved at.
     *
     * @param isWaved Status of being waved at
     * @return none
     */
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
                this.isUser == other.getIsUser() &&
                this.isFavorite == other.getIsFavorite() &&
                this.isWaving == other.getIsWaving() &&
                this.isWaved == other.getIsWaved();
    }

}