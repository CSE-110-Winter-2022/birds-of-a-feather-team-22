/*
 * This file is capable of providing the structure for how implementations should be able to
 * mutate a list of profiles for sorting and filtering.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather.mutator;

import android.util.Pair;

import com.example.birdsofafeather.db.Profile;

import java.util.List;

/*
 * Mutator interface where Mutator objects transform a list of Profiles and return a list of
 * Profile-Integer Pairs.
 */
public interface Mutator {

    /**
     * Mutates/transforms a list of Profile objects to give a last of Profile-Integer pairs.
     *
     * @param matches A list of Profile objects.
     * @return A list of Profile-Integer pairs.
     */
    List<Pair<Profile, Integer>> mutate(List<Profile> matches);
}
