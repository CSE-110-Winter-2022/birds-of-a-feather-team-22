package com.example.birdsofafeather;

import android.app.Activity;
import android.app.AlertDialog;

import com.example.birdsofafeather.db.Course;

import java.util.ArrayList;
import java.util.List;

// A collection of static method for general use
public class Utilities {

    public static AlertDialog mostRecentDialog = null;

    // Show an alert message
    public static void showAlert(Activity activity, String message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, id) -> dialog.cancel())
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();

        alertDialog.show();

        mostRecentDialog = alertDialog;
    }

    // Show an error message
    public static AlertDialog showError(Activity activity, String title, String message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, id) -> dialog.cancel())
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
        mostRecentDialog = alertDialog;
        return alertDialog;


    }

    // Get the number of shared courses between two lists of courses
    public static int getNumSharedCourses(List<Course> myCourses, List<Course> theirCourses) {
        return getSharedCourses(myCourses, theirCourses).size();
    }

    // Get the list of shared courses between two lists of courses
    public static List<Course> getSharedCourses(List<Course> myCourses, List<Course> theirCourses)  {
        List<Course> sharedCourses = new ArrayList<>();

        for (Course myCourse : myCourses) {
            for (Course theirCourse : theirCourses) {
                if (compareCourses(myCourse, theirCourse)) {
                    sharedCourses.add(myCourse);
                }
            }
        }

        return sharedCourses;

    }

    // Compare if two courses are equal
    public static boolean compareCourses(Course c1, Course c2) {
        return c1.getYear().equals(c2.getYear()) && c1.getQuarter().equals(c2.getQuarter()) &&
                c1.getSubject().equals(c2.getSubject()) && c1.getNumber().equals(c2.getNumber());
    }
}
