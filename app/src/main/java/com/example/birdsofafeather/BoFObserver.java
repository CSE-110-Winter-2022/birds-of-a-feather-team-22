/*
 * This file provides the blueprint for implementations of the BOFObserver interface.
 *
 * Author: Group 22
 */
package com.example.birdsofafeather;

import com.example.birdsofafeather.mutator.Mutator;

public interface BoFObserver {

    /**
     * Updates the list of the current matches to the user.
     *
     * @param
     * @return none
     */
    void updateMatchesList();

    /**
     * Sets the mutator of the observer.
     *
     * @param mutator A mutator object
     * @return none
     */
    void setMutator(Mutator mutator);
}
