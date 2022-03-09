package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.Mutator.Mutator;
import com.example.birdsofafeather.Mutator.Sorter.QuantitySorter;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.DiscoveredUser;
import com.example.birdsofafeather.db.Profile;
import com.example.birdsofafeather.db.Session;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Refers to the screen where the user can see discovered users and search for more discovered users
public class MatchActivity extends AppCompatActivity {
    private final String TAG = "<Match>";

    // DB-related fields
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;
    private List<Pair<Profile, Integer>> matches;
    private Session session;
    private String sessionId;
    private Profile selfProfile;
    private List<Course> selfCourses;
    private ArrayList<String> mockedMessages;
    private List<Session> allSessions;
    private BoFObserver nvm;

    // Flags
    private boolean isNewSession = false;
    private boolean isSearching = false;

    // Sorting
    private Mutator mutator;

    // View/UI fields
    private RecyclerView matchesRecyclerView;
    private RecyclerView.LayoutManager matchesLayoutManager;
    private MatchViewAdapter matchesViewAdapter;
    private EditText sessionNameView;
    private Button startButton;
    private Button stopButton;

    // currently open prompt
    private AlertDialog promptDialog;

    // Utilities
    private BoFMessageListener messageListener;
    private Message selfMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        setTitle("Birds of a Feather");

        Log.d(TAG, "Setting up Match Screen");

        // DB-related Initializations
        this.db = AppDatabase.singleton(this);
        this.matches = new ArrayList<>();

        this.backgroundThreadExecutor.submit(() -> {
            this.selfProfile = this.db.profileDao().getUserProfile(true);
            this.selfCourses = this.db.courseDao().getCoursesByProfileId(this.selfProfile.getProfileId());
            this.allSessions = this.db.sessionDao().getAllSessions();
        });

        // Get current session
        this.sessionId = getIntent().getStringExtra("session_id");
        if (this.sessionId == null) {
            Log.d(TAG, "No sessionId intent extra passed in, opening last saved session!");
            this.backgroundThreadExecutor.submit(() -> {
                this.session = this.db.sessionDao().getLastSession(true);
                this.sessionId = this.session.getSessionId();
                setLastSession();
            });
        }
        else if (this.sessionId.equals("")) {
            Log.d(TAG, "Making new session!");
            this.sessionId = UUID.randomUUID().toString();
            this.session = new Session(this.sessionId, getCurrentTimestamp(), true);
            this.backgroundThreadExecutor.submit(() -> {
                this.db.sessionDao().insert(this.session);
                setLastSession();
            });
            this.isNewSession = true;
        }
        else {
            Log.d(TAG, "Resuming previous session!");
            this.backgroundThreadExecutor.submit(() -> {
                this.session = this.db.sessionDao().getSession(this.sessionId);
                this.sessionId = this.session.getSessionId();
                setLastSession();
            });
        }

        // Get mocked messages
        this.mockedMessages = getIntent().getStringArrayListExtra("mocked_messages");
        if (this.mockedMessages == null) {
            this.mockedMessages = new ArrayList<>();
        }

        // Grab list of discovered users to display on start and sort by number of shared courses
        this.mutator = new QuantitySorter(this);
        this.matches = this.mutator.mutate(getCurrentMatches());

        // Utilities initializations
        this.messageListener = new BoFMessageListener(sessionId, this);
        this.selfMessage = new Message(encodeMessage().getBytes());

        // View initializations
        this.matchesRecyclerView = findViewById(R.id.matches_view);
        this.matchesViewAdapter = new MatchViewAdapter(this.matches,this);
        this.matchesLayoutManager = new LinearLayoutManager(this);
        this.stopButton = findViewById(R.id.stop_button);
        this.startButton = findViewById(R.id.start_button);
        this.sessionNameView = findViewById(R.id.session_name_view);

        // Change session name via EditText View
        this.sessionNameView.setText(this.session.getName());
        this.sessionNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId==EditorInfo.IME_ACTION_DONE){
                    sessionNameView.clearFocus();
                    changeSessionName(sessionNameView.getText().toString());
                }
                return false;
            }
        });

        // Setup recycler view
        this.matchesRecyclerView.setAdapter(this.matchesViewAdapter);
        this.matchesRecyclerView.setLayoutManager(this.matchesLayoutManager);
        this.matchesRecyclerView.setVisibility(View.VISIBLE);

        this.nvm = new NearbyViewMediator(this, this.mutator, this.matchesViewAdapter, this.matchesRecyclerView, this.sessionId);
        this.messageListener.register(this.nvm);
    }

    @Override
    protected void onDestroy() {
        setLastSession();
        super.onDestroy();
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

    public void startSearchForMatches() {
        Log.d(TAG, "Starting search for matches...");
        this.isSearching = true;

        this.stopButton.setVisibility(View.VISIBLE);
        this.startButton.setVisibility(View.GONE);

        Nearby.getMessagesClient(this).publish(this.selfMessage);
        Nearby.getMessagesClient(this).subscribe(this.messageListener);

        // Discover mocked messages
        for (String msg : this.mockedMessages) {
            this.messageListener.onFound(new Message(msg.getBytes()));
        }

        this.mockedMessages.clear();

    }

    public void stopSearchForMatches() {
        Log.d(TAG, "Stopping search for matches...");
        this.isSearching = false;

        this.startButton.setVisibility(View.VISIBLE);
        this.stopButton.setVisibility(View.GONE);

        Nearby.getMessagesClient(this).unpublish(this.selfMessage);
        Nearby.getMessagesClient(this).unsubscribe(this.messageListener);
    }

    public String encodeQuarter(String quarter) {
        switch(quarter) {
            case "Fall":
                return "FA";
            case "Winter":
                return "WI";
            case "Spring":
                return "SP";
            case "Summer Session 1":
                return "S1";
            case "Summer Session 2":
                return "S2";
            case "Special Summer Session":
                return "SS";
            default:
                Log.d("<MatchActivity>", "Quarter cannot be encoded");
                return null;
        }
    }

    public String encodeMessage() {
        // Look at BDD Scenario for CSV format
        // Were are encoding our own profile
        StringBuilder encodedMessage = new StringBuilder();
        String selfUUID = this.selfProfile.getProfileId();
        String selfName = this.selfProfile.getName();
        String selfPhoto = this.selfProfile.getPhoto();

        encodedMessage.append(selfUUID + ",,,,\n");
        encodedMessage.append(selfName + ",,,,\n");
        encodedMessage.append(selfPhoto + ",,,,\n");
        for (Course course : this.selfCourses) {
            encodedMessage.append(course.getYear() + ",");
            encodedMessage.append(encodeQuarter(course.getQuarter()) + ",");
            encodedMessage.append(course.getSubject() + ",");
            encodedMessage.append(course.getNumber() + ",");
            encodedMessage.append(course.getClassSize() + "\n");
        }

        return encodedMessage.toString();
    }

    // When the start button is clicked
    public void onStartClicked(View view) {

        Log.d(TAG, "Start button pressed");

        if (!isNewSession) {
            showStartPopup();
        }
        else {
            synchronized (this) {
                startSearchForMatches();
            }
        }

    }


    // When the stop button is clicked
    public void onStopClicked(View view) {
        Log.d(TAG, "Stop button pressed, stopping search for matches...");

        stopSearchForMatches();

        List<Course> currentCourses = getCurrentCourses();

        //check if user has entered courses from this current quarter
//        if (this.isNewSession) {
            if (currentCourses.isEmpty()){
                showEnterSessionNameStopPopup(true);
            }
            else {
                showSelectOrEnterSessionNameStopPopup(currentCourses);
            }
//        }

        this.isNewSession = false;

    }

    public List<Course> getCurrentCourses() {
        String currentQuarter = Utilities.getCurrentQuarter();
        String currentYear = Utilities.getCurrentYear();

        //get profile of current user
        List<Course> currentCourses = new ArrayList<>();

        for (Course course : this.selfCourses){
            if (course.getQuarter().equals(currentQuarter) && course.getYear().equals(currentYear)){
                currentCourses.add(course);
            }
        }

        return currentCourses;
    }

    // When a match in the recycler view is clicked
    public void onMatchRowSelected(View view) {
        Log.d(TAG, "Match selected, displaying match profile and course information");

        // Send the match's profile id to the activity responsible for showing the profile
        TextView matchProfileIdView = view.findViewById(R.id.match_profile_id_view);
        String matchId = matchProfileIdView.getText().toString();

        Intent intent = new Intent(this, MatchProfileActivity.class);
        intent.putExtra("match_id", matchId);
        startActivity(intent);
    }

    // For testing purposes, visibility is set to gone for demoing and actual use
    public void onNearbyClicked(View view) {
        Intent intent = new Intent(this, MockingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (this.isSearching) {
            stopSearchForMatches();
        }

        Intent intent = new Intent(this, CourseActivity.class);
        intent.putStringArrayListExtra("mocked_messages", this.mockedMessages);
        startActivity(intent);
    }

    //onClick listener for Session items within the recyclerview of the
    public void onStartPopupSessionRowSelected(View view){
        Log.d(TAG, "Previous session selected, " +
                "displaying matches in MatchActivity");

        //selected session object
        TextView selectedSessionIdView = view.findViewById(R.id.session_row_id_view);
        String selectedSessionId = selectedSessionIdView.getText().toString();
        if (this.sessionId.equals(selectedSessionId)) {
            // Discover Bluetooth messages
            this.promptDialog.cancel();
            this.promptDialog = null;

            startSearchForMatches();

        }
        else {
            unsetLastSession();
            Intent intent = new Intent(this, MatchActivity.class);
            intent.putExtra("session_id", selectedSessionId);
            startActivity(intent);
        }

    }

    public void onStartPopupCreateNewSessionClicked(View view) {
        unsetLastSession();

        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra("session_id", "");
        startActivity(intent);
    }

    //helper function to create session list dialog box with previous sessions
    private void showStartPopup() {
        Log.d(TAG, "creating session list prompt AlertDialog");

        //populate sessionsList with previously saved sessions
        LayoutInflater inflater = getLayoutInflater();
        View contextView = inflater.inflate(R.layout.select_or_new_session_popup, null);

        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(this);

        RecyclerView sessionsView = contextView.findViewById(R.id.session_recycler_view);

        sessionsView.setLayoutManager(new LinearLayoutManager(this));
        sessionsView.setHasFixedSize(true);

        this.backgroundThreadExecutor.submit(() -> {
            this.allSessions = this.db.sessionDao().getAllSessions();
        });

        SessionsAdapter adapter = new SessionsAdapter(this.allSessions);
        sessionsView.setAdapter(adapter);
        promptBuilder.setView(contextView);

        this.promptDialog = promptBuilder.create();
        this.promptDialog.show();
    }

    //dialog prompt listener
    public void onStopPopupCourseRowSelected(View view){
        Log.d(TAG, "Course selected from second stop prompt's course items," +
                "item becomes selected");

        //find selected item from recyclerview, grab views for course info
        TextView courseNameView = view.findViewById(R.id.session_course_name_view);
        TextView courseNumberView = view.findViewById(R.id.session_course_number_view);

        changeSessionName(courseNameView.getText() + " " +
                courseNumberView.getText());

        this.promptDialog.cancel();
        this.promptDialog = null;
    }

    //dialog prompt listener for submit button when entering a course name
    public void onStopPopupSubmitSessionNameClicked(View view) {
        Log.d(TAG, "Save Session Name button pressed on first stop prompt," +
                " saving session with name given...");

        // TODO: verify that the inputted course name is valid
        EditText enteredCourseName = this.promptDialog.findViewById(R.id.session_name_input_view);
        String courseName = enteredCourseName.getText().toString().trim();

        if (isValidCourseName(courseName)) {
            changeSessionName(courseName);

            this.promptDialog.cancel();
            this.promptDialog = null;
        }

    }

    private boolean isValidCourseName(String courseName) {
        String[] courseInfo = courseName.split(" ");
        if (courseInfo.length < 2) {
            Utilities.showError(this, "Error: Invalid Session Name", "Please enter a course name.");
            return false;
        }

        for (char c : courseInfo[0].toCharArray()) {
            if (!Character.isLetter(c)) {
                Utilities.showError(this, "Error: Invalid Session Name", "Please enter a course name.");
                return false;
            }
        }
        for (int i = 0; i < courseInfo[1].length() - 1; i++) {
            if (!Character.isDigit(courseInfo[1].charAt(i))) {
                Utilities.showError(this, "Error: Invalid Session Name", "Please enter a course name.");
                return false;
            }
        }

        if (!Character.isDigit(courseInfo[1].charAt(courseInfo[1].length() - 1)) && !Character.isLetter(courseInfo[1].charAt(courseInfo[1].length() - 1))) {
            Utilities.showError(this, "Error: Invalid Session Name", "Please enter a course name.");
            return false;
        }

        return true;
    }

    //dialog prompt listener
    public void onStopPopupEnterSessionNameClicked(View view) {
        Log.d(TAG, "Enter Session Name Button pressed," +
                " creating first stop prompt to enter name");

        showEnterSessionNameStopPopup(false); //deployed from second prompt
    }


    //helper function to create first possible stop prompt to enter a name for a created session
    //and it is also the "default" stop prompt
    public void showEnterSessionNameStopPopup(Boolean isOnly){
        Log.d(TAG, "creating first stop prompt AlertDialog");

        LayoutInflater inflater = getLayoutInflater();
        View contextView = inflater.inflate(R.layout.enter_session_name_popup, null);

        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(this);

        promptBuilder.setView(contextView);

        if(!isOnly){ this.promptDialog.cancel(); } //if deployed from second stop prompt, close
        //second prompt

        this.promptDialog = promptBuilder.create();
        this.promptDialog.show();
    }

    public void showSelectOrEnterSessionNameStopPopup(List<Course> list){
        Log.d(TAG, "creating second stop prompt AlertDialog");

        LayoutInflater inflater = getLayoutInflater();
        View contextView = inflater.inflate(R.layout.select_or_enter_session_name_popup, null);

        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(this);


        RecyclerView sessionsView = contextView.findViewById(R.id.classes_list);

        sessionsView.setLayoutManager(new LinearLayoutManager(this));
        sessionsView.setHasFixedSize(true);

        SessionCoursesAdapter adapter = new SessionCoursesAdapter(list);
        sessionsView.setAdapter(adapter);
        promptBuilder.setView(contextView);

        this.promptDialog = promptBuilder.create();
        this.promptDialog.show();
    }

    private void unsetLastSession() {
        backgroundThreadExecutor.submit(() -> {
            this.session.setIsLastSession(false);
            this.db.sessionDao().update(this.session);
        });
    }

    private void setLastSession() {
        backgroundThreadExecutor.submit(() -> {
            this.session.setIsLastSession(true);
            this.db.sessionDao().update(this.session);
        });
    }

    private void changeSessionName(String newName) {
        backgroundThreadExecutor.submit(() -> {
            this.session.setName(newName);
            this.db.sessionDao().update(this.session);
            this.allSessions = this.db.sessionDao().getAllSessions();
        });
        this.sessionNameView.setText(newName);
        this.isNewSession = false;
    }

    private String getCurrentTimestamp() {
        DateFormat df = new SimpleDateFormat("M'/'d'/'yy h:mma");
        String timestamp = df.format(Calendar.getInstance().getTime());
        return timestamp;
    }

    // TODO
    public void onSortFilterClicked(View view) {
    }

}



