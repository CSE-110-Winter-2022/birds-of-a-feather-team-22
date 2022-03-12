/*
 * This file is capable of providing a filter through profile matches based on being a favorited
 * profile.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather.mutator.filter;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.example.birdsofafeather.mutator.sorter.QuantitySorter;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
 * This class filters through profiles by having at least one shared course and is a favorite of the
 * self user.
 */
public class FavoritesFilter extends Filter {

    // Instance variables for class
    private Future<List<Profile>> f1;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;
    private Context context;

    /**
     * Constructor for class.
     *
     * @param context A given context
     */
    public FavoritesFilter(Context context) {
        this.context = context;
        this.db = AppDatabase.singleton(context);
    }

    /**
     * Constructor for class.
     *
     * @param db A given database
     */
    public FavoritesFilter(AppDatabase db) {
        this.db = db;
    }

    /**
     * Provides a list of profiles and number their number of shared courses with user with the applied
     * favorite filter enabled.
     *
     * @param matches List of profiles that share at least one course with user self.
     * @return List of profiles with corresponding number of shared courses that have been favorited
     */
    @Override
    public synchronized List<Pair<Profile, Integer>> mutate(List<Profile> matches) {
        this.f1 = this.backgroundThreadExecutor.submit(() -> this.db.profileDao().getFavoriteProfiles(true));

        try {
            List<Profile> favorites = this.f1.get();
            List<Profile> nonWavingFavorites = new ArrayList<>();
            for (Profile favorite : favorites) {
                if (!favorite.getIsWaving()) {
                    nonWavingFavorites.add(favorite);
                }
            }
            return new QuantitySorter(context).mutate(nonWavingFavorites);
        } catch (Exception e) {
            Log.d("<FavoritesFilter>", "Unable to retrieve filtered matches!");
        }
        return new ArrayList<>();
    }
}
