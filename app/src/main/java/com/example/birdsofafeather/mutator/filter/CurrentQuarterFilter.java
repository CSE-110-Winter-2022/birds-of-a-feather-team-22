/*
 * This file is capable of filtering matches by the current quarter and year.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather.mutator.filter;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.example.birdsofafeather.mutator.sorter.QuantitySorter;
import com.example.birdsofafeather.Utilities;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Filters matches by those having at least one shared course with the user in the current quarter and year.
 * Extends the Filter abstract class.
 */
public class CurrentQuarterFilter extends Filter {

    // Instance variables of class
    private Future<List<Profile>> f1;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;
    private String currentQuarter;
    private String currentYear;
    private Context context;


    /**
     * Constructor for class.
     *
     * @param context A given context
     */
    public CurrentQuarterFilter(Context context) {
        this.context = context;
        this.db = AppDatabase.singleton(context);
        this.currentQuarter = Utilities.getCurrentQuarter();
        this.currentYear = Utilities.getCurrentYear();
    }

    /**
     * Constructor for class.
     *
     * @param db A given database
     * @param currentQuarter A given quarter
     * @param currentYear A given year
     */
    public CurrentQuarterFilter(AppDatabase db, String currentQuarter, String currentYear) {
        this.db = db;
        this.currentQuarter = currentQuarter;
        this.currentYear = currentYear;
    }

    /**
     * Provides a list of user profiles as well as the amount of classes shared with self user
     * and filters through them.
     *
     * @param matches List of profiles that have matches with user self
     * @return List of match profiles with corresponding number of shared courses
     */
    @Override
    public synchronized List<Pair<Profile, Integer>> mutate(List<Profile> matches) {
        this.f1 = this.backgroundThreadExecutor.submit(() -> {
           List<Profile> filtered = new ArrayList<>();
           for (Profile match : matches) {
               List<Course> sharedCourses = getSharedCoursesFromProfile(match);
               int numSharedCoursesThisQuarter = 0;
               for (Course course : sharedCourses) {
                   if (course.getQuarter().equals(this.currentQuarter) && course.getYear().equals(this.currentYear)) {
                       numSharedCoursesThisQuarter++;
                   }
               }

               if (numSharedCoursesThisQuarter > 0) {
                   filtered.add(match);
               }
           }

            return filtered;
        });

        try {
            List<Profile> filteredMatches = this.f1.get();

            return new QuantitySorter(context).mutate(filteredMatches);
        } catch (Exception e) {
            Log.d("<CurrentQuarterFilter>", "Unable to retrieve filtered matches!");
        }
        return new ArrayList<>();
    }

    /**
     * Helper in getting the shared courses between a profile and the self user profile.
     *
     * @param match A matched profile
     * @return List of courses that are shared
     */
    private List<Course> getSharedCoursesFromProfile(Profile match) {
        List<Course> matchCourses = this.db.courseDao().getCoursesByProfileId(match.getProfileId());
        String userId = this.db.profileDao().getSelfProfile(true).getProfileId();
        List<Course> userCourses= this.db.courseDao().getCoursesByProfileId(userId);

        return Utilities.getSharedCourses(userCourses, matchCourses);
    }
}
