package com.example.birdsofafeather;

import com.example.birdsofafeather.mutator.Mutator;

public interface BoFObserver {

    void updateMatchesList();

    void setMutator(Mutator mutator);
}
