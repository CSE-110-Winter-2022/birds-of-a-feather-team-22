package com.example.birdsofafeather.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SESSION")
public class Session {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sessionId")
    private String sessionId;

    @ColumnInfo(name = "name")
    private String name;

    public Session(String sessionId, String name) {
        this.sessionId = sessionId;
        this.name = name;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}