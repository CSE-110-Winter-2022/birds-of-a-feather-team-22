/*
 * This file is capable of allowing a mediator to be able to make changes to displays which the user
 * will interact with and update as the database makes changes.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.mutator.Mutator;
import com.example.birdsofafeather.mutator.sorter.QuantitySorter;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.DiscoveredUser;
import com.example.birdsofafeather.db.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class mediates the MatchViewAdapter as well as the Nearby Messages in a session. As the DB
 * updates due to incoming Nearby Messages, the MatchesViewMediator will update/mediate the
 * MatchViewAdapter and RecyclerView to reflect the changes while actively viewing the session.
 */
public class MatchesViewMediator implements BoFObserver {
    // Log tag
    private final String TAG = "<MVM>";

    // DB/Thread fields
    private Context context;
    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<List<String>> f1;
    private Future<List<DiscoveredUser>> f2;
    private Future<Profile> f3;

    // Session fields
    private Mutator mutator;
    private String sessionId;

    // View fields
    private RecyclerView rv;
    private RecyclerView.LayoutManager llm;

    /**
     * Default constructor for MatchesViewMediator
     *
     * @param context The context of the session
     * @param db The database used in the session
     * @param mutator The mutator used in the session
     * @param mrv The RecyclerView in the session
     * @param sessionId The session id of the session
     */
    public MatchesViewMediator(Context context, AppDatabase db, Mutator mutator, RecyclerView mrv, String sessionId) {
        this.context = context;
        this.db = db;
        this.mutator = mutator;
        this.rv = mrv;
        this.llm = new LinearLayoutManager(context);
        this.rv.setAdapter(new MatchesViewAdapter(new ArrayList<>(), this.context));
        this.rv.setLayoutManager(this.llm);
        this.sessionId = sessionId;
    }

    /**
     * Updates the match view to display new matches as they are being discovered via Nearby Messages
     */
    @Override
    public synchronized void updateView() {
        // Get current position of RecyclerView
        int currentVisiblePosition = ((LinearLayoutManager) Objects.requireNonNull(this.rv.getLayoutManager())).findFirstCompletelyVisibleItemPosition();

        Log.d(TAG, "Updating matches list...");

        // Get the profile ids of the waving profiles
        this.f1 = this.backgroundThreadExecutor.submit(() -> this.db.profileDao().getWavingProfileIds(true));
        List<String> wavingProfileIds = null;
        try {
            wavingProfileIds = this.f1.get();
            Log.d(TAG, "Retrieved waving profile ids!");
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving waving profile ids!");
        }

        // Get all discovered users in the session
        this.f2 = this.backgroundThreadExecutor.submit(() -> this.db.discoveredUserDao().getDiscoveredUsersFromSession(this.sessionId));
        List<DiscoveredUser> usersInSession = null;
        try {
            usersInSession = this.f2.get();
            Log.d(TAG, "Retrieved discovered users in session!");
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving discovered users in session!");
        }

        // Separate waving profiles and regular profiles for sorting/filtering
        List<Profile> wavingProfiles = new ArrayList<>();
        List<Profile> nonWavingProfiles = new ArrayList<>();

        Log.d(TAG, "Categorizing matches...");

        // For each discovered user, categorize them into waving or non-waving
        for (DiscoveredUser user : usersInSession) {
            // Only consider those with more than 0 courses shared (only select matches)
            if (user.getNumShared() > 0) {
                String profileId = user.getProfileId();

                this.f3 = backgroundThreadExecutor.submit(() -> this.db.profileDao().getProfile(profileId));
                Profile profile = null;
                try {
                    profile = this.f3.get();
                    Log.d(TAG, "Retrieved profile of discovered user!");
                } catch (Exception e) {
                    Log.e(TAG, "Error retrieving profile of discovered user!");
                }

                if (wavingProfileIds.contains(profileId)) {
                    wavingProfiles.add(profile);
                    Log.d(TAG, "Discovered user is waving!");
                }
                else {
                    nonWavingProfiles.add(profile);
                    Log.d(TAG, "Discovered user is not waving!");
                }
            }
        }

        Log.d(TAG, "Sorting/Filtering...");

        // Find and sort/filter matches
        List<Pair<Profile, Integer>> matches = new QuantitySorter(context).mutate(wavingProfiles);
        List<Pair<Profile, Integer>> matchesRest = this.mutator.mutate(nonWavingProfiles);
        matches.addAll(matchesRest);

        Log.d(TAG, "Displaying new/updated matches!");

        // Refresh recycler view
        this.rv.setAdapter(new MatchesViewAdapter(matches, this.context));
        this.rv.setLayoutManager(this.llm);

        // Scroll RecyclerView back to previous position
        ((LinearLayoutManager) Objects.requireNonNull(rv.getLayoutManager())).scrollToPosition(currentVisiblePosition);
    }

    /**
     * Sets the Mutator object of the MatchesViewMediator to accommodate a change in sorting/filtering.
     *
     * @param mutator A Mutator object.
     */
    public void setMutator(Mutator mutator) {
        this.mutator = mutator;
    }
}
