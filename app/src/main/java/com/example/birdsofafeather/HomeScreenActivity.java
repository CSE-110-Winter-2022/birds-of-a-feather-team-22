package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.DiscoveredUser;
import com.example.birdsofafeather.db.Profile;
import com.example.birdsofafeather.db.Session;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Refers to the screen where the user can see discovered users and search for more discovered users
public class HomeScreenActivity extends AppCompatActivity {
    // DB-related fields
    private Future<Void> f1;
    private Future<List<Course>> f2;
    private Future<List<Pair<Profile, Integer>>> f3;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;
    private List<Pair<Profile, Integer>> matches;
    private Session session;

    // View/UI fields
    private RecyclerView matchesRecyclerView;
    private RecyclerView.LayoutManager matchesLayoutManager;
    private MatchesViewAdapter matchesViewAdapter;
    private Button startButton;
    private Button stopButton;

    private AlertDialog promptDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        setTitle("Birds of a Feather");

        Log.d("<Home>", "Setting up Home Screen");

        // DB-related Initializations
        this.db = AppDatabase.singleton(this);
        this.matches = new ArrayList<>();
        this.session = null;

//        String sessionId = getIntent().getStringExtra("sessionId");
//        // Grab list of discovered users to display on start
//        this.f3 = this.backgroundThreadExecutor.submit(() -> {
//            Log.d("<Home>", "Display list of already matched students");
//            List<DiscoveredUser> discovered = db.discoveredUserDao().getDiscoveredUsers(sessionId);
//
//            if (discovered != null) {
//                for (DiscoveredUser u : discovered) {
//
//                    Profile p = db.profileDao().getProfile(u.getProfileId());
//                    this.matches.add(new Pair(p, u.getNumShared()));
//                }
//
//                this.matches.sort(new MatchesComparator());
//            }
//
//            return null;
//        });


        // View initializations
        this.matchesRecyclerView = findViewById(R.id.matches_view);
        this.matchesViewAdapter = new MatchesViewAdapter(this.matches,this);
        this.matchesLayoutManager = new LinearLayoutManager(this);
        this.stopButton = findViewById(R.id.stop_button);
        this.startButton = findViewById(R.id.start_button);

        // Setup recycler view
        this.matchesRecyclerView.setAdapter(this.matchesViewAdapter);
        this.matchesRecyclerView.setLayoutManager(this.matchesLayoutManager);
        this.matchesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        this.f1 = this.backgroundThreadExecutor.submit(() -> {
            Session currentSession = this.db.sessionDao().getSession(this.session.getSessionId());
            if (currentSession == null) {
                DateFormat df = new SimpleDateFormat("M'/'d'/'yy h:mma");
                String date = df.format(Calendar.getInstance().getTime());
                this.session.setName(date);
                this.db.sessionDao().insert(this.session);
            }
            return null;
        });

        super.onDestroy();
        if (this.f1 != null) {
            this.f1.cancel(true);
        }
        if (this.f2 != null) {
            this.f2.cancel(true);
        }
        if (this.f3 != null) {
            this.f3.cancel(true);
        }
    }

    // When the start button is clicked
    public void onClickStart(View view) {

        Log.d("<Home>", "Start button pressed, searching for matches...");

        this.stopButton.setVisibility(View.VISIBLE);
        this.startButton.setVisibility(View.GONE);

        // TODO: choose/resume session?
        String sessionId = UUID.randomUUID().toString();
        this.session = new Session(sessionId, "");
        // TODO: get match info via Nearby Messages API
        // TODO: Create Profile and DiscoveredUser object for match
        // TODO: Add Profile and DiscoveredUser objects to DB
        // TODO: Update matches List and sort


        //promptDialog.getWindow().setLayout(600,400);

//        // Demo purposes, calculate the number of shared courses between the user and a match and add to a list to send to the view adapter
//        if (!addedMatches.isEmpty()) {
//            Log.d("<Home>", "Finding matches and displaying to screen");
//            f1 = backgroundThreadExecutor.submit(() -> {
//                Profile match = addedMatches.pop();
//                while (db.discoveredUserDao().getProfileId(match.getProfileId()) != null && !addedMatches.isEmpty()) {
//                    match = addedMatches.pop();
//                }
//
//                if (db.discoveredUserDao().getProfileId(match.getProfileId()) == null) {
//                    // Get the user's courses
//                    Profile user = db.profileDao().getUserProfile(true);
//                    this.myCourses = db.courseDao().getCoursesByProfileId(user.getProfileId());
//                    List<Course> theirCourses = db.courseDao().getCoursesByProfileId(match.getProfileId());
//
//                    int numShared = Utilities.getNumSharedCourses(this.myCourses, theirCourses);
//
//                    if (numShared > 0) {
//                        db.discoveredUserDao().insert(new DiscoveredUser(match.getProfileId(), numShared));
//
//                        this.matches.add(new Pair(match, numShared));
//                        this.matches.sort(new MatchesComparator());
//                    }
//                }
//                return null;
//            });
//        }

        // Refresh recycler view
        this.matchesViewAdapter = new MatchesViewAdapter(this.matches,this);
        this.matchesLayoutManager = new LinearLayoutManager(this);
        this.matchesRecyclerView.setAdapter(this.matchesViewAdapter);
        this.matchesRecyclerView.setLayoutManager(this.matchesLayoutManager);
    }
    public void onClickCourseLabel(View view){
        //find selected item from recyclerview, grab views for course info
        TextView sessionCourseNameTextView = view.findViewById(R.id.session_course_name_text_view);
        TextView sessionCourseNumberTextView = view.findViewById(R.id.session_course_number_text_view);
        TextView setSessionTextView = this.promptDialog.findViewById(R.id.set_course);

        //
        this.promptDialog.findViewById(R.id.enter_session_button).setVisibility(View.GONE);
        this.promptDialog.findViewById(R.id.submit_session_button).setVisibility(View.VISIBLE);
        setSessionTextView.setText(""+sessionCourseNameTextView.getText() +
                                    sessionCourseNumberTextView.getText());


    }

    // When the stop button is clicked
    public void onClickStop(View view) {
        Log.d("<Home>", "Stop button pressed, stopping search for matches...");

        this.startButton.setVisibility(View.VISIBLE);
        this.stopButton.setVisibility(View.GONE);

        // TODO: Prompt user to save current session under a current course or other course name
        // String sessionName = getUserSavedName();
        // TODO: Update session name and add session to DB
        // this.session.setName(sessionName);


        Profile user = this.db.profileDao().getUserProfile(true);
        List<Course> sessionCoursesList = this.db.courseDao().getCoursesByProfileId(user.getProfileId());

        LayoutInflater inflater = getLayoutInflater();
        View contextView = inflater.inflate(R.layout.activity_home_screen_stop_alert, null);

        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(this);


        RecyclerView sessionsView = contextView.findViewById(R.id.classes_list);

        sessionsView.setLayoutManager(new LinearLayoutManager(this));
        sessionsView.setHasFixedSize(true);

        SessionsAdapter adapter = new SessionsAdapter(sessionCoursesList);
        sessionsView.setAdapter(adapter);
        promptBuilder.setView(contextView);

        this.promptDialog = promptBuilder.create();
        this.promptDialog.show();

    }

    public void onClickSubmitSession(View view){
        TextView selectedCourseName = this.promptDialog.findViewById(R.id.set_course);
        this.session.setName(selectedCourseName.getText().toString());
        this.promptDialog.cancel();
    }

    public void onClickEnterSession(View view) {

    }

    // When a match in the recycler view is clicked
    public void onClickMatch(View view) {
        Log.d("<Home>", "Match selected, displaying match profile and course information");

        // Send the match's profile id to the activity responsible for showing the profile
        TextView profileIdView = view.findViewById(R.id.match_profile_id_view);
        String profileId = profileIdView.getText().toString();
        Intent intent = new Intent(this, ViewProfileActivity.class);
        intent.putExtra("profileId", profileId);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CourseActivity.class);
        startActivity(intent);
    }

    // For testing purposes, visibility is set to gone for demoing and actual use
    public void onDeleteDBClicked(View view) {
        this.db.clearAllTables();
    }


}



// Comparator used to sort matches by their number of shared courses in decreasing order
class MatchesComparator implements Comparator<Pair<Profile, Integer>> {
    public int compare(Pair<Profile, Integer> p1, Pair<Profile, Integer> p2) {
        return p2.second - p1.second;
    }
}