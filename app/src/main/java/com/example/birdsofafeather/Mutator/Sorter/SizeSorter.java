package com.example.birdsofafeather.mutator.sorter;

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

// Specialized sorting algorithm that sorts matches based upon the size of shared courses
public class SizeSorter extends Sorter {
    private Future<List<Pair<Profile, Double>>> f;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;

    public SizeSorter(Context context) {
        this.db = AppDatabase.singleton(context);
    }

    // For testing
    public SizeSorter(AppDatabase db) {
        this.db = db;
    }

    @Override
    public synchronized List<Pair<Profile, Integer>> mutate(List<Profile> matches) {
        this.f = backgroundThreadExecutor.submit(() -> {
            List<Pair<Profile, Double>> matchScorePairs = new ArrayList<>();
            for (Profile match : matches) {
                double matchScore = 0;
                List<Course> sharedCourses = getSharedCoursesFromProfile(match);

                for (Course course : sharedCourses) {
                    matchScore += calculateSizeScore(course);
                }

                matchScorePairs.add(new Pair(match, matchScore));
            }

            return matchScorePairs;
        });

        List<Pair<Profile, Double>> sortedMatchScorePairs = new ArrayList<>();
        try {
            sortedMatchScorePairs = this.f.get();
        } catch (Exception e) {
            Log.d("<SizeSorter>", "Unable to retrieve unsorted match-score pairs!");
        }

        sortedMatchScorePairs.sort(new DoubleMatchesComparator());

        List<Pair<Profile, Integer>> newMatchPairs = new ArrayList<>();
        for (Pair<Profile, Double> pair : sortedMatchScorePairs) {
            newMatchPairs.add(new Pair(pair.first, getNumSharedCoursesFromProfile(pair.first)));
        }

        return newMatchPairs;
    }


    // Helper method to get the shared courses between a profile and the user profile
    private List<Course> getSharedCoursesFromProfile(Profile match) {
        List<Course> matchCourses = this.db.courseDao().getCoursesByProfileId(match.getProfileId());
        String userId = this.db.profileDao().getUserProfile(true).getProfileId();
        List<Course> userCourses= this.db.courseDao().getCoursesByProfileId(userId);

        return Utilities.getSharedCourses(userCourses, matchCourses);
    }

    // Helper method to get the shared courses between a profile and the user profile
    private int getNumSharedCoursesFromProfile(Profile match) {
        List<Course> matchCourses = this.db.courseDao().getCoursesByProfileId(match.getProfileId());
        String userId = this.db.profileDao().getUserProfile(true).getProfileId();
        List<Course> userCourses= this.db.courseDao().getCoursesByProfileId(userId);

        return Utilities.getNumSharedCourses(userCourses, matchCourses);
    }


    // Helper method to calculate the size score per MS2 Planning Phase writeup for a Course
    private double calculateSizeScore(Course course) {
        switch (course.getClassSize()) {
            case "Tiny":
                return 1.0;
            case "Small":
                return 0.33;
            case "Medium":
                return 0.18;
            case "Large":
                return 0.10;
            case "Huge":
                return 0.06;
            case "Gigantic":
                return 0.03;
            default:
                return 0;
        }
    }
}
