/*
 * This file is capable of implementing the sorting of profiles with recency of shared courses.
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
 * Class providing a specialized sorting algorithm that sorts matches based upon recency of shared courses
 * between profiles and user self profile.
 */
public class RecencySorter extends Sorter {

    // Instance variables for class
    private Future<List<Pair<Profile, Integer>>> f;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;

    /**
     * Constructor for class.
     *
     * @param context A given context
     */
    public RecencySorter(Context context) {
        this.db = AppDatabase.singleton(context);
    }

    /**
     * Constructor for class. Used for testing purposes
     *
     * @param db A given database
     */
    public RecencySorter(AppDatabase db) {
        this.db = db;
    }

    /**
     * Filters through the profiles by the recency of the shared courses and provides modified
     * list.
     *
     * @param matches List of profiles with at least one shared course
     * @return List of profiles with application of recency of shared courses
     */
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

    /**
     * Helps in getting the shared courses between a profile and the user self profile.
     *
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
     * Helps in getting the number of shared courses between a profile and the user self profile.
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
     * Helps in calculating the recency score per MS2 Planning Phase writeup for a course.
     *
     * @param course A given course object
     * @return The score base on course's recency
     */
    public int calculateRecencyScore(Course course) {
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
