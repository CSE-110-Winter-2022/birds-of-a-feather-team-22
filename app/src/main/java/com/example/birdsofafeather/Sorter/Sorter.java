package com.example.birdsofafeather.Sorter;

import android.util.Pair;

import com.example.birdsofafeather.db.Profile;

import java.util.List;

public interface Sorter {
    List<Pair<Profile, Integer>> sort(List<Profile> matches);
}
