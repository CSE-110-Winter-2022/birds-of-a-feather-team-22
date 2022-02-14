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
    private Profile bill_profile = new Profile(6, "Bill","https://cse.ucsd.edu/sites/cse.ucsd.edu/files/faculty/griswold17-115x150.jpg");
    private Profile gary_profile = new Profile(7, "Gary","https://cse.ucsd.edu/sites/cse.ucsd.edu/files/faculty/gillespie17M-115x150.jpg");
    private Profile john_profile = new Profile(8, "John","https://cse.ucsd.edu/sites/cse.ucsd.edu/files/faculty/eldon17-115x150.jpg");
    private Profile daniel_profile = new Profile(9, "Daniel","https://cse.ucsd.edu/sites/cse.ucsd.edu/files/faculty/kane17-115x150.jpg");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        setTitle("Birds of a Feather");

        db = AppDatabase.singleton(this);

        // Check if the user's profile needs to be setup
        f1 = backgroundThreadExecutor.submit(() -> {
            Profile p = db.profileDao().getProfile(1);
            if (p == null) {
                Log.d("<Home>", "User profile not created");
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, NameActivity.class);
                    startActivity(intent);
                });
            }
            Log.d("<Home>", "User profile already created");
            return null;
        });

        this.matches = new ArrayList<>();

        // Grab list of discovered users to display on start
        f3 = backgroundThreadExecutor.submit(() -> {
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

        // For demo purposes
        fillStack();
        addCoursesToDB();
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

    // Demo purposes, simulate the finding matches service
    public void fillStack() {
        Log.d("<Home_Stack>", "Filling stack with users for display");
        f1 = backgroundThreadExecutor.submit(() -> {
            addedMatches = new Stack<>();
            addedMatches.push(this.bill_profile);
            addedMatches.push(this.gary_profile);
            addedMatches.push(this.john_profile);
            addedMatches.push(this.daniel_profile);

            db.profileDao().insert(this.bill_profile);
            db.profileDao().insert(this.gary_profile);
            db.profileDao().insert(this.john_profile);
            db.profileDao().insert(this.daniel_profile);
            return null;
        });
    }

    // Demo purposes, create and add courses for the test dummies to the DB
    public void addCoursesToDB() {
        Log.d("<Home>", "Adding demo courses to database");
        f1 = backgroundThreadExecutor.submit(() -> {
            Course course1 = new Course(11, this.bill_profile.getProfileId(), "2020", "Fall", "CSE", "110");
            Course course2 = new Course(12, this.bill_profile.getProfileId(), "2020", "Winter", "MATH", "20C");
            Course course7 = new Course(17, this.bill_profile.getProfileId(), "2020", "Spring", "MATH", "183");
            Course course3 = new Course(13, this.gary_profile.getProfileId(), "2020", "Fall", "CSE", "110");
            Course course4 = new Course(14, this.john_profile.getProfileId(), "2020", "Fall", "CSE", "110");
            Course course6 = new Course(16, this.john_profile.getProfileId(), "2020", "Spring", "MATH", "183");
            Course course5 = new Course(15, this.daniel_profile.getProfileId(), "2020", "Fall", "CSE", "110");
            db.courseDao().insert(course1);
            db.courseDao().insert(course2);
            db.courseDao().insert(course3);
            db.courseDao().insert(course4);
            db.courseDao().insert(course5);
            db.courseDao().insert(course6);
            db.courseDao().insert(course7);

            return null;
        });
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
                while (db.discoveredUserDao().exists(match.getProfileId()) != 0 && !addedMatches.isEmpty()) {
                    match = addedMatches.pop();
                }

                if (db.discoveredUserDao().exists(match.getProfileId()) == 0) {
                    // Get the user's courses
                    this.myCourses = db.courseDao().getCoursesByProfileId(1);
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
        int profileId = Integer.parseInt(profileIdView.getText().toString());
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

}

// Comparator used to sort matches by their number of shared courses in decreasing order
class MatchesComparator implements Comparator<Pair<Profile, Integer>> {
    public int compare(Pair<Profile, Integer> p1, Pair<Profile, Integer> p2) {
        return p2.second - p1.second;
    }
}