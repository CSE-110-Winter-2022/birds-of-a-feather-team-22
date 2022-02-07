package com.example.birdsofafeather.db;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Course.class, Profile.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase singletonInstance;

    public static AppDatabase singleton(Context context){
        if (singletonInstance == null){
            singletonInstance = Room.databaseBuilder(context, AppDatabase.class, "profile.db")
                    .build();
        }
        return singletonInstance;
    }

    public abstract CourseDao courseDao();

    public abstract ProfileDao profileDao();

}