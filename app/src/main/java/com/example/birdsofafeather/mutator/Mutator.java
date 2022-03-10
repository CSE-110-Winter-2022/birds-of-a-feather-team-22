package com.example.birdsofafeather.mutator;

import android.util.Pair;

import com.example.birdsofafeather.db.Profile;

import java.util.List;

public interface Mutator {
    List<Pair<Profile, Integer>> mutate(List<Profile> matches);
}
