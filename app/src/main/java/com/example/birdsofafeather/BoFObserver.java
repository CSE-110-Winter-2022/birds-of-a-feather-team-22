package com.example.birdsofafeather;

import com.example.birdsofafeather.Mutator.Mutator;

public interface BoFObserver {

    void updateMatchesList();

    void setMutator(Mutator mutator);
}
