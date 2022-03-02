package com.example.birdsofafeather;

import android.app.Activity;
import android.app.AlertDialog;

import com.example.birdsofafeather.db.Course;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    public static List<String> getCurrentQuarter() {
        List<String> quarter = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("MMddyyyy");
        String date = df.format(Calendar.getInstance().getTime());
        int month = Integer.parseInt(date.substring(0, 2));
        int day = Integer.parseInt(date.substring(2, 4));
        String year = date.substring(4, 8);
        if (month >= 1 && month <= 3) {
            if (month == 3 && day > 20) {
                quarter.add("Spring");
            } else {
                quarter.add("Winter");
            }
        }
        else if (month >= 4 && month <= 6) {
            if (month == 6 && day >= 15) {
                quarter.add("Summer Session 1");
            }
            else {
                quarter.add("Spring");
            }
        }
        else if (month >= 7 && month <= 8) {
            if (month == 8 && day >= 6) {
                quarter.add("Summer Session 2");
            }
            else {
                quarter.add("Summer Session 1");
            }
        }
        else if (month >= 8 && month <= 9) {
            if (month == 9 && day >= 20) {
                quarter.add("Fall");
            }
            else {
                quarter.add("Summer Session 2");
            }
        }
        else {
            quarter.add("Fall");
        }
        quarter.add(year);

        return quarter;
    }
}
