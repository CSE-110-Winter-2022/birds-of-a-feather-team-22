package com.example.birdsofafeather.Mutator;

import android.util.Pair;

import com.example.birdsofafeather.BoFSubject;
import com.example.birdsofafeather.db.Profile;

import java.util.List;

public interface Mutator {
    List<Pair<Profile, Integer>> mutate(List<Profile> matches);
}
