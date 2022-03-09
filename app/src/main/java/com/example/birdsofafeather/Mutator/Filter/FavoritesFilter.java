package com.example.birdsofafeather.Mutator.Filter;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.example.birdsofafeather.Utilities;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FavoritesFilter extends Filter {
    private Future<List<Pair<Profile, Integer>>> f1;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;


    public FavoritesFilter(Context context) {
        this.db = AppDatabase.singleton(context);
    }

    // TODO:
    @Override
    public synchronized List<Pair<Profile, Integer>> mutate(List<Profile> matches) {
        this.f1 = this.backgroundThreadExecutor.submit(() -> {
            List<Profile> favorites = this.db.profileDao().getFavoriteProfiles(true);
            List<Pair<Profile, Integer>> filtered = new ArrayList<>();
            for (Profile favorite : favorites) {
                int numSharedCourses = getNumSharedCoursesFromProfile(favorite);
                filtered.add(new Pair(favorite, numSharedCourses));
            }

            return filtered;
        });

        List<Pair<Profile, Integer>> newMatches = new ArrayList<>();
        try {
            newMatches = this.f1.get();
        } catch (Exception e) {
            Log.d("<FavoritesFilter>", "Unable to retrieve filtered matches!");
        }
        return newMatches;
    }

    // Helper method to get the shared courses between a profile and the user profile
    private int getNumSharedCoursesFromProfile(Profile match) {
        List<Course> matchCourses = this.db.courseDao().getCoursesByProfileId(match.getProfileId());
        String userId = this.db.profileDao().getUserProfile(true).getProfileId();
        List<Course> userCourses= this.db.courseDao().getCoursesByProfileId(userId);

        return Utilities.getNumSharedCourses(userCourses, matchCourses);
    }
}
