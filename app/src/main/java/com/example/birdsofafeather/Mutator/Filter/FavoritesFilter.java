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

public class FavoritesFilter extends Filter {
    private Future<List<Profile>> f1;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;
    private Context context;


    public FavoritesFilter(Context context) {
        this.context = context;
        this.db = AppDatabase.singleton(context);
    }

    public FavoritesFilter(AppDatabase db) {
        this.db = db;
    }

    @Override
    public synchronized List<Pair<Profile, Integer>> mutate(List<Profile> matches) {
        this.f1 = this.backgroundThreadExecutor.submit(() -> this.db.profileDao().getFavoriteProfiles(true));

        try {
            List<Profile> favorites = this.f1.get();
            return new QuantitySorter(context).mutate(favorites);
        } catch (Exception e) {
            Log.d("<FavoritesFilter>", "Unable to retrieve filtered matches!");
        }
        return new ArrayList<>();
    }
}
