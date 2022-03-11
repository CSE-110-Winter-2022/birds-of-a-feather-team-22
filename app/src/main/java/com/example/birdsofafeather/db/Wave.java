package com.example.birdsofafeather.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "WAVE")
public class Wave {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "profileId")
    private String profileId;

    @ColumnInfo(name = "wave")
    private String wave;

    public Wave(@NonNull String profileId, String wave) {
        this.profileId = profileId;
        this.wave = wave;
    }

    @NonNull
    public String getProfileId() {
        return this.profileId;
    }

    public void setProfileId(@NonNull String profileId) {
        this.profileId = profileId;
    }

    public String getWave() {
        return this.wave;
    }

    public void setWave(String wave) {
        this.wave = wave;
    }
}
