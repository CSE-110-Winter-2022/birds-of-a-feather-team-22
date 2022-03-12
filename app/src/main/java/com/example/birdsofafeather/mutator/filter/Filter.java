/*
 * This file is capable of providing the structure for how implementations should filter through
 * profiles.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather.mutator.filter;

import android.util.Pair;

import com.example.birdsofafeather.mutator.Mutator;
import com.example.birdsofafeather.db.Profile;

import java.util.List;

/*
 * This class declares a mutation to allow for filtering of profiles.
 */
public abstract class Filter implements Mutator {

    /**
     * Filters through students a certain way based on the implementation.
     *
     * @param matches List of profiles that have at least one shared course with user self
     * @return List of filtered profiles with their corresponding number of shared courses
     */
    public List<Pair<Profile, Integer>> mutate(List<Profile> matches) {
        return null;
    }
}
