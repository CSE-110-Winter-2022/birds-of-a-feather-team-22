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

/**
 * Filters matches by those having at least one shared course with the user in the current quarter and year.
 * Extends the Filter abstract class.
 */
public class CurrentQuarterFilter extends Filter {
    private Future<List<Pair<Profile, Integer>>> f1;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;


    public CurrentQuarterFilter(Context context) {
        this.db = AppDatabase.singleton(context);
    }

    @Override
    public List<Pair<Profile, Integer>> mutate(List<Profile> matches) {
        String currentQuarter = Utilities.getCurrentQuarter();
        String currentYear = Utilities.getCurrentYear();

        this.f1 = this.backgroundThreadExecutor.submit(() -> {
           List<Pair<Profile, Integer>> filtered = new ArrayList<>();
           for (Profile match : matches) {
               List<Course> sharedCourses = getSharedCoursesFromProfile(match);
               int numSharedCourses = getNumSharedCoursesFromProfile(match);
               for (Course course : sharedCourses) {
                   if (course.getQuarter().equals(currentQuarter) && course.getYear().equals(currentYear)) {
                       filtered.add(new Pair(match, numSharedCourses));
                   }
               }
           }

            return filtered;
        });

        List<Pair<Profile, Integer>> newMatches = new ArrayList<>();
        try {
            newMatches = this.f1.get();
        } catch (Exception e) {
            Log.d("<CurrentQuarterFilter>", "Unable to retrieve filtered matches!");
        }
        return newMatches;
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
}
