package com.example.birdsofafeather.Sorter;

import android.util.Pair;

import com.example.birdsofafeather.db.Profile;

import java.util.Comparator;

// Comparator used to sort matches by their number of shared courses in decreasing order
public class MatchesComparator implements Comparator<Pair<Profile, Integer>> {
    public int compare(Pair<Profile, Integer> p1, Pair<Profile, Integer> p2) {
        return p2.second - p1.second;
    }
}
