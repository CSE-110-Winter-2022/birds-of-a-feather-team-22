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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MatchViewMediator implements BoFObserver {
    private final String TAG = "<MVM>";

    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Context context;
    private AppDatabase db;
    private Mutator mutator;
    private RecyclerView rv;
    private RecyclerView.LayoutManager llm;

    private String sessionId;

    private Future<List<String>> f1;
    private Future<List<DiscoveredUser>> f2;
    private Future<Profile> f3;


    public MatchViewMediator(Context context, AppDatabase db, Mutator mutator, RecyclerView mrv, String sessionId) {
        this.context = context;
        this.db = db;
        this.mutator = mutator;
        this.rv = mrv;
        this.llm = new LinearLayoutManager(context);
        this.rv.setAdapter(new MatchViewAdapter(new ArrayList<>(), this.context));
        this.rv.setLayoutManager(this.llm);
        this.sessionId = sessionId;
    }

    // TODO: Implement for waves
    @Override
    public synchronized void updateMatchView() {
        // Get current position of RecyclerView
        int currentVisiblePosition = ((LinearLayoutManager) Objects.requireNonNull(this.rv.getLayoutManager())).findFirstCompletelyVisibleItemPosition();

        Log.d(TAG, "Updating matches list!");

        this.f1 = this.backgroundThreadExecutor.submit(() -> this.db.profileDao().getWavingProfileIds(true));
        List<String> wavingProfileIds = null;
        try {
            wavingProfileIds = this.f1.get();
        } catch (Exception e) {
            Log.d(TAG, "Unable to retrieve waving profile ids!");
        }

        this.f2 = this.backgroundThreadExecutor.submit(() -> this.db.discoveredUserDao().getDiscoveredUsersFromSession(this.sessionId));
        List<DiscoveredUser> usersInSession = null;
        try {
            usersInSession = this.f2.get();
        } catch (Exception e) {
            Log.d(TAG, "Unable to retrieve discovered users in session!");
        }

        List<Profile> wavingProfiles = new ArrayList<>();
        List<Profile> nonWavingProfiles = new ArrayList<>();

        for (DiscoveredUser user : usersInSession) {
            if (user.getNumShared() > 0) {
                String profileId = user.getProfileId();

                this.f3 = backgroundThreadExecutor.submit(() -> this.db.profileDao().getProfile(profileId));
                Profile profile = null;
                try {
                    profile = this.f3.get();
                } catch (Exception e) {
                    Log.d(TAG, "Unable to retrieve profile!");
                }

                if (wavingProfileIds.contains(profileId)) {
                    wavingProfiles.add(profile);
                }
                else {
                    nonWavingProfiles.add(profile);
                }
            }
        }

        // Find and sort/filter matches
        List<Pair<Profile, Integer>> matches = new QuantitySorter(context).mutate(wavingProfiles);
        List<Pair<Profile, Integer>> matchesRest = this.mutator.mutate(nonWavingProfiles);
        matches.addAll(matchesRest);

        Log.d(TAG, "Displaying updated matches!");

        // Refresh recycler view

        this.rv.setAdapter(new MatchViewAdapter(matches, this.context));
        this.rv.setLayoutManager(this.llm);

        // Scroll RecyclerView back to previous position
        ((LinearLayoutManager) Objects.requireNonNull(rv.getLayoutManager())).scrollToPosition(currentVisiblePosition);
    }

    public void setMutator(Mutator mutator) {
        this.mutator = mutator;
    }
}
