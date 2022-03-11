/*
 * This file provides the structure for how implementations should sort through profiles.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather.mutator.sorter;

import android.util.Pair;

import com.example.birdsofafeather.mutator.Mutator;
import com.example.birdsofafeather.db.Profile;

import java.util.List;

/*
 * This class declares a mutation to allow for sorting of profiles.
 */
public abstract class Sorter implements Mutator {

    /**
     * Sorts through students a certain way based on the implementation.
     *
     * @param matches List of profiles that have at least one shared course with user self
     * @return List of filtered profiles with their corresponding number of shared courses
     */
    public List<Pair<Profile, Integer>> mutate(List<Profile> matches) {
        return null;
    }
}
