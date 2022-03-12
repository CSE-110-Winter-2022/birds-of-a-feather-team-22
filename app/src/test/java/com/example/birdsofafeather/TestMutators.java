package com.example.birdsofafeather;

import android.content.Context;
import android.util.Pair;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.mutator.filter.CurrentQuarterFilter;
import com.example.birdsofafeather.mutator.filter.FavoritesFilter;
import com.example.birdsofafeather.mutator.Mutator;
import com.example.birdsofafeather.mutator.sorter.QuantitySorter;
import com.example.birdsofafeather.mutator.sorter.RecencySorter;
import com.example.birdsofafeather.mutator.sorter.SizeSorter;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class TestMutators {

    public Context context = ApplicationProvider.getApplicationContext();
    public AppDatabase db = AppDatabase.useTestSingleton(context);
    public Profile bill = new Profile("a", "Bill", false, false, false, false);
    public Profile will = new Profile("b", "Will", false, false, false, false);
    public Profile jill = new Profile("c", "Jill", false, true, false, false);
    public Profile dill = new Profile("d", "Dill", false, true, false, false);
    public Profile self = new Profile("self", "Me", true, false, false, false);

    @Test
    public void TestQuantitySorter() {
        List<Profile> matches = new ArrayList<>();
        Mutator sorter = new QuantitySorter(this.db);

        this.db.profileDao().insert(bill);
        this.db.profileDao().insert(will);
        this.db.profileDao().insert(jill);
        this.db.profileDao().insert(dill);
        matches.add(bill);
        matches.add(will);
        matches.add(jill);
        matches.add(dill);

        this.db.courseDao().insert(new Course("a", "2022", "Winter", "CSE", "110", "Large"));
        this.db.courseDao().insert(new Course("b", "2021", "Fall", "CSE", "140", "Large"));
        this.db.courseDao().insert(new Course("b", "2021", "Fall", "CSE", "140L", "Large"));
        this.db.courseDao().insert(new Course("c", "2021", "Winter", "CSE", "12", "Large"));
        this.db.courseDao().insert(new Course("c", "2021", "Winter", "CSE", "15L", "Large"));
        this.db.courseDao().insert(new Course("d", "2021", "Fall", "CSE", "140L", "Large"));

        List<Pair<Profile, Integer>> result = new ArrayList<>();
        result.add(new Pair(bill, 3));
        result.add(new Pair(will, 2));
        result.add(new Pair(jill, 1));
        result.add(new Pair(dill, 0));

        List<Pair<Profile, Integer>> sorted = sorter.mutate(matches);

        int prev = 0;
        for (int i = 0; i < sorted.size(); i++) {
            Pair<Profile, Integer> sortedPair = sorted.get(i);
            Pair<Profile, Integer> resultPair = result.get(i);
            assertEquals(sortedPair.first, resultPair.first);
            assertSame(sortedPair.second, resultPair.second);
            assertTrue(sortedPair.second >= prev);
            prev = sortedPair.second;
        }
        this.db.clearAllTables();
    }

    @Test
    public void TestRecencySorter() {
        List<Profile> matches = new ArrayList<>();
        Mutator sorter = new RecencySorter(this.db);
        this.db.profileDao().insert(self);
        this.db.profileDao().insert(bill);
        this.db.profileDao().insert(will);
        this.db.profileDao().insert(jill);
        this.db.profileDao().insert(dill);
        matches.add(bill);
        matches.add(will);
        matches.add(jill);
        matches.add(dill);

        this.db.courseDao().insert(new Course("self", "2022", "Winter", "CSE", "110", "Large"));
        this.db.courseDao().insert(new Course("self", "2021", "Fall", "CSE", "140", "Large"));
        this.db.courseDao().insert(new Course("self", "2021", "Summer Session 1", "CSE", "101", "Large"));
        this.db.courseDao().insert(new Course("self", "2021", "Winter", "CSE", "12", "Large"));
        this.db.courseDao().insert(new Course("self", "2021", "Winter", "CSE", "15L", "Large"));
        this.db.courseDao().insert(new Course("self", "2020", "Fall", "CSE", "11", "Large"));
        this.db.courseDao().insert(new Course("self", "2021", "Fall", "CSE", "120", "Large"));

        this.db.courseDao().insert(new Course("a", "2022", "Winter", "CSE", "110", "Large"));
        this.db.courseDao().insert(new Course("b", "2021", "Fall", "CSE", "140", "Large"));
        this.db.courseDao().insert(new Course("b", "2021", "Summer Session 1", "CSE", "101", "Large"));
        this.db.courseDao().insert(new Course("c", "2021", "Winter", "CSE", "12", "Large"));
        this.db.courseDao().insert(new Course("c", "2021", "Winter", "CSE", "15L", "Large"));
        this.db.courseDao().insert(new Course("c", "2020", "Fall", "CSE", "11", "Large"));
        this.db.courseDao().insert(new Course("d", "2021", "Fall", "CSE", "120", "Large"));

        List<Integer> expectedRecencyScores = Arrays.asList(5, 7, 3, 4);
        for (int i = 0; i < matches.size(); i++) {
            assertEquals(expectedRecencyScores.get(i), Integer.valueOf(calculateRecencyScore(matches.get(i))));
        }

        List<Pair<Profile, Integer>> result = new ArrayList<>();
        result.add(new Pair(will, 2));
        result.add(new Pair(bill, 1));
        result.add(new Pair(dill, 1));
        result.add(new Pair(jill, 3));


        List<Pair<Profile, Integer>> sorted = sorter.mutate(matches);

        for (int i = 0; i < sorted.size(); i++) {
            Pair<Profile, Integer> sortedPair = sorted.get(i);
            Pair<Profile, Integer> resultPair = result.get(i);
            assertEquals(sortedPair.first, resultPair.first);
            assertSame(sortedPair.second, resultPair.second);
        }

        this.db.clearAllTables();
    }

    @Test
    public void TestSizeSorter() {
        List<Profile> matches = new ArrayList<>();
        Mutator sorter = new SizeSorter(this.db);
        this.db.profileDao().insert(self);
        this.db.profileDao().insert(bill);
        this.db.profileDao().insert(will);
        this.db.profileDao().insert(jill);
        this.db.profileDao().insert(dill);
        matches.add(bill);
        matches.add(will);
        matches.add(jill);
        matches.add(dill);

        this.db.courseDao().insert(new Course("self", "2022", "Winter", "CSE", "110", "Gigantic"));
        this.db.courseDao().insert(new Course("self", "2021", "Fall", "CSE", "140", "Medium"));
        this.db.courseDao().insert(new Course("self", "2021", "Summer Session 1", "CSE", "101", "Tiny"));
        this.db.courseDao().insert(new Course("self", "2021", "Winter", "CSE", "12", "Huge"));
        this.db.courseDao().insert(new Course("self", "2021", "Winter", "CSE", "15L", "Small"));
        this.db.courseDao().insert(new Course("self", "2020", "Fall", "CSE", "11", "Huge"));
        this.db.courseDao().insert(new Course("self", "2021", "Fall", "CSE", "120", "Large"));

        this.db.courseDao().insert(new Course("a", "2022", "Winter", "CSE", "110", "Gigantic"));
        this.db.courseDao().insert(new Course("b", "2021", "Fall", "CSE", "140", "Medium"));
        this.db.courseDao().insert(new Course("b", "2021", "Summer Session 1", "CSE", "101", "Tiny"));
        this.db.courseDao().insert(new Course("c", "2021", "Winter", "CSE", "12", "Huge"));
        this.db.courseDao().insert(new Course("c", "2021", "Winter", "CSE", "15L", "Small"));
        this.db.courseDao().insert(new Course("c", "2020", "Fall", "CSE", "11", "Huge"));
        this.db.courseDao().insert(new Course("d", "2021", "Fall", "CSE", "120", "Large"));

        List<Double> expectedRecencyScores = Arrays.asList(0.03, 1.18, 0.45, 0.10);
        for (int i = 0; i < matches.size(); i++) {
            assertEquals(expectedRecencyScores.get(i), Double.valueOf(calculateSizeScore(matches.get(i))));
        }

        List<Pair<Profile, Integer>> result = new ArrayList<>();
        result.add(new Pair(will, 2));
        result.add(new Pair(jill, 3));
        result.add(new Pair(dill, 1));
        result.add(new Pair(bill, 1));

        List<Pair<Profile, Integer>> sorted = sorter.mutate(matches);

        for (int i = 0; i < sorted.size(); i++) {
            Pair<Profile, Integer> sortedPair = sorted.get(i);
            Pair<Profile, Integer> resultPair = result.get(i);
            assertEquals(sortedPair.first, resultPair.first);
            assertSame(sortedPair.second, resultPair.second);
        }

        this.db.clearAllTables();
    }

    @Test
    public void TestCurrentQuarterFilter() {
        List<Profile> matches = new ArrayList<>();
        Mutator sorter = new CurrentQuarterFilter(this.db, "Fall", "2021");
        this.db.profileDao().insert(self);
        this.db.profileDao().insert(bill);
        this.db.profileDao().insert(will);
        this.db.profileDao().insert(jill);
        this.db.profileDao().insert(dill);
        matches.add(bill);
        matches.add(will);
        matches.add(jill);
        matches.add(dill);

        this.db.courseDao().insert(new Course("self", "2022", "Winter", "CSE", "110", "Gigantic"));
        this.db.courseDao().insert(new Course("self", "2021", "Fall", "CSE", "140", "Medium"));
        this.db.courseDao().insert(new Course("self", "2021", "Summer Session 1", "CSE", "101", "Tiny"));
        this.db.courseDao().insert(new Course("self", "2021", "Winter", "CSE", "12", "Huge"));
        this.db.courseDao().insert(new Course("self", "2021", "Winter", "CSE", "15L", "Small"));
        this.db.courseDao().insert(new Course("self", "2020", "Fall", "CSE", "11", "Huge"));
        this.db.courseDao().insert(new Course("self", "2021", "Fall", "CSE", "120", "Large"));

        this.db.courseDao().insert(new Course("a", "2022", "Winter", "CSE", "110", "Gigantic"));
        this.db.courseDao().insert(new Course("b", "2021", "Fall", "CSE", "140", "Medium"));
        this.db.courseDao().insert(new Course("b", "2021", "Summer Session 1", "CSE", "101", "Tiny"));
        this.db.courseDao().insert(new Course("c", "2021", "Winter", "CSE", "12", "Huge"));
        this.db.courseDao().insert(new Course("c", "2021", "Winter", "CSE", "15L", "Small"));
        this.db.courseDao().insert(new Course("c", "2020", "Fall", "CSE", "11", "Huge"));
        this.db.courseDao().insert(new Course("d", "2021", "Fall", "CSE", "120", "Large"));

        List<Pair<Profile, Integer>> result = new ArrayList<>();
        result.add(new Pair(will, 2));
//        result.add(new Pair(jill, 3));
        result.add(new Pair(dill, 1));
//        result.add(new Pair(bill, 1));

        List<Pair<Profile, Integer>> sorted = sorter.mutate(matches);

        for (int i = 0; i < sorted.size(); i++) {
            Pair<Profile, Integer> sortedPair = sorted.get(i);
            Pair<Profile, Integer> resultPair = result.get(i);
            assertEquals(sortedPair.first, resultPair.first);
            assertSame(sortedPair.second, resultPair.second);
        }

        this.db.clearAllTables();
    }

    @Test
    public void TestFavoritesFilter() {
        List<Profile> matches = new ArrayList<>();
        Mutator sorter = new FavoritesFilter(this.db);

        this.db.profileDao().insert(self);
        this.db.profileDao().insert(bill);
        this.db.profileDao().insert(will);
        this.db.profileDao().insert(jill);
        this.db.profileDao().insert(dill);
        matches.add(bill);
        matches.add(will);
        matches.add(jill);
        matches.add(dill);

        this.db.courseDao().insert(new Course("self", "2022", "Winter", "CSE", "110", "Gigantic"));
        this.db.courseDao().insert(new Course("self", "2021", "Fall", "CSE", "140", "Medium"));
        this.db.courseDao().insert(new Course("self", "2021", "Summer Session 1", "CSE", "101", "Tiny"));
        this.db.courseDao().insert(new Course("self", "2021", "Winter", "CSE", "12", "Huge"));
        this.db.courseDao().insert(new Course("self", "2021", "Winter", "CSE", "15L", "Small"));
        this.db.courseDao().insert(new Course("self", "2020", "Fall", "CSE", "11", "Huge"));
        this.db.courseDao().insert(new Course("self", "2021", "Fall", "CSE", "120", "Large"));

        this.db.courseDao().insert(new Course("a", "2022", "Winter", "CSE", "110", "Gigantic"));
        this.db.courseDao().insert(new Course("b", "2021", "Fall", "CSE", "140", "Medium"));
        this.db.courseDao().insert(new Course("b", "2021", "Summer Session 1", "CSE", "101", "Tiny"));
        this.db.courseDao().insert(new Course("c", "2021", "Winter", "CSE", "12", "Huge"));
        this.db.courseDao().insert(new Course("c", "2021", "Winter", "CSE", "15L", "Small"));
        this.db.courseDao().insert(new Course("c", "2020", "Fall", "CSE", "11", "Huge"));
        this.db.courseDao().insert(new Course("d", "2021", "Fall", "CSE", "120", "Large"));

        List<Pair<Profile, Integer>> result = new ArrayList<>();
//        result.add(new Pair(will, 2));
        result.add(new Pair(jill, 3));
        result.add(new Pair(dill, 1));
//        result.add(new Pair(bill, 1));

        List<Pair<Profile, Integer>> sorted = sorter.mutate(matches);

        for (int i = 0; i < sorted.size(); i++) {
            Pair<Profile, Integer> sortedPair = sorted.get(i);
            Pair<Profile, Integer> resultPair = result.get(i);
            assertEquals(sortedPair.first, resultPair.first);
            assertSame(sortedPair.second, resultPair.second);
        }

        this.db.clearAllTables();
    }


    public int calculateRecencyScore(Profile profile) {
        List<Course> courses = this.db.courseDao().getCoursesByProfileId(profile.getProfileId());
        int score = 0;
        for (Course course : courses) {
            score += calculateCourseRecencyScore(course);
        }
        return score;
    }

    public int calculateCourseRecencyScore(Course course) {
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

    public double calculateSizeScore(Profile profile) {
        List<Course> courses = this.db.courseDao().getCoursesByProfileId(profile.getProfileId());
        double score = 0;
        for (Course course : courses) {
            score += calculateCourseSizeScore(course);
        }
        return score;
    }

    public double calculateCourseSizeScore(Course course) {
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
