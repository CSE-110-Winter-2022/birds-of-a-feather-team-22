/*
 * This file is capable of implementing the sorting of profiles with the sizes of shared courses.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

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

/*
 * Class provides a specialized sorting algorithm that sorts matches based upon the size of shared
 * courses.
 */
public class SizeSorter extends Sorter {

    // Instance variables for class
    private Future<List<Pair<Profile, Double>>> f;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;

    /**
     * Constructor for class.
     *
     * @param context A given context
     */
    public SizeSorter(Context context) {
        this.db = AppDatabase.singleton(context);
    }

    /**
     * Constructor for class. Used for testing purposes
     *
     * @param db A given database
     */
    public SizeSorter(AppDatabase db) {
        this.db = db;
    }

    /**
     * Filters through the profiles by the scores of the course sizes and provides modifications to
     * a list.
     * @param matches List of profiles with at least one shared course
     * @return List of profiles with application of scores by course size
     */
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


    /**
     * Helps in getting the shared courses between a profile and the user self profile.
     * @param match A profile object that is a match
     * @return List of shared courses
     */
    private List<Course> getSharedCoursesFromProfile(Profile match) {
        List<Course> matchCourses = this.db.courseDao().getCoursesByProfileId(match.getProfileId());
        String userId = this.db.profileDao().getSelfProfile(true).getProfileId();
        List<Course> userCourses= this.db.courseDao().getCoursesByProfileId(userId);

        return Utilities.getSharedCourses(userCourses, matchCourses);
    }

    /**
     * Helpers in getting the number of shared courses between a profile and the user self profile.
     *
     * @param match A profile object that is a match
     * @return Number of shared courses
     */
    private int getNumSharedCoursesFromProfile(Profile match) {
        List<Course> matchCourses = this.db.courseDao().getCoursesByProfileId(match.getProfileId());
        String userId = this.db.profileDao().getSelfProfile(true).getProfileId();
        List<Course> userCourses= this.db.courseDao().getCoursesByProfileId(userId);

        return Utilities.getNumSharedCourses(userCourses, matchCourses);
    }

    /**
     * Helps in calculating the size score per MS2 Planning Phase writeup for a course.
     *
     * @param course A given course object
     * @return The score based on class size
     */
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
