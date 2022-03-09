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
import com.example.birdsofafeather.factory.EnterNamePromptFactory;
import com.example.birdsofafeather.factory.EnterNameWithStopPromptFactory;
import com.example.birdsofafeather.factory.PromptFactory;
import com.example.birdsofafeather.factory.SessionsPromptFactory;
import com.example.birdsofafeather.factory.StopPromptFactory;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Refers to the screen where the user can see discovered users and search for more discovered users
public class MatchActivity extends AppCompatActivity {
    private final String TAG = "<Match>";

    // DB-related fields
    private Future<Void> f1;
    private Future<List<Course>> f2;
    private Future<List<Pair<Profile, Integer>>> f3;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private AppDatabase db;
    private List<Pair<Profile, Integer>> matches;
    private Session session;
    private String sessionId;
    private Profile selfProfile;
    private List<Course> selfCourses;
    private ArrayList<String> mockedMessages;
    private List<Session> allSessions;
    private boolean isResumedSession;
    private List<Course> currentCourses;

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

    //prompt factory
    private PromptFactory factory;

    // Utilities
    private MessageListener messageListener;
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
        this.isResumedSession = false;
        if (this.sessionId == null) {
            Log.d(TAG, "No sessionId intent extra passed in, opening last saved session!");
            this.backgroundThreadExecutor.submit(() -> {
                this.session = this.db.sessionDao().getLastSession(true);
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

        }
        else {
            Log.d(TAG, "Resuming previous session!");
            this.backgroundThreadExecutor.submit(() -> {
                this.session = this.db.sessionDao().getSession(this.sessionId);
                setLastSession();
            });
            this.isResumedSession = true;
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


    }



    private List<Profile> getCurrentMatches() {
        Future<List<Profile>> future = this.backgroundThreadExecutor.submit(() -> {
            Log.d(TAG, "Display list of already matched students");
            List<DiscoveredUser> discovered = db.discoveredUserDao().getDiscoveredUsersFromSession(sessionId);
            List<Profile> discoveredProfiles = new ArrayList<>();
            if (discovered != null) {
                for (DiscoveredUser user : discovered) {

                    Profile profile = db.profileDao().getProfile(user.getProfileId());
                    discoveredProfiles.add(profile);
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

    public void startSearchingForMatches() {
        Nearby.getMessagesClient(this).publish(this.selfMessage);
        Nearby.getMessagesClient(this).subscribe(this.messageListener);
    }

    public void stopSearchingForMatches() {
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

    @Override
    protected void onDestroy() {
        setLastSession();

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

        Log.d(TAG, "Start button pressed, searching for matches...");

        this.stopButton.setVisibility(View.VISIBLE);
        this.startButton.setVisibility(View.GONE);

        //if previous sessions exist: session list pop-up occurs
        if (!isResumedSession) {
            createStartPopup();
        }


        // Discover mocked messages
        for (String msg : this.mockedMessages) {
           this.messageListener.onFound(new Message(msg.getBytes()));
           this.mockedMessages.remove(msg);
        }

        // Discover Bluetooth messages
        startSearchingForMatches();

        // Find and sort/filter matches
        this.matches = this.mutator.mutate(getCurrentMatches());

        // Refresh recycler view
        this.matchesViewAdapter = new MatchViewAdapter(this.matches,this);
        this.matchesLayoutManager = new LinearLayoutManager(this);
        this.matchesRecyclerView.setAdapter(this.matchesViewAdapter);
        this.matchesRecyclerView.setLayoutManager(this.matchesLayoutManager);
    }


    // When the stop button is clicked
    public void onClickStop(View view) {
        Log.d(TAG, "Stop button pressed, stopping search for matches...");

        this.startButton.setVisibility(View.VISIBLE);
        this.stopButton.setVisibility(View.GONE);

        stopSearchingForMatches();

        if (!isResumedSession) {
            String currentQuarter = Utilities.getCurrentQuarter();
            String currentYear = Utilities.getCurrentYear();

            //get profile of current user
            this.currentCourses = new ArrayList<Course>();

            for(Course course : this.selfCourses){
                if(course.getQuarter().equals(currentQuarter) && course.getYear().equals(currentYear)){
                    currentCourses.add(course);
                }
            }

            //check if user has entered courses from this current quarter
            if(currentCourses.isEmpty()){
                EnterSessionNameStopPopup();
            }
            else{
                chooseSessionNameStopPopup(currentCourses);
            }
        }

        this.isResumedSession = false;
    }

    //onClick listener for Session items within the recyclerview of the
    public void onStartSelectSessionRow(View view){
            Log.d("<Home>", "Previous session selected, " +
                    "displaying matches in HomeScreenActivity");

            //selected session object strings
            String selectedSessionName =
                    ((TextView)(view.findViewById(R.id.session_name_text_view))).getText().toString();
            String selectedSessionId =
                    ((TextView)(view.findViewById(R.id.session_id_text_view))).getText().toString();


            //grab selected session as current
            this.session = this.db.sessionDao().getSession(selectedSessionId);

            String regex  = "(0?[1-9]|1[012])\\/(0?[1-9]|[12][0-9]|3[01])\\/\\d{2}";
            Pattern pattern = Pattern.compile(regex);

            //Matching the compiled pattern in the String
            String toCheck = selectedSessionName.substring(0,6);
            Matcher matcher = pattern.matcher(toCheck);

            //if timestamp selected, change name
            if(matcher.matches()){
                this.session =
                        new Session(selectedSessionId, "", false);

                View changeNameTextView = findViewById(R.id.session_name_text_view);
                changeNameTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MatchActivity.this.factory = new EnterNamePromptFactory();
                        MatchActivity.this.promptDialog =
                                MatchActivity.this.factory
                                        .createPrompt(MatchActivity.this,
                                                MatchActivity.this.promptDialog,
                                                null);

                        MatchActivity.this.promptDialog.show();
                    }
                });
            }

            //get list of discovered users from selected session
            List<DiscoveredUser> sessionDiscoveredUsers = this.db.discoveredUserDao()
                    .getDiscoveredUsersFromSession(selectedSessionId);

            //get list of the user's courses after retrieving this user's profile Id
            List<Course> myCourses = this.db.courseDao()
                    .getCoursesByProfileId
                            (this.db.profileDao().getUserProfile(true).getProfileId());

            List<Course> theirCourses = new ArrayList<Course>();

            //populate appropriate matches from selected session
            for(DiscoveredUser discoveredUser : sessionDiscoveredUsers){
                Profile foundProfile = this.db.profileDao().getProfile(discoveredUser.getProfileId());
                matches.add(new Pair(foundProfile, Utilities.getNumSharedCourses(myCourses, theirCourses)));
            }

            /**testing purposes only -- start **/
            Profile testProfile = new Profile("2", "hello", "hello");
            matches.add(new Pair(testProfile, 1));
            /**testing purposes only -- end **/

            this.promptDialog.cancel();
            this.promptDialog = null;

            //load main recyclerview with matches from selected session
            reloadMatchesRecyclerView();

            displaySessionTitle(selectedSessionName);

        //////////////

        Log.d(TAG, "Previous session selected, " +
                "displaying matches in MatchActivity");

        //selected session object
        TextView selectedSessionName = view.findViewById(R.id.match_name_view); //session_row_name_view);
        TextView selectedSessionId = view.findViewById(R.id.match_profile_id_view);//session_row_id_view);

        unsetLastSession();

        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra("session_id", selectedSessionId.getText().toString());
        startActivity(intent);
    }

    public void onStartClickCreateNewSession(View view) {
        unsetLastSession();
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra("session_id", "");
        startActivity(intent);
    }

    //helper function to create session list dialog box with previous sessions
    private void createStartPopup() {
        Log.d(TAG, "creating session list prompt AlertDialog");

        this.backgroundThreadExecutor.submit(() -> {
            this.allSessions = this.db.sessionDao().getAllSessions();
        });

        this.factory = new SessionsPromptFactory();
        this.promptDialog = factory.createPrompt(this, this.promptDialog, this.allSessions);
        this.promptDialog.show();
    }

    //dialog prompt listener
    public void onStopSelectCourseRow(View view){
        Log.d(TAG, "Course selected from second stop prompt's course items," +
                "item becomes selected");

        //find selected item from recyclerview, grab views for course info
        TextView courseNameView = view.findViewById(R.id.session_course_name_view);
        TextView courseNumberView = view.findViewById(R.id.session_course_number_view);
        TextView setSessionView = this.promptDialog.findViewById(R.id.set_course);

        //adjust view elements of UI accordingly
        this.promptDialog.findViewById(R.id.enter_session_button).setVisibility(View.GONE); //hide enter
        this.promptDialog.findViewById(R.id.or).setVisibility(View.GONE); //hide or
        this.promptDialog.findViewById(R.id.submit_session_button).setVisibility(View.VISIBLE); // show save

        setSessionView.setText(courseNameView.getText() + " " +
                courseNumberView.getText());

        this.sessionNameView.setText(courseNameView.getText() + " " + courseNumberView.getText());
        backgroundThreadExecutor.submit(() -> {
            this.db.sessionDao().delete(this.session);
            this.session.setName(this.sessionNameView.getText().toString());
            this.db.sessionDao().insert(this.session);
        });
    }

    //dialog prompt listener for save button on first stop prompt
    public void onStopClickSaveSessionName(View view) {
        Log.d(TAG, "Save Session Name button pressed on first stop prompt," +
                " saving session with name given...");

        EditText enteredCourseName = this.promptDialog.findViewById(R.id.enterSessionNameEditText);


        changeSessionName(enteredCourseName.getText().toString());

        this.promptDialog.cancel();
        this.promptDialog = null;
    }

    //dialog prompt listener for save button on second stop prompt
    public void onStopClickSubmitSessionName(View view){
        Log.d(TAG, "Save Session Name Button pressed on second stop prompt," +
                " saving session with name selected...");

        TextView selectedCourseName = this.promptDialog.findViewById(R.id.set_course); //grab name

        //update database
        this.db.sessionDao().delete(this.session);
        this.session.setName(selectedCourseName.getText().toString());
        this.db.sessionDao().insert(this.session);

        //close up prompt
        this.promptDialog.cancel();
        this.promptDialog = null;
    }

    //dialog prompt listener
    public void onStopClickEnterSessionName(View view) {
        Log.d(TAG, "Enter Session Name Button pressed," +
                " creating first stop prompt to enter name");

        //EnterSessionNameStopPopup();
        // deployed from second prompt
        this.factory = new EnterNameWithStopPromptFactory();
        this.factory.createPrompt(this, this.promptDialog, null);
        this.promptDialog.show();
    }

    // When a match in the recycler view is clicked
    public void onMatchesSelectMatchRow(View view) {
        Log.d(TAG, "Match selected, displaying match profile and course information");

        // Send the match's profile id to the activity responsible for showing the profile
        TextView profileIdView = view.findViewById(R.id.match_profile_id_view);
        String profileId = profileIdView.getText().toString();
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("profileId", profileId);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CourseActivity.class);
        intent.putExtra("session_id", this.sessionId);
        startActivity(intent);
    }

    // For testing purposes, visibility is set to gone for demoing and actual use
    public void onNearbyClicked(View view) {
        Intent intent = new Intent(this, MockingActivity.class);
        intent.putExtra("session_id", this.sessionId);
        startActivity(intent);
    }


    //helper function to create first possible stop prompt to enter a name for a created session
    //and it is also the "default" stop prompt
    public void EnterSessionNameStopPopup(){
        Log.d(TAG, "creating first stop prompt AlertDialog");

        this.factory = new EnterNamePromptFactory();
        this.promptDialog = this.factory.createPrompt(this, this.promptDialog, null);

        this.promptDialog.show();
    }

    public void chooseSessionNameStopPopup(List<Course> list){
        Log.d(TAG, "creating second stop prompt AlertDialog");

        this.factory = new SessionsPromptFactory();
        this.factory.createPrompt(this, this.promptDialog, list);

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



