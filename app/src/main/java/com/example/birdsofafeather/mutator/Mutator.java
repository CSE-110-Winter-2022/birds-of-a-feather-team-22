package com.example.birdsofafeather.mutator;

import android.util.Pair;

import com.example.birdsofafeather.db.Profile;

import java.util.List;

/**
 * Mutator interface where Mutator objects transform a list of Profiles and return a list of
 * Profile-Integer Pairs.
 */
public interface Mutator {

    /**
     * Mutates/transforms a list of Profile objects to give a list of Profile-Integer Pairs.
     *
     * @param matches A list of Profile objects.
     * @return A list of Profile-Integer Pairs.
     */
    List<Pair<Profile, Integer>> mutate(List<Profile> matches);
}
