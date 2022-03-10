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
    private Future<List<Profile>> f1;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;
    private String currentQuarter;
    private String currentYear;
    private Context context;


    public CurrentQuarterFilter(Context context) {
        this.context = context;
        this.db = AppDatabase.singleton(context);
        this.currentQuarter = Utilities.getCurrentQuarter();
        this.currentYear = Utilities.getCurrentYear();
    }

    public CurrentQuarterFilter(AppDatabase db, String currentQuarter, String currentYear) {
        this.db = db;
        this.currentQuarter = currentQuarter;
        this.currentYear = currentYear;
    }

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
