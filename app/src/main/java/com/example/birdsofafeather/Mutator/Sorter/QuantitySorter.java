package com.example.birdsofafeather.Mutator.Sorter;

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

// Default sorting algorithm that sorts matches based upon the quantity of shared courses
public class QuantitySorter extends Sorter {
    private Future<List<Pair<Profile, Integer>>> f1;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;

    public QuantitySorter(Context context) {
        this.db = AppDatabase.singleton(context);
    }

    @Override
    public List<Pair<Profile, Integer>> mutate(List<Profile> matches) {
        this.f1 = backgroundThreadExecutor.submit(() -> {
            List<Pair<Profile, Integer>> matchQuantityPairs = new ArrayList<>();
            for (Profile match : matches) {
                int quantity = getNumSharedCoursesFromProfile(match);
                matchQuantityPairs.add(new Pair(match, quantity));
            }

            return matchQuantityPairs;
        });

        List<Pair<Profile, Integer>> pairs = new ArrayList<>();
        try {
            pairs = this.f1.get();
        } catch (Exception e) {
            Log.d("<QuantitySorter>", "Unable to retrieve unsorted match-quantity pairs!");
        }

        pairs.sort(new MatchesComparator());

        return pairs;
    }

    private int getNumSharedCoursesFromProfile(Profile match) {
        List<Course> matchCourses = this.db.courseDao().getCoursesByProfileId(match.getProfileId());
        String userId = this.db.profileDao().getUserProfile(true).getProfileId();
        List<Course> userCourses= this.db.courseDao().getCoursesByProfileId(userId);

        return Utilities.getNumSharedCourses(userCourses, matchCourses);
    }
}
