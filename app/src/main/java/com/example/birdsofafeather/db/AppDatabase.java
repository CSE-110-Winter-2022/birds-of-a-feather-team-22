/*
 * This file is capable of creating databases for actual application user and for testing purposes.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/*
 * This class allows for storing of courses, profiles, and discovered users.
 */
@Database(entities = {Course.class, Profile.class, DiscoveredUser.class, Session.class, Wave.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase singletonInstance;

    /**
     * Creates a database for usage from a given context.
     *
     * @param context The given context
     * @return A database
     */
    public static AppDatabase singleton(Context context){
        if (singletonInstance == null) {
            singletonInstance = Room.databaseBuilder(context, AppDatabase.class, "profile.db")
                    .allowMainThreadQueries()
                    .build();
        }

        return singletonInstance;
    }

    /**
     * Creates a database for testing purposes from a given context.
     *
     * @param context A given context
     * @return A database
     */
    public static AppDatabase useTestSingleton(Context context){
        singletonInstance = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        return singletonInstance;
    }

    /**
     * Provides a CourseDao object.
     *
     * @return A CourseDao object
     */
    public abstract CourseDao courseDao();

    /**
     * Provides a ProfileDao object.
     *
     * @return A ProfileDao object
     */
    public abstract ProfileDao profileDao();

    /**
     * Provides a DiscoveredUserDao object.
     *
     * @return A DiscoveredUserDao object.
     */
    public abstract DiscoveredUserDao discoveredUserDao();

    /**
     * Provides a SessionDao object.
     *
     * @return A SessionDao object
     */
    public abstract SessionDao sessionDao();

    /**
     * Provides a WaveDao object.
     *
     * @return A WaveDao object
     */
    public abstract WaveDao waveDao();
}
