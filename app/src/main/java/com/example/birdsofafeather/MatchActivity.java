/*
 * This file is capable of assorting and displaying a list of matches to the user. Allowing the user
 * to interact with the matches through sort/filter functionality.
 *
 * Author: Group 22
 */
package com.example.birdsofafeather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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

import java.nio.charset.StandardCharsets;
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
    private List<Session> allSessions;
    private Session session;
    private String sessionId;
    private Profile selfProfile;
    private List<Course> selfCourses;
    private List<Course> currentCourses;

    private ArrayList<String> mockedMessages;
    private ArrayList<Message> wavedMessages;
    private BoFObserver nvm;


    // Flags
    private boolean isNewSession = false;
    private boolean isSearching = false;
    private boolean isTesting = false;

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

    private Future<List<String>> f1;
    private Future<Profile> f2;
    private Future<List<Course>> f3;
    private Future<List<Session>> f4;
    private Future<Void> f5;

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

        this.isTesting = getIntent().getBooleanExtra("isTesting", false);

        // DB-related Initializations
        if (this.isTesting) {
            this.db = AppDatabase.useTestSingleton(this);
        }
        else {
            this.db = AppDatabase.singleton(this);
        }

        this.f2 = this.backgroundThreadExecutor.submit(() -> this.db.profileDao().getUserProfile(true));

        try {
            this.selfProfile = this.f2.get();
        } catch (Exception e) {
            Log.d(TAG, "Unable to retrieve self profile!");
        }

        this.f3 = this.backgroundThreadExecutor.submit(() -> this.db.courseDao().getCoursesByProfileId(this.selfProfile.getProfileId()));

        try {
            this.selfCourses = this.f3.get();
        } catch (Exception e) {
            Log.d(TAG, "Unable to retrieve self courses!");
        }

        this.f4 = this.backgroundThreadExecutor.submit(() -> this.db.sessionDao().getAllSessions());

        try {
            this.allSessions = this.f4.get();
        } catch (Exception e) {
            Log.d(TAG, "Unable to retrieve all session!");
        }

        this.currentCourses = getCurrentCourses();

        // Get mocked messages
        this.mockedMessages = getIntent().getStringArrayListExtra("mocked_messages");
        if (this.mockedMessages == null) {
            this.mockedMessages = new ArrayList<>();
        }

        // Get outgoing waved messages
        this.f1 = this.backgroundThreadExecutor.submit(() -> this.db.profileDao().getWavedProfileIds(true));
        this.wavedMessages = new ArrayList<>();
        try {
            List<String> wavedProfileIds = this.f1.get();
            for (String profileId : wavedProfileIds) {
                Message wavedMessage = new Message(encodeWaveMessage(profileId).getBytes(StandardCharsets.UTF_8));
                Nearby.getMessagesClient(this).publish(wavedMessage);
                this.wavedMessages.add(wavedMessage);
            }
        } catch (Exception e) {
            Log.d(TAG, "Unable to retrieve waved profile ids!");
        }

        // Get current session
        this.sessionId = getIntent().getStringExtra("session_id");
        if (this.sessionId == null) {
            Log.d(TAG, "No sessionId intent extra passed in, opening last saved session!");
            Future<Session> future = this.backgroundThreadExecutor.submit(() -> this.db.sessionDao().getLastSession(true));

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
            this.f5 = this.backgroundThreadExecutor.submit(() -> {
                this.db.sessionDao().insert(this.session);
                return null;
            });
            this.isNewSession = true;
        }
        else {
            Log.d(TAG, "Resuming previous session!");
            Future<Session> future = this.backgroundThreadExecutor.submit(() -> this.db.sessionDao().getSession(this.sessionId));

            try {
                this.session = future.get();
            } catch (Exception e) {
                Log.d(TAG, "Unable to retrieve session!");
            }
        }

        setLastSession();

        // Resume last sort/filter option
        switch(this.session.getSortFilter()) {
            case "Favorites Only":
                this.mutator = new FavoritesFilter(this);
                break;
            case "Prioritize Recent":
                this.mutator = new RecencySorter(this);
                break;
            case "Prioritize Small Classes":
                this.mutator = new SizeSorter(this);
                break;
            case "This Quarter Only":
                this.mutator = new CurrentQuarterFilter(this);
                break;
            default:
                this.mutator = new QuantitySorter(this);
                break;
        }

        // View initializations
        this.matchesRecyclerView = findViewById(R.id.matches_view);
        this.stopButton = findViewById(R.id.stop_button);
        this.startButton = findViewById(R.id.start_button);
        this.sessionLabel = findViewById(R.id.session_name_view);
        this.sessionLabel.setText(this.session.getName());

        this.sortFilterSpinner = findViewById(R.id.sort_filter_spinner);
        List<String> mutations = new ArrayList<>(Arrays.asList("No Sort/Filter", "Favorites Only", "Prioritize Recent", "Prioritize Small Classes", "This Quarter Only"));
        ArrayAdapter<String> sort_filter_adapter = new ArrayAdapter<>(this, R.layout.sort_filter_spinner_item_text, mutations);
        sort_filter_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.sortFilterSpinner.setAdapter(sort_filter_adapter);

        // Setting spinner to correct option
        for (int i = 0; i < this.sortFilterSpinner.getCount(); i++) {
            if (this.sortFilterSpinner.getItemAtPosition(i).equals(this.session.getSortFilter())) {
                this.sortFilterSpinner.setSelection(i);
            }
        }

        // Setting a listener for spinner
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
                        mutator = new QuantitySorter(context);
                        break;
                    case "Prioritize Recent":
                        mutator = new RecencySorter(context);
                        break;
                    case "Prioritize Small Classes":
                        mutator = new SizeSorter(context);
                        break;
                    case "This Quarter Only":
                        mutator = new CurrentQuarterFilter(context);
                        break;
                    case "Favorites Only":
                        mutator = new FavoritesFilter(context);
                        break;
                }

                session.setSortFilter(sortFilterSpinner.getSelectedItem().toString());
                f5 = backgroundThreadExecutor.submit(() -> {
                    db.sessionDao().update(session);
                    return null;
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

        // Utilities initializations
        this.messageListener = new BoFMessageListener(this.sessionId, this);
        this.selfMessage = null;

        // Initializing and registering NearbyViewMediator, then updating the matches list to retrieve prior matches
        this.nvm = new NearbyViewMediator(this, this.mutator, this.matchesRecyclerView, this.sessionId);
        this.messageListener.register(this.nvm);
        this.nvm.updateMatchesList();
    }

    /**
     * Closes/destroyed the current activity.
     *
     * @param
     * @return none
     */
    @Override
    protected void onDestroy() {
        for (Message wavedMessage : this.wavedMessages) {
            Nearby.getMessagesClient(this).unpublish(wavedMessage);
        }
        this.promptDialog.cancel();
        unsetLastSession();
        super.onDestroy();
        Log.d(TAG, "MatchActivity destroyed!");
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
        this.selfMessage = new Message(encodeInformationMessage().getBytes(StandardCharsets.UTF_8));
        Nearby.getMessagesClient(this).publish(this.selfMessage);
        Log.d(TAG, "Published a message: " + new String(this.selfMessage.getContent()));
        Nearby.getMessagesClient(this).subscribe(this.messageListener);
        Log.d(TAG, "BoFMessageListener subscribed!");

        // Discover mocked messages
        for (String msg : this.mockedMessages) {
            this.messageListener.onFound(new Message(msg.getBytes(StandardCharsets.UTF_8)));
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
        Log.d(TAG, "Unpublished a message: " + new String(this.selfMessage.getContent()));
        Nearby.getMessagesClient(this).unsubscribe(this.messageListener);
        Log.d(TAG, "BoFMessageListener unsubscribed!");
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
    public String encodeInformationMessage() {
        // Look at BDD Scenario for CSV format
        // Were are encoding our own profile
        StringBuilder encodedMessage = new StringBuilder();
        String selfUUID = this.selfProfile.getProfileId();
        String selfName = this.selfProfile.getName();
        String selfPhoto = this.selfProfile.getPhoto();
//        String encoded = "";
//        encoded += selfUUID + ",,,,\n" +
//                   selfName + ",,,,\n" +
//                   selfPhoto + ",,,,\n";
//        for (Course course : this.selfCourses) {
//            encoded += course.getYear() + "," +
//                       encodeQuarter(course.getQuarter()) + "," +
//                       course.getSubject() + "," +
//                       course.getNumber() + "," +
//                       course.getClassSize() + "\n";
//        }
//
//        return encoded;

        encodedMessage.append(selfUUID).append(",,,,\n");
        encodedMessage.append(selfName).append(",,,,\n");
        encodedMessage.append(selfPhoto).append(",,,,\n");
        for (Course course : this.selfCourses) {
            encodedMessage.append(course.getYear()).append(",");
            encodedMessage.append(encodeQuarter(course.getQuarter())).append(",");
            encodedMessage.append(course.getSubject()).append(",");
            encodedMessage.append(course.getNumber()).append(",");
            encodedMessage.append(course.getClassSize()).append("\n");
        }

        return encodedMessage.toString();
    }

    public String encodeWaveMessage(String profileId) {
        return encodeInformationMessage() + profileId + ",wave,,,\n";
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
        intent.putExtra("self_profile_id", selfProfile.getProfileId());
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

            startSearchForMatches();

        }
        else {
//            unsetLastSession();

            Intent intent = new Intent(this, MatchActivity.class);
            intent.putExtra("session_id", selectedSessionId);
            startActivity(intent);
            finish();
        }

    }

    /**
     * Allows the user to be able to create a new session when option is clicked.
     *
     * @param view The given view
     * @return none
     */
    public void onStartPopupCreateNewSessionClicked(View view) {
//        unsetLastSession();

        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra("session_id", "");
        startActivity(intent);
        finish();
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
        Log.d(TAG, "Unsetting this session as the last session.");
        backgroundThreadExecutor.submit(() -> {
            this.session.setIsLastSession(false);
            this.db.sessionDao().update(this.session);
            clearWaves();
            clearWaved();
        });
    }

    private void setLastSession() {
        Log.d(TAG, "Setting this session as the last session.");
        backgroundThreadExecutor.submit(() -> {
            this.session.setIsLastSession(true);
            this.db.sessionDao().update(this.session);
        });
    }

    private void changeSessionName(String newName) {
        Log.d(TAG, "Changing this session name to " + newName + ".") ;

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
        Log.d(TAG, "Clearing all waving profiles.");
        List<Profile> wavingProfiles = this.db.profileDao().getWavingProfiles(true);
        for (Profile profile : wavingProfiles) {
            profile.setIsWaving(false);
            this.db.profileDao().update(profile);
        }
    }

    private void clearWaved() {
        Log.d(TAG, "Clearing all waved profiles.");
        List<Profile> wavedProfiles = this.db.profileDao().getWavedProfiles(true);
        for (Profile profile : wavedProfiles) {
            System.out.println(profile.getName() + " " + profile.getIsWaved());
            profile.setIsWaved(false);
            System.out.println(profile.getName() + " " + profile.getIsWaved());
            this.db.profileDao().update(profile);
        }
    }

    public void onSessionLabelClicked(View view) {
        showSelectOrEnterSessionNameStopPopup();
    }

    // For testing
    public void mockSearch() {
        this.nvm.updateMatchesList();
    }
}



