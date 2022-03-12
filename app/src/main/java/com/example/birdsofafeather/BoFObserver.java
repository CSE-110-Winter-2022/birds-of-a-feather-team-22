/*
 * This file provides the blueprint for implementations of the BOFObserver interface.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */
package com.example.birdsofafeather;

import com.example.birdsofafeather.mutator.Mutator;

/**
 * Interface for a BoFObserver which observes a BoFSubject
 */
public interface BoFObserver {

    /**
     * Updates the match view.
     */
    void updateView();

    /**
     * Sets the mutator of the observer.
     *
     * @param mutator A mutator object
     */
    void setMutator(Mutator mutator);
}
