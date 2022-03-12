/*
 * This file is capable of implementing the comparison of doubles in pair objects.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather.mutator.sorter;

import android.util.Pair;

import com.example.birdsofafeather.db.Profile;

import java.util.Comparator;

/*
 * This class compares the second element in a pair using doubles.
 */
public class DoubleMatchesComparator implements Comparator<Pair<Profile, Double>> {

    /**
     * Compares the second element in a pair object.
     *
     * @param p1 The first given pair
     * @param p2 The second given pair
     * @return Whether the double in the first pair is greater than, less than, or equal to the double
     * in the second pair
     */
    public int compare(Pair<Profile, Double> p1, Pair<Profile, Double> p2) {
        return p2.second.compareTo(p1.second);
    }
}