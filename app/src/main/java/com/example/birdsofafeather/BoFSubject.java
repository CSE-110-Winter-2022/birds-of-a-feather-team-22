/*
 * This file provides the blueprint for implementations of BOFSubject interface.
 *
 * Author: Group 22
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
