package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.DiscoveredUser;
import com.example.birdsofafeather.db.Profile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Refers to the screen where the user can see discovered users and search for more discovered users
public class HomeScreenActivity extends AppCompatActivity {
    private Future<Void> f1;
    private Future<List<Course>> f2;
    private Future<List<Pair<Profile, Integer>>> f3;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    
    private AppDatabase db;
    private List<Pair<Profile, Integer>> matches;
    private Stack<Profile> addedMatches;
    
    private RecyclerView matchesRecyclerView;
    private RecyclerView.LayoutManager matchesLayoutManager;
    private MatchesViewAdapter matchesViewAdapter;
    private List<Course> myCourses;
    private Button startButton;
    private Button stopButton;

    // Demo purposes
    private Profile bill_profile = new Profile("Bill","https://cse.ucsd.edu/sites/cse.ucsd.edu/files/faculty/griswold17-115x150.jpg");
    private Profile gary_profile = new Profile("Gary","https://cse.ucsd.edu/sites/cse.ucsd.edu/files/faculty/gillespie17M-115x150.jpg");
    private Profile john_profile = new Profile("John","https://cse.ucsd.edu/sites/cse.ucsd.edu/files/faculty/eldon17-115x150.jpg");
    private Profile daniel_profile = new Profile("Daniel","https://cse.ucsd.edu/sites/cse.ucsd.edu/files/faculty/kane17-115x150.jpg");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        setTitle("Birds of a Feather");

        db = AppDatabase.singleton(this);

        this.matches = new ArrayList<>();

        // Grab list of discovered users to display on start
        this.f3 = this.backgroundThreadExecutor.submit(() -> {
            Log.d("<Home>", "Display list of already matched students");
            List<DiscoveredUser> discovered = db.discoveredUserDao().getDiscoveredUsers();

            if (discovered != null) {
                for (DiscoveredUser u : discovered) {

                    Profile p = db.profileDao().getProfile(u.getProfileId());
                    this.matches.add(new Pair(p, u.getNumShared()));
                }

                this.matches.sort(new MatchesComparator());
            }

            return null;
        });

        this.myCourses = null;

        // Grab all view elements
        matchesRecyclerView = findViewById(R.id.matches_view);
        matchesViewAdapter = new MatchesViewAdapter(matches,this);
        matchesRecyclerView.setAdapter(matchesViewAdapter);
        matchesLayoutManager = new LinearLayoutManager(this);
        matchesRecyclerView.setLayoutManager(matchesLayoutManager);
        stopButton = findViewById(R.id.stop_button);
        startButton = findViewById(R.id.start_button);
        matchesRecyclerView.setVisibility(View.VISIBLE);
    }

    // When the start button is clicked
    public void onClickStart(View view) {

        stopButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);

        // Demo purposes, calculate the number of shared courses between the user and a match and add to a list to send to the view adapter
        if (!addedMatches.isEmpty()) {
            Log.d("<Home>", "Finding matches and displaying to screen");
            f1 = backgroundThreadExecutor.submit(() -> {
                Profile match = addedMatches.pop();
                while (db.discoveredUserDao().getProfileId(match.getProfileId()) != null && !addedMatches.isEmpty()) {
                    match = addedMatches.pop();
                }

                if (db.discoveredUserDao().getProfileId(match.getProfileId()) == null) {
                    // Get the user's courses
                    Profile user = db.profileDao().getUserProfile(true);
                    this.myCourses = db.courseDao().getCoursesByProfileId(user.getProfileId());
                    List<Course> theirCourses = db.courseDao().getCoursesByProfileId(match.getProfileId());

                    int numShared = Utilities.getNumSharedCourses(this.myCourses, theirCourses);

                    if (numShared > 0) {
                        db.discoveredUserDao().insert(new DiscoveredUser(match.getProfileId(), numShared));

                        this.matches.add(new Pair(match, numShared));
                        this.matches.sort(new MatchesComparator());
                    }
                }
                return null;
            });
        }

        // Initialize view adapter and recycler view
        matchesViewAdapter = new MatchesViewAdapter(matches,this);
        matchesRecyclerView.setAdapter(matchesViewAdapter);
        matchesLayoutManager = new LinearLayoutManager(this);
        matchesRecyclerView.setLayoutManager(matchesLayoutManager);
    }

    // When the stop button is clicked
    public void onClickStop(View view) {
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.GONE);
        matchesRecyclerView.setVisibility(View.VISIBLE);
        Log.d("<Home>", "Stop searching");
    }

    // When a match in the recycler view is clicked
    public void onClickMatch(View view) {
        Log.d("<Home>", "Clicked on profile to display");
        // Send the match's profile id to the activity responsible for showing the profile
        TextView profileIdView = view.findViewById(R.id.match_profile_id_view);
        String profileId = profileIdView.getText().toString();
        Intent intent = new Intent(this, ViewProfileActivity.class);
        intent.putExtra("profileId", profileId);
        startActivity(intent);
    }

    // For testing purposes, visibility is set to gone for demoing and actual use
    public void onDeleteDBClicked(View view) {
        db.clearAllTables();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (f1 != null) {
            f1.cancel(true);
        }
        if (f2 != null) {
            f2.cancel(true);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CourseActivity.class);
        startActivity(intent);
    }

}



// Comparator used to sort matches by their number of shared courses in decreasing order
class MatchesComparator implements Comparator<Pair<Profile, Integer>> {
    public int compare(Pair<Profile, Integer> p1, Pair<Profile, Integer> p2) {
        return p2.second - p1.second;
    }
}