package com.example.birdsofafeather.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Room database for storing courses, profiles, and discovered users
@Database(entities = {Course.class, Profile.class, DiscoveredUser.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase singletonInstance;

    public static AppDatabase singleton(Context context){
        if (singletonInstance == null) {
            singletonInstance = Room.databaseBuilder(context, AppDatabase.class, "profile.db")
                    .allowMainThreadQueries()
                    .build();
        }

        return singletonInstance;
    }

    public static AppDatabase useTestSingleton(Context context){
        singletonInstance = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        return singletonInstance;
    }

    public abstract CourseDao courseDao();
    public abstract ProfileDao profileDao();
    public abstract DiscoveredUserDao discoveredUserDao();
}
