package com.example.birdsofafeather.Filter;

import com.example.birdsofafeather.db.Profile;

import java.util.List;

public interface Filter {
    List<Profile> filter(List<Profile> matches);
}
