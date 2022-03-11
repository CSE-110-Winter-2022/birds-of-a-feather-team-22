/*
 * This file provides the blueprint for implementations of the BOFObserver interface.
 *
 * Author: Group 22
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
    void updateMatchView();

    /**
     * Sets the mutator of the observer.
     *
     * @param mutator A mutator object
     */
    void setMutator(Mutator mutator);
}
