package com.example.birdsofafeather.mutator.sorter;

import android.util.Pair;

import com.example.birdsofafeather.mutator.Mutator;
import com.example.birdsofafeather.db.Profile;

import java.util.List;


public abstract class Sorter implements Mutator {
    public List<Pair<Profile, Integer>> mutate(List<Profile> matches) {
        return null;
    }
}
