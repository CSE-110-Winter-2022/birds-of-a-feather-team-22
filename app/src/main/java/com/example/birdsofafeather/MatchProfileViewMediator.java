package com.example.birdsofafeather;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Profile;
import com.example.birdsofafeather.mutator.Mutator;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class mediates the wave GIF when a wave is received on the MatchProfileActivity
 */
public class MatchProfileViewMediator implements BoFObserver {
    // Log tag
    private final String TAG = "<MPVM>";

    // DB/Thread fields
    private Context context;
    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Profile> f1;

    // Profile id
    private String profileId;

    // Wave view
    private ImageView wave;

    /**
     * Default constructor for MatchProfileViewMediator
     *
     * @param wave ImageView wave
     */
    public MatchProfileViewMediator(Context context, AppDatabase db, String profileId, ImageView wave) {
        this.context = context;
        this.db = db;
        this.profileId = profileId;
        this.wave = wave;
    }

    /**
     * Updates the wave view as waves are received via Nearby Messages
     */
    @Override
    public synchronized void updateView() {
        this.f1 = backgroundThreadExecutor.submit(() -> this.db.profileDao().getProfile(this.profileId));
        Profile match;
        try {
            match = this.f1.get();
            if (match.getIsWaving()) {
                this.wave.setVisibility(View.VISIBLE);
                Glide.with(this.context).load(R.drawable.waving_hand).into(this.wave);
            }
            else {
                this.wave.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving match profile id!");
            e.printStackTrace();
        }
    }

    /**
     * Does nothing but needs to be implemented
     *
     * @param mutator A mutator object
     */
    @Override
    public void setMutator(Mutator mutator) {
    }
}
