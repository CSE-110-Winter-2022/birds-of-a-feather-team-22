package com.example.birdsofafeather;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.Mutator.Mutator;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.DiscoveredUser;
import com.example.birdsofafeather.db.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NearbyViewMediator implements BoFObserver {
    private final String TAG = "<NVM>";

    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Context context;
    private AppDatabase db;
    private Mutator mutator;
    private MatchViewAdapter mva;
    private RecyclerView mrv;
    private RecyclerView.LayoutManager llm;

    private String sessionId;


    public NearbyViewMediator(Context context, Mutator mutator, MatchViewAdapter mva, RecyclerView mrv, String sessionId) {
        this.context = context;
        this.db = AppDatabase.singleton(context);
        this.mutator = mutator;
        this.mva = mva;
        this.mrv = mrv;
        this.llm = new LinearLayoutManager(context);
        this.sessionId = sessionId;
    }

    @Override
    public void onChange() {
        // Find and sort/filter matches
        List<Pair<Profile, Integer>> matches = this.mutator.mutate(getCurrentMatches());

        // Refresh recycler view
        this.mva = new MatchViewAdapter(matches, this.context);
        this.mrv.setAdapter(this.mva);
        this.mrv.setLayoutManager(this.llm);
    }

    private List<Profile> getCurrentMatches() {
        Future<List<Profile>> future = this.backgroundThreadExecutor.submit(() -> {
            Log.d(TAG, "Display list of already matched students");
            List<DiscoveredUser> discovered = db.discoveredUserDao().getDiscoveredUsersFromSession(sessionId);
            List<Profile> discoveredProfiles = new ArrayList<>();
            if (discovered != null) {
                for (DiscoveredUser user : discovered) {
                    Log.d(TAG, "Prior DiscoveredUser found!");
                    if (user.getNumShared() > 0) {
                        Profile profile = db.profileDao().getProfile(user.getProfileId());
                        discoveredProfiles.add(profile);
                    }
                }
            }

            return discoveredProfiles;
        });

        try {
            return future.get();
        } catch (Exception e) {
            Log.d(TAG, "Unable to retrieve current matches!");
        }

        return null;
    }

}
