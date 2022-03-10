/*
 * This file provides the blueprint for implementations of BOFSubject interface.
 *
 * Author: Group 22
 */
package com.example.birdsofafeather;

public interface BoFSubject {
    /**
     * Register a new listener.
     *
     * @param observer An observer
     * @return none
     */
    void register(BoFObserver observer);

    /**
     * Unregisters an observer.
     *
     * @param observer An observer
     * @return none
     */
    void unregister(BoFObserver observer);
}
