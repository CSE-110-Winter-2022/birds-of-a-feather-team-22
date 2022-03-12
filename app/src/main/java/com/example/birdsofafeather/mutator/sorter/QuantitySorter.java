/*
 * This file is capable of implementing the sorting of profiles in pair objects, with the
 * corresponding number of courses shared.
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
 * Class providing default sorting algorithm that sorts matches based upon the quantity of shared
 * courses
 */
public class QuantitySorter extends Sorter {

    // Instance variables of class
    private Future<List<Pair<Profile, Integer>>> f1;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;

    /**
     * Constructor for class.
     * @param context A given context
     */
    public QuantitySorter(Context context) {
        this.db = AppDatabase.singleton(context);
    }

    /**
     * Constrcutor for class. Used for testing.
     *
     * @param db A given database
     */
    public QuantitySorter(AppDatabase db) {
        this.db = db;
    }

    /**
     * Filters through the profiles by number of shared courses in descending order.
     *
     * @param matches List of profiles that have at least one shared course with user self
     * @return List of pair objects with profiles and their corresponding number of shared courses
     * with user self
     */
    @Override
    public synchronized List<Pair<Profile, Integer>> mutate(List<Profile> matches) {
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

    /**
     * Returns the number of shared courses from a profile that has been matched.
     *
     * @param match A given profile match
     * @return Number of shared courses between match and user self
     */
    private int getNumSharedCoursesFromProfile(Profile match) {
        List<Course> matchCourses = this.db.courseDao().getCoursesByProfileId(match.getProfileId());
        String userId = this.db.profileDao().getSelfProfile(true).getProfileId();
        List<Course> userCourses= this.db.courseDao().getCoursesByProfileId(userId);

        return Utilities.getNumSharedCourses(userCourses, matchCourses);
    }

}
