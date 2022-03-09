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

// Specialized sorting algorithm that sorts matches based upon the recency of shared courses
public class RecencySorter extends Sorter {
    private Future<List<Pair<Profile, Integer>>> f;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;

    public RecencySorter(Context context) {
        this.db = AppDatabase.singleton(context);
    }

    @Override
    public synchronized List<Pair<Profile, Integer>> mutate(List<Profile> matches) {
        this.f = backgroundThreadExecutor.submit(() -> {
            List<Pair<Profile, Integer>> matchScorePairs = new ArrayList<>();
            for (Profile match : matches) {
                int matchScore = 0;
                List<Course> sharedCourses = getSharedCoursesFromProfile(match);

                for (Course course : sharedCourses) {
                    matchScore += calculateRecencyScore(course);
                }

                matchScorePairs.add(new Pair(match, matchScore));
            }

            return matchScorePairs;
        });

        List<Pair<Profile, Integer>> sortedMatchScorePairs = new ArrayList<>();
        try {
            sortedMatchScorePairs = this.f.get();
        } catch (Exception e) {
            Log.d("<RecencySorter>", "Unable to retrieve unsorted match-score pairs!");
        }

        sortedMatchScorePairs.sort(new MatchesComparator());

        List<Pair<Profile, Integer>> newMatchPairs = new ArrayList<>();
        for (Pair<Profile, Integer> pair : sortedMatchScorePairs) {
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

    // Helper method to calculate the recency score per MS2 Planning Phase writeup for a Course
    private int calculateRecencyScore(Course course) {
        String currentQuarter = Utilities.getCurrentQuarter();
        String currentYear = Utilities.getCurrentYear();
        String courseYear = course.getYear();
        String courseQuarter = course.getQuarter();
        int curr = Utilities.enumerateQuarter(currentQuarter);
        int cour = Utilities.enumerateQuarter(courseQuarter);

        int numQuartersAgo = 4 * (Integer.parseInt(currentYear) - Integer.parseInt(courseYear)) + (curr - cour);
        switch (numQuartersAgo) {
            case 0:
                return 5;
            case 1:
                return 4;
            case 2:
                return 3;
            case 3:
                return 2;
            default:
                return 1;
        }
    }
}
