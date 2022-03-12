/*
 * This file is capable of assorting and displaying a list of matches to the user. Allowing the user
 * to interact with the matches through sort/filter functionality.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */
package com.example.birdsofafeather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.db.Wave;
import com.example.birdsofafeather.mutator.filter.CurrentQuarterFilter;
import com.example.birdsofafeather.mutator.filter.FavoritesFilter;
import com.example.birdsofafeather.mutator.Mutator;
import com.example.birdsofafeather.mutator.sorter.QuantitySorter;
import com.example.birdsofafeather.mutator.sorter.RecencySorter;
import com.example.birdsofafeather.mutator.sorter.SizeSorter;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;
import com.example.birdsofafeather.db.Session;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class refers to the screen where the user can see discovered users and search for more
 * discovered users.
 */
public class MatchActivity extends AppCompatActivity {
    // Log tag
    private final String TAG = "<Match>";

    // DB/Threading fields
    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<List<Wave>> f1;
    private Future<Profile> f2;
    private Future<List<Course>> f3;
    private Future<List<Session>> f4;
    private Future<Void> f5;
    private Future<Session> f6;
    private Future<List<String>> f7;

    // Self information fields
    private List<Session> allSessions;
    private Session session;
    private Profile selfProfile;
    private List<Course> selfCourses;
    private List<Course> currentCourses;


    // Flags
    private boolean isNewSession = false;
    private boolean isSearching = false;
    private boolean isTesting = false;
    private boolean isDone = false;

    // Sorting
    private Mutator mutator;

    // View/UI fields
    private TextView sessionLabel;
    private Button startButton;
    private Button stopButton;
    private Spinner sortFilterSpinner;
    private RecyclerView matchesRecyclerView;

    // Popup field
    private AlertDialog currentPopup;

    // Nearby/MatchViewMediator fields
    private BoFMessagesClient messagesClient;
    private BoFMessageListener messageListener;
    private Message selfMessage;
    private ArrayList<String> mockedMessages;
    private BoFObserver mvm;

    /**
     * Initializes the activity and screen for MatchActivity.
     *
     * @param savedInstanceState A bundle that contains information regarding layout and data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        setTitle("Birds of a Feather");

        Log.d(TAG, "Setting up Match Screen");

        // Initialize testing DB if testing, otherwise use normal DB
        this.isTesting = getIntent().getBooleanExtra("isTesting", false);
        if (this.isTesting) {
            Log.d(TAG, "Currently testing, using test DB!");
            this.db = AppDatabase.useTestSingleton(this);
        }
        else {
            Log.d(TAG, "Not currently testing, using regular DB!");
            this.db = AppDatabase.singleton(this);
        }

        // Get self profile
        this.f2 = this.backgroundThreadExecutor.submit(() -> this.db.profileDao().getSelfProfile(true));
        try {
            this.selfProfile = this.f2.get();
            Log.d(TAG, "Self profile retrieved!");
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving self profile!");
            e.printStackTrace();
        }

        // Get list of all self courses
        this.f3 = this.backgroundThreadExecutor.submit(() -> this.db.courseDao().getCoursesByProfileId(this.selfProfile.getProfileId()));
        try {
            this.selfCourses = this.f3.get();
            Log.d(TAG, "Self courses retrieved!");
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving self courses!");
            e.printStackTrace();
        }

        // Get list of all sessions
        this.f4 = this.backgroundThreadExecutor.submit(() -> this.db.sessionDao().getAllSessions());
        try {
            this.allSessions = this.f4.get();
            Log.d(TAG, "All sessions retrieved!");
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving all sessions!");
            e.printStackTrace();
        }

        // Get current courses
        this.currentCourses = getCurrentCourses();

        this.messagesClient = new BoFMessagesClient(Nearby.getMessagesClient(this));

        // Get mocked messages
        this.mockedMessages = getIntent().getStringArrayListExtra("mocked_messages");
        if (this.mockedMessages == null) {
            this.mockedMessages = new ArrayList<>();
            Log.d(TAG, "No mocked messages found!");
        }
        else {
            Log.d(TAG, "Mocked messages found!");
        }

        // Gets the current session depending on the "session_id" extra
        String sessionId = getIntent().getStringExtra("session_id");
        // Open/resume last session
        if (sessionId == null) {
            Log.d(TAG, "Opening last saved session!");
            this.f6 = this.backgroundThreadExecutor.submit(() -> this.db.sessionDao().getLastSession(true));

            try {
                this.session = this.f6.get();
                Log.d(TAG, "Retrieved last saved session!");
            } catch (Exception e) {
                Log.e(TAG, "Error retrieving last session!");
                e.printStackTrace();
            }
        }
        // Create a new session
        else if (sessionId.equals("")) {
            Log.d(TAG, "Making new session!");
            sessionId = UUID.randomUUID().toString();
            this.session = new Session(sessionId, getCurrentTimestamp(), true);
            this.f5 = this.backgroundThreadExecutor.submit(() -> {
                this.db.sessionDao().insert(this.session);
                return null;
            });
            this.isNewSession = true;
        }
        // Resume a previous session
        else {
            Log.d(TAG, "Resuming previous session!");
            String finalSessionId = sessionId;
            this.f6 = this.backgroundThreadExecutor.submit(() -> this.db.sessionDao().getSession(finalSessionId));

            try {
                this.session = this.f6.get();
                Log.d(TAG, "Retrieved previous session!");
            } catch (Exception e) {
                Log.e(TAG, "Error retrieving previous session!");
                e.printStackTrace();
            }
        }

        // Sets current session to the last session
        setLastSession();

        Log.d(TAG, "Setting and configuring views...");
        // View initializations
        this.matchesRecyclerView = findViewById(R.id.matches_view);
        this.stopButton = findViewById(R.id.stop_button);
        this.startButton = findViewById(R.id.start_button);
        this.sessionLabel = findViewById(R.id.session_name_view);


        // Set up dynamic sort/filter spinner
        this.sortFilterSpinner = findViewById(R.id.sort_filter_spinner);
        List<String> mutations = new ArrayList<>(Arrays.asList("No Sort/Filter", "Favorites Only", "Prioritize Recent", "Prioritize Small Classes", "This Quarter Only"));
        ArrayAdapter<String> sort_filter_adapter = new ArrayAdapter<>(this, R.layout.sort_filter_spinner_item_text, mutations);
        sort_filter_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.sortFilterSpinner.setAdapter(sort_filter_adapter);

        // Setting spinner to prior sort/filter selection
        for (int i = 0; i < this.sortFilterSpinner.getCount(); i++) {
            if (this.sortFilterSpinner.getItemAtPosition(i).equals(this.session.getSortFilter())) {
                this.sortFilterSpinner.setSelection(i);
            }
        }

        // Setting a selection listener for sort/filter spinner
        Context context = this;
        this.sortFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            /**
             * Allows the spinner to create an interaction with the list.
             *
             * @param parent Given parent view
             * @param view Given view
             * @param position Given position
             * @param id Given id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                switch(sortFilterSpinner.getSelectedItem().toString()) {
                    case "No Sort/Filter":
                        Log.d(TAG, "No sort/filter selected, using QuantitySorter!");
                        mutator = new QuantitySorter(context);
                        break;
                    case "Prioritize Recent":
                        Log.d(TAG, "Prioritize Recent selected, using RecencySorter!");
                        mutator = new RecencySorter(context);
                        break;
                    case "Prioritize Small Classes":
                        Log.d(TAG, "Prioritize Small Classes selected, using SizeSorter!");
                        mutator = new SizeSorter(context);
                        break;
                    case "This Quarter Only":
                        Log.d(TAG, "This Quarter Only selected, using CurrentQuarterFilter!");
                        mutator = new CurrentQuarterFilter(context);
                        break;
                    case "Favorites Only":
                        Log.d(TAG, "Favorites Only selected, using FavoritesFilter!");
                        mutator = new FavoritesFilter(context);
                        break;
                }

                session.setSortFilter(sortFilterSpinner.getSelectedItem().toString());
                f5 = backgroundThreadExecutor.submit(() -> {
                    db.sessionDao().update(session);
                    return null;
                });

                mvm.setMutator(mutator);
                mvm.updateView();
            }

            /**
             * Ensures that nothing occurs when nothing is selected from spinner.
             *
             * @param parent Given parent view
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "No sorter/filter is selected in the sort/filter spinner!");
            }
        });

        Log.d(TAG, "Setting session name label to current session name!");
        this.sessionLabel.setText(this.session.getName());

        Log.d(TAG, "Setting up Nearby and MVM dependencies...");
        // Setting up Nearby and MatchesViewMediator dependencies
        this.mvm = new MatchesViewMediator(this, this.db, this.mutator, this.matchesRecyclerView, this.session.getSessionId());
        this.messageListener = new BoFMessageListener(this.session.getSessionId(), this);
        this.messageListener.register(this.mvm);
        this.selfMessage = null;
    }

    /**
     * Unpublishes all outgoing waves, closes any popups, unsets the current session as the last session,
     * and cancels any nonnull futures to prepare for opening another session.
     *
     */
    @Override
    protected void onDestroy() {
        if (this.currentPopup != null) {
            Log.d(TAG, "Closing current popup!");
            this.currentPopup.cancel();
        }
        if (this.isDone) {
            unsetLastSession();
        }

        if (this.f1 != null) {
            this.f1.cancel(true);
        }
        if (this.f2 != null) {
            this.f2.cancel(true);
        }
        if (this.f3 != null) {
            this.f3.cancel(true);
        }
        if (this.f4 != null) {
            this.f4.cancel(true);
        }
        if (this.f5 != null) {
            this.f5.cancel(true);
        }
        if (this.f6 != null) {
            this.f6.cancel(true);
        }
        if (this.f6 != null) {
            this.f6.cancel(true);
        }
        if (this.f7 != null) {
            this.f7.cancel(true);
        }
        this.messageListener.unregister(this.mvm);
        super.onDestroy();
        Log.d(TAG, "MatchActivity destroyed!");
    }

    /**
     * Sets the current session as the last session so that the current session will be resumed by default if the app crashes or closes.
     */
    private void setLastSession() {
        Log.d(TAG, "Setting this session as the last session.");
        backgroundThreadExecutor.submit(() -> {
            this.session.setIsLastSession(true);
            this.db.sessionDao().update(this.session);
        });
    }

    /**
     * Unsets the current session as the last session and also removes all incoming and outgoing waves
     */
    private void unsetLastSession() {
        Log.d(TAG, "Unsetting this session as the last session.");
        backgroundThreadExecutor.submit(() -> {
            this.session.setIsLastSession(false);
            this.db.sessionDao().update(this.session);

            removeWaving();
            removeWaved();
            removeOutgoingWaves();
        });
    }

    /**
     * Removes the incoming waves from the database.
     */
    private void removeWaving() {
        Log.d(TAG, "Clearing all waving profiles!");
        List<Profile> wavingProfiles = this.db.profileDao().getWavingProfiles(true);
        for (Profile profile : wavingProfiles) {
            profile.setIsWaving(false);
            this.db.profileDao().update(profile);
        }
    }

    /**
     * Removes the outgoing waves from the database.
     */
    private void removeWaved() {
        Log.d(TAG, "Clearing all waved profiles!");
        List<Profile> wavedProfiles = this.db.profileDao().getWavedProfiles(true);
        for (Profile profile : wavedProfiles) {
            profile.setIsWaved(false);
            this.db.profileDao().update(profile);
        }
    }

    /**
     * Unpublishes outgoing waves in the database.
     */
    private void removeOutgoingWaves() {
        // Get and unpublish outgoing waves
        Log.d(TAG, "Clearing all outgoing waves!");
        List<Wave> waves = this.db.waveDao().getAllWaves();
        for (Wave wave : waves) {
            Log.d(TAG, "Found outgoing wave, clearing now...");

            Message waveMessage = new Message(wave.getWave().getBytes(StandardCharsets.UTF_8));
            this.messagesClient.unpublish(waveMessage);
            this.db.waveDao().delete(wave);
        }
    }

    /**
     * Allows the user to view sessions or start for matches, when the start button is clicked.
     *
     * @param view The start button.
     */
    public void onStartClicked(View view) {

        Log.d(TAG, "Start button pressed");

        if (!isNewSession) {
            Log.d(TAG, "Current session is not a new session!");
            showStartPopup();
        }
        else {
            Log.d(TAG, "Current session is a new session!");
            startSearchForMatches();
        }
    }

    /**
     * Starts searching for matches.
     */
    public void startSearchForMatches() {
        Log.d(TAG, "Starting search for matches...");
        this.isSearching = true;

        this.stopButton.setVisibility(View.VISIBLE);
        this.startButton.setVisibility(View.GONE);
        this.selfMessage = new Message(Utilities.encodeSelfInformation(this.selfProfile, this.selfCourses).getBytes(StandardCharsets.UTF_8));
        this.messagesClient.subscribe(this.messageListener);
        this.messagesClient.publish(this.selfMessage);


        // Discover mocked messages
        for (String msg : this.mockedMessages) {
            Log.d(TAG, "Mocked message found...");
            this.messageListener.onFound(new Message(msg.getBytes(StandardCharsets.UTF_8)));
        }

        this.mockedMessages.clear();
    }

    /**
     * Allows the user to name a current session of matches or select another session, when the
     * stop button is clicked.
     *
     * @param view The stop button.
     */
    public void onStopClicked(View view) {
        Log.d(TAG, "Stop button pressed, stopping search for matches...");

        stopSearchForMatches();
        changeSessionName(this.session.getName());

        //check if user has entered courses from this current quarter
        if (this.currentCourses.isEmpty()){
            Log.d(TAG, "No current courses!");
            showEnterSessionNameStopPopup(false);
        }
        else {
            Log.d(TAG, "Have current courses!");
            showSelectOrEnterSessionNameStopPopup();
        }

        // Now that a search has occurred in the session, the session is no longer a new session
        this.isNewSession = false;
    }

    /**
     * Stops searching for new matches.
     */
    public void stopSearchForMatches() {
        Log.d(TAG, "Stopping search for matches...");
        this.isSearching = false;

        this.startButton.setVisibility(View.VISIBLE);
        this.stopButton.setVisibility(View.GONE);
        this.messagesClient.unpublish(this.selfMessage);
        this.messagesClient.unsubscribe(this.messageListener);
    }

    /**
     * On click method for when the user selects a match row.
     *
     * @param view The selected match row.
     */
    public void onMatchRowSelected(View view) {
        Log.d(TAG, "Match selected, displaying match profile and course information!");

        // Send the match's profile id to the activity responsible for showing the profile
        TextView matchProfileIdView = view.findViewById(R.id.match_profile_id_view);
        String matchId = matchProfileIdView.getText().toString();

        Intent intent = new Intent(this, MatchProfileActivity.class);
        intent.putExtra("session_id", this.session.getSessionId());
        intent.putExtra("match_id", matchId);
        intent.putStringArrayListExtra("mocked_messages", this.mockedMessages);
        startActivity(intent);
        finish();
    }

    /**
     * On click method for when the user clicks the favorite star on a corresponding match row.
     *
     * @param view The favorite star button corresponding to a match row.
     */
    public void onFavoriteStarClicked(View view) {
        Log.d(TAG, "Favorite star clicked!");

        // Send the match's profile id to the activity responsible for showing the profile
        ViewGroup vg = (ViewGroup) view.getParent();
        TextView matchProfileIdView = vg.findViewById(R.id.match_profile_id_view);
        String matchId = matchProfileIdView.getText().toString();
        ImageView star = view.findViewById(R.id.star);

        this.f2 = this.backgroundThreadExecutor.submit(() -> this.db.profileDao().getProfile(matchId));

        Profile matchProfile;
        try {
            matchProfile = this.f2.get();

            // Match already a favorite, need to unfavorite
            if (matchProfile.getIsFavorite()) {
                matchProfile.setIsFavorite(false);

                Toast.makeText(this, "Unsaved from Favorites!", Toast.LENGTH_SHORT).show();
                star.setImageResource(R.drawable.hollow_star);
                Log.d(TAG, "Unsaved match from favorites!");
            }
            // Match not already a favorite, need to favorite
            else {
                matchProfile.setIsFavorite(true);

                Toast.makeText(this, "Saved to Favorites!", Toast.LENGTH_SHORT).show();
                // Update UI to reflect that the match is no longer a favorite
                star.setImageResource(R.drawable.filled_star);
                Log.d(TAG, "Saved match from favorites!");
            }

            // Update DB to reflect change in favorite status for match
            Profile finalMatchProfile = matchProfile;
            backgroundThreadExecutor.submit(() -> {
                this.db.profileDao().update(finalMatchProfile);
                Log.d(TAG, "Updated favorite status in DB!");
            });

            // Update match view in light of changes
            this.mvm.updateView();
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving match profile!");
            e.printStackTrace();
        }
    }

    /**
     * On click method for when the user clicks on the nearby button.
     *
     * @param view The nearby button.
     */
    public void onNearbyClicked(View view) {
        Log.d(TAG, "Nearby button clicked, moving to the mocking screen!");
        Intent intent = new Intent(this, MockingActivity.class);
        intent.putExtra("session_id", this.session.getSessionId());
        intent.putStringArrayListExtra("mocked_messages", this.mockedMessages);
        startActivity(intent);
        finish();
    }

    /**
     * On click method for when the user clicks on the session name label to change the session name.
     *
     * @param view The view of the session name label.
     */
    public void onSessionLabelClicked(View view) {
        showSelectOrEnterSessionNameStopPopup();
    }

    /**
     * Overrides the back button to return to the CourseActivity to add more courses.
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button pressed!");
        if (this.isSearching) {
            Log.e(TAG, "Currently searching for matches...");
            stopSearchForMatches();
        }

        Log.d(TAG, "Moving back to the add courses screen!");
        Intent intent = new Intent(this, CourseActivity.class);
        intent.putExtra("session_id", this.session.getSessionId());
        intent.putStringArrayListExtra("mocked_messages", this.mockedMessages);
        intent.putExtra("isBack", true);
        startActivity(intent);
        finish();
    }

    /**
     * Create and show the start popup asking whether to create a new session or resume a previous session.
     */
    private void showStartPopup() {
        Log.d(TAG, "Creating start popup...");

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

        SessionsViewAdapter adapter = new SessionsViewAdapter(this.allSessions);
        sessionsView.setAdapter(adapter);
        promptBuilder.setView(contextView);

        Log.d(TAG, "Start popup created, now showing!");
        this.currentPopup = promptBuilder.create();
        this.currentPopup.show();
    }

    /**
     * Allows the user to be able to view the clicked session from a list of previous sessions.
     *
     * @param view The selected session row in the start popup
     */
    public void onStartPopupSessionRowSelected(View view){
        //selected session object
        TextView selectedSessionIdView = view.findViewById(R.id.session_row_id_view);
        String selectedSessionId = selectedSessionIdView.getText().toString();
        TextView selectedSessionNameView = view.findViewById(R.id.session_row_name_view);
        String selectedSessionName = selectedSessionNameView.getText().toString();

        Log.d(TAG, "Previous session " + selectedSessionName + " selected, resuming previous session...");
        if (this.session.getSessionId().equals(selectedSessionId)) {
            Log.d(TAG, "Previous session is current session!");
            // Discover Bluetooth messages
            this.currentPopup.cancel();
            startSearchForMatches();
        }
        else {
            Log.d(TAG, "Resuming previous session!");
            this.isDone = true;
            Intent intent = new Intent(this, MatchActivity.class);
            intent.putExtra("session_id", selectedSessionId);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Allows the user to be able to create a new session when option is clicked.
     *
     * @param view The create new session button in the start popup
     */
    public void onStartPopupCreateNewSessionClicked(View view) {
        Log.d(TAG, "Creating new session!");
        this.isDone = true;
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra("session_id", "");
        startActivity(intent);
        finish();
    }

    /**
     * Create and show the stop popup asking whether to select a session or enter a session name.
     */
    public void showSelectOrEnterSessionNameStopPopup(){
        Log.d(TAG, "Creating stop prompt with select and enter capability...");

        LayoutInflater inflater = getLayoutInflater();
        View contextView = inflater.inflate(R.layout.select_or_enter_session_name_popup, null);

        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(this);


        RecyclerView sessionsView = contextView.findViewById(R.id.classes_list);

        sessionsView.setLayoutManager(new LinearLayoutManager(this));
        sessionsView.setHasFixedSize(true);

        CurrentCoursesViewAdapter adapter = new CurrentCoursesViewAdapter(this.currentCourses);
        sessionsView.setAdapter(adapter);
        promptBuilder.setView(contextView);

        Log.d(TAG, "Stop popup created, now showing!");
        this.currentPopup = promptBuilder.create();
        this.currentPopup.show();
    }

    /**
     * On click method for when a course row is selected in the stop popup
     * @param view The selected course row in the stop popup.
     */
    public void onStopPopupCourseRowSelected(View view){
        //find selected item from recyclerview, grab views for course info
        TextView courseNameView = view.findViewById(R.id.course_row_subject_view);
        TextView courseNumberView = view.findViewById(R.id.course_row_number_view);
        String courseName = courseNameView.getText().toString() + " " + courseNumberView.getText().toString();

        Log.d(TAG, "Current course " + courseName + " selected, setting session name...");

        if (isValidCourseName(courseName)) {
            changeSessionName(courseName);

            this.currentPopup.cancel();
        }
        else {
            Log.e(TAG, "Error setting session name, session with name " + courseName + "already exists!");
        }
    }

    /**
     * On click method for when the submit session name button is clicked in the stop popup
     * @param view The submit session name button in the stop popup.
     */
    public void onStopPopupSubmitSessionNameClicked(View view) {
        EditText enteredCourseName = this.currentPopup.findViewById(R.id.session_name_input_view);
        String courseName = enteredCourseName.getText().toString().trim();

        Log.d(TAG, "Submit session name button clicked, setting session name to " + courseName + "!");

        if (isValidCourseName(courseName)) {
            changeSessionName(courseName);

            this.currentPopup.cancel();
        }
        else {
            Log.e(TAG, "Error setting session name, session with name " + courseName + "already exists!");
        }

    }

    /**
     * Create and show the stop popup asking to enter a session name.
     *
     * @param closeCurrentPopup whether to close the current popup or not.
     */
    public void showEnterSessionNameStopPopup(Boolean closeCurrentPopup){
        Log.d(TAG, "Creating stop prompt with enter capability...");

        LayoutInflater inflater = getLayoutInflater();
        View contextView = inflater.inflate(R.layout.enter_session_name_popup, null);

        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(this);

        promptBuilder.setView(contextView);

        if(closeCurrentPopup){ this.currentPopup.cancel(); }

        Log.d(TAG, "Stop prompt created, now showing!");
        this.currentPopup = promptBuilder.create();
        this.currentPopup.show();
    }

    /**
     * On click method for when the enter session name button is clicked in the stop popup.
     *
     * @param view The enter session name button
     */
    public void onStopPopupEnterSessionNameClicked(View view) {
        Log.d(TAG, "Enter session name button pressed!");

        showEnterSessionNameStopPopup(true); //deployed from second prompt
    }

    /**
     * Checks whether a given course name is valid.
     *
     * @param courseName The given course name.
     * @return Whether courseName is a valid course name or not.
     */
    private boolean isValidCourseName(String courseName) {
        Log.d(TAG, "Checking if name " + courseName + " is valid...");

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

        synchronized(this) {
            this.f7 = this.backgroundThreadExecutor.submit(() -> this.db.sessionDao().getAllSessionNames());

            List<String> sessionNames;
            try {
                sessionNames = this.f7.get();
                if (sessionNames.contains(courseName) && !courseName.equals(this.session.getName())) {
                    Utilities.showError(this, "Error: Invalid Session Name", "Session name already exists, please enter a unique course name.");
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    /**
     * Retrieves the self courses from the current quarter.
     *
     * @return The list of current courses
     */
    public List<Course> getCurrentCourses() {
        Log.d(TAG, "Getting current courses!");
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

    /**
     * Changes the name of the current session to newName.
     *
     * @param newName The new name of the current session.
     */
    private void changeSessionName(String newName) {
        Log.d(TAG, "Changing current session name from " + this.session.getName() + " to " + newName + "!") ;

        backgroundThreadExecutor.submit(() -> {
            this.session.setName(newName);
            this.db.sessionDao().update(this.session);
            this.allSessions = this.db.sessionDao().getAllSessions();
        });
        this.sessionLabel.setText(newName);
        this.isNewSession = false;
    }

    /**
     * Retrieves the current timestamp, new sessions by default are named with current timestamp upon creation.
     *
     * @return The formatted current timestamp String.
     */
    private String getCurrentTimestamp() {
        Log.d(TAG, "Getting current timestamp!");
        DateFormat df = new SimpleDateFormat("M'/'d'/'yy h:mma");
        return df.format(Calendar.getInstance().getTime());
    }

    /**
     * Utilized for testing the view.
     */
    public void mockSearch() {
        this.mvm.updateView();
    }
}



