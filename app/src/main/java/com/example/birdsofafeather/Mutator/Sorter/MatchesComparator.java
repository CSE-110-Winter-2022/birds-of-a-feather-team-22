/*
 * This file is capable of implementing the comparison of Integers in pair objects.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather.mutator.sorter;

import android.util.Pair;

import com.example.birdsofafeather.db.Profile;

import java.util.Comparator;

/*
 * Class implements comparison to allow sorting of matches by their number of shared courses in
 * decreasing order.
 */
public class MatchesComparator implements Comparator<Pair<Profile, Integer>> {

    /**
     * Compares the integer objects found in the given pair objects.
     *
     * @param p1 The first given pair object
     * @param p2 The second given pair object
     * @return The comparison result of comparing the two integer objects
     */
    public int compare(Pair<Profile, Integer> p1, Pair<Profile, Integer> p2) {
        return p2.second - p1.second;
    }
}


