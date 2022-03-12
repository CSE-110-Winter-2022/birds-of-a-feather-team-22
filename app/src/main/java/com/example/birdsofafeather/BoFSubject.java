/*
 * This file provides the blueprint for implementations of BOFSubject interface.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */
package com.example.birdsofafeather;

/**
 * Interface for a BoFSubject which composes BoFObservers
 */
public interface BoFSubject {
    /**
     * Register a BoFObserver.
     *
     * @param observer A BoFObserver
     * @return none
     */
    void register(BoFObserver observer);

    /**
     * Unregisters a BoFObserver.
     *
     * @param observer A BoFObserver
     */
    void unregister(BoFObserver observer);
}
