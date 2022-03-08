package com.example.birdsofafeather.Mutator.Sorter;

import android.util.Pair;

import com.example.birdsofafeather.Mutator.Mutator;
import com.example.birdsofafeather.db.Profile;

import java.util.List;


public abstract class Sorter implements Mutator {
    public List<Pair<Profile, Integer>> mutate(List<Profile> matches) {
        return null;
    }
}
