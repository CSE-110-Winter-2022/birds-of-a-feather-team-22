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

    @ColumnInfo(name = "isLastSession")
    private boolean isLastSession;

    @ColumnInfo(name = "sortFilter")
    private String sortFilter;

    public Session(@NonNull String sessionId, String name, boolean isLastSession) {
        this.sessionId = sessionId;
        this.name = name;
        this.isLastSession = isLastSession;
        this.sortFilter = "No Sort/Filter";
    }

    @NonNull
    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(@NonNull String sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsLastSession() {
        return this.isLastSession;
    }

    public void setIsLastSession(boolean isLastSession) {
        this.isLastSession = isLastSession;
    }

    public String getSortFilter() {
        return this.sortFilter;
    }

    public void setSortFilter(String sortFilter) {
        this.sortFilter = sortFilter;
    }
}
