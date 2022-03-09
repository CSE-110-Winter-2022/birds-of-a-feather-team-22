package com.example.birdsofafeather.Mutator.Sorter;

import android.util.Pair;

import com.example.birdsofafeather.db.Profile;

import java.util.Comparator;

public class DoubleMatchesComparator implements Comparator<Pair<Profile, Double>> {
    public int compare(Pair<Profile, Double> p1, Pair<Profile, Double> p2) {
        return p2.second.compareTo(p1.second);
    }
}