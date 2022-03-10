package com.example.birdsofafeather.mutator.filter;

import android.util.Pair;

import com.example.birdsofafeather.mutator.Mutator;
import com.example.birdsofafeather.db.Profile;

import java.util.List;

public abstract class Filter implements Mutator {
    public List<Pair<Profile, Integer>> mutate(List<Profile> matches) {
        return null;
    }
}
