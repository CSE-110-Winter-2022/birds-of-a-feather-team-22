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

// Specialized sorting algorithm that sorts matches based upon the size of shared courses
public class SizeSorter extends Sorter {
    private Future<List<Pair<Profile, Integer>>> f;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;

    public SizeSorter(Context context) {
        this.db = AppDatabase.singleton(context);
    }

    @Override
    public List<Pair<Profile, Integer>> mutate(List<Profile> matches) {
        this.f = backgroundThreadExecutor.submit(() -> {
            List<Pair<Profile, Integer>> matchScorePairs = new ArrayList<>();
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

        List<Pair<Profile, Integer>> newMatchPairs = new ArrayList<>();
        try {
            newMatchPairs = this.f.get();
        } catch (Exception e) {
            Log.d("<SizeSorter>", "Unable to retrieve unsorted match-score pairs!");
        }

        newMatchPairs.sort(new MatchesComparator());

        return newMatchPairs;
    }


    // Helper method to get the shared courses between a profile and the user profile
    private List<Course> getSharedCoursesFromProfile(Profile match) {
        List<Course> matchCourses = this.db.courseDao().getCoursesByProfileId(match.getProfileId());
        String userId = this.db.profileDao().getUserProfile(true).getProfileId();
        List<Course> userCourses= this.db.courseDao().getCoursesByProfileId(userId);

        return Utilities.getSharedCourses(userCourses, matchCourses);
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
