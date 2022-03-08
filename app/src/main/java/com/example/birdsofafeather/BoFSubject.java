package com.example.birdsofafeather;

public interface BoFSubject {
    /**
     * Register a new listener.
     */
    void register(BoFObserver observer);

    /**
     * Unregister a listener.
     */
    void unregister(BoFObserver observer);
}
