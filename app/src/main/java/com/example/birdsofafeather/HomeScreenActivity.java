package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
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

        this.db = AppDatabase.singleton(this);

        // Check if the user's profile needs to be setup
        this.f1 = backgroundThreadExecutor.submit(() -> {
            Profile p = this.db.profileDao().getProfile(1);
            if (p == null) {
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, NameActivity.class);
                    startActivity(intent);
                });

            }
            return null;
        });

        this.matches = new ArrayList<>();

        // Grab list of discovered users to display on start
        this.f3 = backgroundThreadExecutor.submit(() -> {
            List<DiscoveredUser> discovered = this.db.discoveredUserDao().getDiscoveredUsers();

            if (discovered != null) {
                for (DiscoveredUser u : discovered) {

                    Profile p = this.db.profileDao().getProfile(u.getProfileId());
                    this.matches.add(new Pair(p, u.getNumShared()));
                }

                this.matches.sort(new MatchesComparator());
            }

            return null;
        });

        // For demo purposes
        fillStack();
        addCoursesToDB();

        // Grab all view elements
        this.matchesRecyclerView = findViewById(R.id.matches_view);
        this.matchesViewAdapter = new MatchesViewAdapter(this.matches,this);
        this.matchesRecyclerView.setAdapter(this.matchesViewAdapter);
        this.matchesLayoutManager = new LinearLayoutManager(this);
        this.matchesRecyclerView.setLayoutManager(this.matchesLayoutManager);
        this.stopButton = findViewById(R.id.stop_button);
        this.startButton = findViewById(R.id.start_button);
        this.matchesRecyclerView.setVisibility(View.VISIBLE);
    }

    // Demo purposes, simulate the finding matches service
    public void fillStack() {
        this.f1 = backgroundThreadExecutor.submit(() -> {
            this.addedMatches = new Stack<>();
            this.addedMatches.push(this.bill_profile);
            this.addedMatches.push(this.gary_profile);
            this.addedMatches.push(this.john_profile);
            this.addedMatches.push(this.daniel_profile);
            this.db.profileDao().insert(this.bill_profile);
            this.db.profileDao().insert(this.gary_profile);
            this.db.profileDao().insert(this.john_profile);
            this.db.profileDao().insert(this.daniel_profile);
            return null;
        });
    }

    // Demo purposes, create and add courses for the test dummies to the DB
    public void addCoursesToDB() {
        this.f1 = backgroundThreadExecutor.submit(() -> {
            Course course1 = new Course(11, this.bill_profile.getProfileId(), "2020", "Fall", "CSE", "110");
            Course course2 = new Course(12, this.bill_profile.getProfileId(), "2020", "Winter", "MATH", "20C");
            Course course7 = new Course(17, this.bill_profile.getProfileId(), "2020", "Spring", "MATH", "183");
            Course course3 = new Course(13, this.gary_profile.getProfileId(), "2020", "Fall", "CSE", "110");
            Course course4 = new Course(14, this.john_profile.getProfileId(), "2020", "Fall", "CSE", "110");
            Course course6 = new Course(16, this.john_profile.getProfileId(), "2020", "Spring", "MATH", "183");
            Course course5 = new Course(15, this.daniel_profile.getProfileId(), "2020", "Fall", "CSE", "110");
            this.db.courseDao().insert(course1);
            this.db.courseDao().insert(course2);
            this.db.courseDao().insert(course3);
            this.db.courseDao().insert(course4);
            this.db.courseDao().insert(course5);
            this.db.courseDao().insert(course6);
            this.db.courseDao().insert(course7);

            return null;
        });
    }

    // When the start button is clicked
    public void onClickStart(View view) {

        this.stopButton.setVisibility(View.VISIBLE);
        this.startButton.setVisibility(View.GONE);

        // Demo purposes, calculate the number of shared courses between the user and a match and add to a list to send to the view adapter
        if (!this.addedMatches.isEmpty()) {
            this.f1 = this.backgroundThreadExecutor.submit(() -> {
                Profile match = this.addedMatches.pop();
                while (this.db.discoveredUserDao().exists(match.getProfileId()) != 0 && !this.addedMatches.isEmpty()) {
                    match = this.addedMatches.pop();
                }

                if (this.db.discoveredUserDao().exists(match.getProfileId()) == 0) {
                    // Get the user's courses
                    List<Course> myCourses = this.db.courseDao().getCoursesByProfileId(1);
                    List<Course> theirCourses = this.db.courseDao().getCoursesByProfileId(match.getProfileId());

                    int numShared = Utilities.getNumSharedCourses(myCourses, theirCourses);
                    if (numShared > 0) {
                        this.db.discoveredUserDao().insert(new DiscoveredUser(match.getProfileId(), numShared));

                        this.matches.add(new Pair(match, numShared));
                        this.matches.sort(new MatchesComparator());
                    }
                }
                return null;
            });
        }

        // Initialize view adapter and recycler view
        this.matchesViewAdapter = new MatchesViewAdapter(this.matches,this);
        this.matchesRecyclerView.setAdapter(this.matchesViewAdapter);
        this.matchesLayoutManager = new LinearLayoutManager(this);
        this.matchesRecyclerView.setLayoutManager(this.matchesLayoutManager);
    }

    // When the stop button is clicked
    public void onClickStop(View view) {
        this.startButton.setVisibility(View.VISIBLE);
        this.stopButton.setVisibility(View.GONE);
        this.matchesRecyclerView.setVisibility(View.VISIBLE);
    }

    // When a match in the recycler view is clicked
    public void onClickMatch(View view) {
        // Send the match's profile id to the activity responsible for showing the profile
        TextView profileIdView = view.findViewById(R.id.match_profile_id_view);
        int profileId = Integer.parseInt(profileIdView.getText().toString());
        Intent intent = new Intent(this, ViewProfileActivity.class);
        intent.putExtra("profileId", profileId);
        startActivity(intent);
    }

    // For testing purposes, visibility is set to gone for demoing and actual use
    public void onDeleteDBClicked(View view) {
        this.db.clearAllTables();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.f1 != null) {
            this.f1.cancel(true);
        }
        if (this.f2 != null) {
            this.f2.cancel(true);
        }
    }

}

// Comparator used to sort matches by their number of shared courses in decreasing order
class MatchesComparator implements Comparator<Pair<Profile, Integer>> {
    public int compare(Pair<Profile, Integer> p1, Pair<Profile, Integer> p2) {
        if (p2.second == p1.second) {
            return p1.first.getName().compareTo(p2.first.getName());
        }
        else {
            return p2.second - p1.second;
        }
    }
}