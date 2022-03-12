/*
 * This file is capable of providing a client to allow for logging of the client for nearby
 * messaging.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.internal.ApiKey;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesClient;
import com.google.android.gms.nearby.messages.MessagesOptions;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.StatusCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.android.gms.tasks.Task;

/**
 * MessagesClient class type that composes a Nearby MessagesClient to allow for MessagesClient to be logged.
 */
public class BoFMessagesClient implements MessagesClient {
    // Log tag
    private final String TAG = "<BoFMessagesClient>";

    // Nearby Messages client
    private MessagesClient nearbyMessagesClient;

    /**
     * Default constructor for BoFMessagesClient
     *
     * @param nearbyMessagesClient Nearby MessagesClient object
     */
    public BoFMessagesClient(MessagesClient nearbyMessagesClient) {
        this.nearbyMessagesClient = nearbyMessagesClient;
    }

    /**
     * Publishes a message with log
     *
     * @param message A Message
     * @return A Task<Void>
     */
    @NonNull
    @Override
    public Task<Void> publish(@NonNull Message message) {
        Log.d(TAG, "Publishing message: " + new String(message.getContent()));
        return this.nearbyMessagesClient.publish(message);
    }

    /**
     * Publishes a message with publish options and log
     *
     * @param message A Message
     * @param publishOptions A PublishOptions
     * @return A Task<Void>
     */
    @NonNull
    @Override
    public Task<Void> publish(@NonNull Message message, @NonNull PublishOptions publishOptions) {
        Log.d(TAG, "Publishing message: " + new String(message.getContent()) + " with " + publishOptions);
        return this.nearbyMessagesClient.publish(message, publishOptions);
    }

    /**
     * Unpublishes a message with log
     *
     * @param message A Message
     * @return A Task<Void>
     */
    @NonNull
    @Override
    public Task<Void> unpublish(@NonNull Message message) {
        Log.d(TAG, "Unpublishing message: " + new String(message.getContent()));
        return this.nearbyMessagesClient.unpublish(message);
    }

    /**
     * Subscribes a message listener with log
     *
     * @param messageListener A MessageListener
     * @return A Task<Void>
     */
    @NonNull
    @Override
    public Task<Void> subscribe(@NonNull MessageListener messageListener) {
        Log.d(TAG, "Subscribing message listener!");
        return this.nearbyMessagesClient.subscribe(messageListener);
    }

    /**
     * Subscribes a message listener with subscribe options and log
     *
     * @param messageListener A MessageListener
     * @param subscribeOptions A SubscribeOptions
     * @return A Task<Void>
     */
    @NonNull
    @Override
    public Task<Void> subscribe(@NonNull MessageListener messageListener, @NonNull SubscribeOptions subscribeOptions) {
        Log.d(TAG, "Subscribing message listener with subscribe options!");
        return this.nearbyMessagesClient.subscribe(messageListener, subscribeOptions);
    }

    /**
     * Subscribes a pending intent with subscribe options and log
     *
     * @param pendingIntent A PendingIntent
     * @param subscribeOptions A SubscribeOptions
     * @return A Task<Void>
     */
    @NonNull
    @Override
    public Task<Void> subscribe(@NonNull PendingIntent pendingIntent, @NonNull SubscribeOptions subscribeOptions) {
        Log.d(TAG, "Subscribing pending intent with subscribe options!");
        return this.nearbyMessagesClient.subscribe(pendingIntent, subscribeOptions);
    }

    /**
     * Subscribes a pending intent with log
     *
     * @param pendingIntent A PendingIntent
     * @return A Task<Void>
     */
    @NonNull
    @Override
    public Task<Void> subscribe(@NonNull PendingIntent pendingIntent) {
        Log.d(TAG, "Subscribing pending intent!");
        return this.nearbyMessagesClient.subscribe(pendingIntent);
    }

    /**
     * Unsubscribes a message listener with log
     *
     * @param messageListener A MessageListener
     * @return A Task<Void>
     */
    @NonNull
    @Override
    public Task<Void> unsubscribe(@NonNull MessageListener messageListener) {
        Log.d(TAG, "Unsubscribing message listener!");
        return this.nearbyMessagesClient.unsubscribe(messageListener);
    }

    /**
     * Unsubscribes a pending intent with log
     *
     * @param pendingIntent A PendingIntent
     * @return A Task<Void>
     */
    @NonNull
    @Override
    public Task<Void> unsubscribe(@NonNull PendingIntent pendingIntent) {
        Log.d(TAG, "Unsubscribing pending intent!");
        return this.nearbyMessagesClient.unsubscribe(pendingIntent);
    }

    /**
     * Registers a status callback with log
     *
     * @param statusCallback A StatusCallback
     * @return A Task<Void>
     */
    @NonNull
    @Override
    public Task<Void> registerStatusCallback(@NonNull StatusCallback statusCallback) {
        Log.d(TAG, "Registering status callback!");
        return this.nearbyMessagesClient.registerStatusCallback(statusCallback);
    }

    /**
     * Unregisters a status callback with log
     *
     * @param statusCallback A StatusCallback
     * @return A Task<Void>
     */
    @NonNull
    @Override
    public Task<Void> unregisterStatusCallback(@NonNull StatusCallback statusCallback) {
        Log.d(TAG, "Unregistering status callback!");
        return this.nearbyMessagesClient.unregisterStatusCallback(statusCallback);
    }

    /**
     * Handles an intent with message listener and log
     *
     * @param intent A Intent
     * @param messageListener A MessageListener
     */
    @Override
    public void handleIntent(@NonNull Intent intent, @NonNull MessageListener messageListener) {
        Log.d(TAG, "Handling intent with messageListener");
        this.nearbyMessagesClient.handleIntent(intent, messageListener);
    }

    /**
     * Gets the API key with log
     * @return The API key
     */
    @NonNull
    @Override
    public ApiKey<MessagesOptions> getApiKey() {
        Log.d(TAG, "Getting API Key!");
        return this.nearbyMessagesClient.getApiKey();
    }
}
