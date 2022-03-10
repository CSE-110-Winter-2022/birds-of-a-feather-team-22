/*
 * This file is capable of assorting and displaying a list of matches to the user. Allowing the user
 * to interact with the matches through sort/filter functionality.
 *
 * Author: Group 22
 */
package com.example.birdsofafeather;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.birdsofafeather.mutator.filter.CurrentQuarterFilter;
import com.example.birdsofafeather.mutator.filter.FavoritesFilter;
import com.example.birdsofafeather.mutator.Mutator;
import com.example.birdsofafeather.mutator.sorter.QuantitySorter;
import com.example.birdsofafeather.mutator.sorter.RecencySorter;
import com.example.birdsofafeather.mutator.sorter.SizeSorter;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
 * This class refers to the screen where the user can see discovered users and search for more
 * discovered users.
 */
public class MatchActivity extends AppCompatActivity {
    // Log tag
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
    private List<Course> currentCourses;

    // Flags
    private boolean isNewSession = false;
    private boolean isSearching = false;

    // Sorting
    private Mutator mutator;

    // View/UI fields
    private RecyclerView matchesRecyclerView;
    private Button startButton;
    private Button stopButton;
    private Spinner sortFilterSpinner;
    private TextView sessionLabel;

    // currently open prompt
    private AlertDialog promptDialog;

    // Utilities
    private BoFMessageListener messageListener;
    private Message selfMessage;

    /**
     * Initializes the screen for this acitivity.
     *
     * @param savedInstanceState A bundle that contains information regarding layout and data
     * @return none
     */
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
            Future<Session> future = this.backgroundThreadExecutor.submit(() -> {
                this.session = this.db.sessionDao().getLastSession(true);

                return this.session;
            });

            try {
                this.session = future.get();
                this.sessionId = this.session.getSessionId();
            } catch (Exception e) {
                Log.d(TAG, "Unable to retrieve session!");
            }
        }
        else if (this.sessionId.equals("")) {
            Log.d(TAG, "Making new session!");
            this.sessionId = UUID.randomUUID().toString();
            this.session = new Session(this.sessionId, getCurrentTimestamp(), true);
            this.backgroundThreadExecutor.submit(() -> {
                this.db.sessionDao().insert(this.session);
            });
            this.isNewSession = true;
        }
        else {
            Log.d(TAG, "Resuming previous session!");
            Future<Session> future = this.backgroundThreadExecutor.submit(() -> {
                this.session = this.db.sessionDao().getSession(this.sessionId);

                return this.session;
            });

            try {
                this.session = future.get();
            } catch (Exception e) {
                Log.d(TAG, "Unable to retrieve session!");
            }
        }

        setLastSession();

        switch(this.session.getSortFilter()) {
            case "Favorites Only":
                this.mutator = (Mutator) new FavoritesFilter(this);
                break;
            case "Prioritize Recent":
                this.mutator = (Mutator) new RecencySorter(this);
                break;
            case "Prioritize Small Classes":
                this.mutator = (Mutator) new SizeSorter(this);
                break;
            case "This Quarter Only":
                this.mutator = (Mutator) new CurrentQuarterFilter(this);
                break;
            default:
                this.mutator = (Mutator) new QuantitySorter(this);
                break;
        }

        System.out.println(this.session.getSortFilter());
        System.out.println(this.session.getSortFilter().equals("Favorites Only"));

        // Grab list of discovered users to display on start and sort by number of shared courses
        this.matches = this.mutator.mutate(getCurrentMatches());

        // Get mocked messages
        this.mockedMessages = getIntent().getStringArrayListExtra("mocked_messages");
        if (this.mockedMessages == null) {
            this.mockedMessages = new ArrayList<>();
        }

        // Utilities initializations
        this.messageListener = new BoFMessageListener(this.sessionId, this);
        this.selfMessage = new Message(encodeSelfInformation().getBytes());

        // View initializations
        this.matchesRecyclerView = findViewById(R.id.matches_view);
//        this.matchesViewAdapter = new MatchViewAdapter(this.matches,this);
//        this.matchesLayoutManager = new LinearLayoutManager(this);
        this.stopButton = findViewById(R.id.stop_button);
        this.startButton = findViewById(R.id.start_button);
        this.sessionLabel = findViewById(R.id.session_name_view);
        this.sessionLabel.setText(this.session.getName());
////        this.sessionNameView = findViewById(R.id.session_name_view);
//
//        // Change session name via EditText View
//        this.sessionNameView.setText(this.session.getName());
//        this.sessionNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId==EditorInfo.IME_ACTION_DONE){
//                    sessionNameView.clearFocus();
//                    if (isValidCourseName(sessionNameView.getText().toString())) {
//                        changeSessionName(sessionNameView.getText().toString());
//                    }
//                    else {
//                        sessionNameView.setText(session.getName());
//                    }
//
//                }
//                return false;
//            }
//        });

        this.nvm = new NearbyViewMediator(this, this.mutator, this.matchesRecyclerView, this.sessionId);
        this.messageListener.register(this.nvm);

        this.sortFilterSpinner = findViewById(R.id.sort_filter_spinner);
        List<String> mutations = new ArrayList<>(Arrays.asList("No Sort/Filter", "Favorites Only", "Prioritize Recent", "Prioritize Small Classes", "This Quarter Only"));
        ArrayAdapter<String> sort_filter_adapter = new ArrayAdapter<>(this, R.layout.sort_filter_spinner_item_text, mutations);
        sort_filter_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.sortFilterSpinner.setAdapter(sort_filter_adapter);

        for (int i = 0; i < this.sortFilterSpinner.getCount(); i++) {
            if (this.sortFilterSpinner.getItemAtPosition(i).equals(this.session.getSortFilter())) {
                this.sortFilterSpinner.setSelection(i);
            }
        }

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
                        mutator = (Mutator) new QuantitySorter(context);
                        break;
                    case "Prioritize Recent":
                        mutator = (Mutator) new RecencySorter(context);
                        break;
                    case "Prioritize Small Classes":
                        mutator = (Mutator) new SizeSorter(context);
                        break;
                    case "This Quarter Only":
                        mutator = (Mutator) new CurrentQuarterFilter(context);
                        break;
                    case "Favorites Only":
                        mutator = (Mutator) new FavoritesFilter(context);
                        break;
                }

                session.setSortFilter(sortFilterSpinner.getSelectedItem().toString());
                backgroundThreadExecutor.submit(() -> {
                    db.sessionDao().update(session);
                });

                nvm.setMutator(mutator);
                nvm.updateMatchesList();
            }

            /**
             * Ensures the nothing occurs when nothing is selected from spinner.
             *
             * @param parent Given parent view
             * @return none
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        this.nvm.updateMatchesList();
        this.currentCourses = getCurrentCourses();

        System.out.println(selfProfile.getProfileId());
    }

    /**
     * Closes/destoryed the current activity.
     *
     * @param
     * @return none
     */
    @Override
    protected void onDestroy() {
        setLastSession();
        super.onDestroy();
    }

    /**
     * Provides a list of all the current matches to the user.
     *
     * @param
     * @return A list of profiles which have courses matches to the user.
     */
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

    /**
     * Start searching for matches when the user enables student searches.
     *
     * @param
     * @return none
     */
    public void startSearchForMatches() {
        Log.d(TAG, "Starting search for matches...");
        this.isSearching = true;

        this.stopButton.setVisibility(View.VISIBLE);
        this.startButton.setVisibility(View.GONE);

        Nearby.getMessagesClient(this).publish(this.selfMessage);
        Log.d(TAG, "Published a message: " + this.selfMessage.getContent().toString());
        Nearby.getMessagesClient(this).subscribe(this.messageListener);
        Log.d(TAG, "New listener subscribed!");

        // Discover mocked messages
        for (String msg : this.mockedMessages) {
            this.messageListener.onFound(new Message(msg.getBytes()));
        }

        this.mockedMessages.clear();

    }

    /**
     * Prevents user from receiving any new matches, stops student searches.
     *
     * @param
     * @return none
     */
    public void stopSearchForMatches() {
        Log.d(TAG, "Stopping search for matches...");
        this.isSearching = false;

        this.startButton.setVisibility(View.VISIBLE);
        this.stopButton.setVisibility(View.GONE);

        Nearby.getMessagesClient(this).unpublish(this.selfMessage);
        Log.d(TAG, "Unpublished a message: " + this.selfMessage.getContent().toString());
        Nearby.getMessagesClient(this).unsubscribe(this.messageListener);
        Log.d(TAG, "Listener has been unsubscribed!");
    }

    /**
     * Encodes the full quarter name to an abbreviation.
     *
     * @param quarter A given quarter
     * @return The abbreviation of the full quarter name
     */
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

    /**
     * Encodes the information of the self user into a CSV format.
     *
     * @param
     * @return Returns a CSV format representation of the user's information
     */
    public String encodeSelfInformation() {
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

    /**
     * Allows the user to view sessions or start for matches, when the start button is clicked.
     *
     * @param view The given view
     * @return none
     */
    public void onStartClicked(View view) {

        Log.d(TAG, "Start button pressed");

        if (!isNewSession) {
            showStartPopup();
        }
        else {
            startSearchForMatches();
        }
    }


    /**
     * Allows the user to name a current session of matches or select another session, when the
     * stop button is clicked.
     *
     * @param view The given view
     * @return none
     */
    // When the stop button is clicked
    public void onStopClicked(View view) {
        Log.d(TAG, "Stop button pressed, stopping search for matches...");

        stopSearchForMatches();
        changeSessionName(this.session.getName());

        //check if user has entered courses from this current quarter
//        if (this.isNewSession) {
            if (this.currentCourses.isEmpty()){
                showEnterSessionNameStopPopup(true);
            }
            else {
                showSelectOrEnterSessionNameStopPopup();
            }
//        }

        this.isNewSession = false;

    }

    /**
     * Retrieves the current courses from present time.
     *
     * @param
     * @return A list of current courses
     */
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

    /**
     * Allows the user to view a match when they have been clicked on.
     *
     * @param view The given view
     * @return none
     */
    public void onMatchRowSelected(View view) {
        Log.d(TAG, "Match selected, displaying match profile and course information");

        // Send the match's profile id to the activity responsible for showing the profile
        TextView matchProfileIdView = view.findViewById(R.id.match_profile_id_view);
        String matchId = matchProfileIdView.getText().toString();

        Intent intent = new Intent(this, MatchProfileActivity.class);
        intent.putExtra("match_id", matchId);
        startActivity(intent);
    }

    /**
     * Allows the user to be able to click on the favorite button to a match and updates it to the
     * database.
     *
     * @param view The given view
     * @return none
     */
    public void onFavoriteStarClicked(View view) {
        Log.d(TAG, "Making selected match a favorite.");

        // Send the match's profile id to the activity responsible for showing the profile
        ViewGroup vg = (ViewGroup) view.getParent();
        TextView matchProfileIdView = vg.findViewById(R.id.match_profile_id_view);
        String matchId = matchProfileIdView.getText().toString();
        ImageView favoriteStar = view.findViewById(R.id.star);
//        CheckBox checkBox = (CheckBox) view;

        Future<Profile> future = this.backgroundThreadExecutor.submit(() -> {
           return this.db.profileDao().getProfile(matchId);
        });

        Profile matchProfile = null;
        try {
            matchProfile = future.get();
            // Match already a favorite, need to unfavorite
            if (matchProfile.getIsFavorite()) {
                matchProfile.setIsFavorite(false);


                Toast.makeText(this, "Unsaved from Favorites!", Toast.LENGTH_SHORT).show();
//                favoriteStar.setText("☆");
//                favoriteStar.setTextColor(Color.parseColor("#BBBBBB"));
                favoriteStar.setImageResource(R.drawable.hollow_star);
//                checkBox.getButtonDrawable().setColorFilter(0xFF808080, PorterDuff.Mode.SRC_ATOP);
            }
            // Match not already a favorite, need to favorite
            else {
                matchProfile.setIsFavorite(true);

                Toast.makeText(this, "Saved to Favorites!", Toast.LENGTH_SHORT).show();
                // Update UI to reflect that the match is no longer a favorite
                favoriteStar.setImageResource(R.drawable.filled_star);
//                favoriteStar.setText("★");
//                favoriteStar.setTextColor(Color.parseColor("#FFFF00"));
            }

            // Update DB to reflect change in favorite status for match
            Profile finalMatchProfile = matchProfile;
            backgroundThreadExecutor.submit(() -> {
                this.db.profileDao().update(finalMatchProfile);
            });
            this.nvm.updateMatchesList();
        } catch (Exception e) {
            Log.d(TAG, "Could not retrieve match profile");
        }

    }

    /**
     * Utilizes to test visibility for the nearby button.
     *
     * @param view The given view
     * @return none
     */
    public void onNearbyClicked(View view) {
        Intent intent = new Intent(this, MockingActivity.class);
        startActivity(intent);
    }

    /**
     * Allows the user to be able to press the back button and opens the course activity screen.
     *
     * @param
     * @return none
     */
    @Override
    public void onBackPressed() {
        if (this.isSearching) {
            stopSearchForMatches();
        }

        Intent intent = new Intent(this, CourseActivity.class);
        intent.putStringArrayListExtra("mocked_messages", this.mockedMessages);
        startActivity(intent);
    }

    /**
     * Allows the user to be able to view the clicked session from a list of previous sessions.
     *
     * @param view The given view
     * @return none
     */
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
            clearWaves();

            Intent intent = new Intent(this, MatchActivity.class);
            intent.putExtra("session_id", selectedSessionId);
            startActivity(intent);
        }

    }

    /**
     * Allows the user to be able to create a new session when option is clicked.
     *
     * @param view The given view
     * @return none
     */
    public void onStartPopupCreateNewSessionClicked(View view) {
        unsetLastSession();
        clearWaves();

        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra("session_id", "");
        startActivity(intent);
    }

    /**
     * Assists in creating a session list dialog box with previous sessions.
     */
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

        synchronized(this) {
            Future<List<String>> future = this.backgroundThreadExecutor.submit(() -> this.db.sessionDao().getAllSessionNames());

            List<String> sessionNames = null;
            try {
                sessionNames = future.get();
            } catch (Exception e) {
                return false;
            }

            if (sessionNames.contains(courseName) && !courseName.equals(this.session.getName())) {
                Utilities.showError(this, "Error: Invalid Session Name", "Session name already exists, please enter a unique course name.");
                return false;
            }
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

    public void showSelectOrEnterSessionNameStopPopup(){
        Log.d(TAG, "creating second stop prompt AlertDialog");

        LayoutInflater inflater = getLayoutInflater();
        View contextView = inflater.inflate(R.layout.select_or_enter_session_name_popup, null);

        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(this);


        RecyclerView sessionsView = contextView.findViewById(R.id.classes_list);

        sessionsView.setLayoutManager(new LinearLayoutManager(this));
        sessionsView.setHasFixedSize(true);

        SessionCoursesAdapter adapter = new SessionCoursesAdapter(this.currentCourses);
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
        this.sessionLabel.setText(newName);
        this.isNewSession = false;
    }

    private String getCurrentTimestamp() {
        DateFormat df = new SimpleDateFormat("M'/'d'/'yy h:mma");
        String timestamp = df.format(Calendar.getInstance().getTime());
        return timestamp;
    }

    private void clearWaves() {
        backgroundThreadExecutor.submit(() -> {
            List<Profile> wavingProfiles = this.db.profileDao().getWavingProfiles(true);
            for (Profile profile : wavingProfiles) {
                profile.setIsWaving(false);
                this.db.profileDao().update(profile);
            }
        });
    }

    public void onSessionLabelClicked(View view) {
        showSelectOrEnterSessionNameStopPopup();
    }
}



