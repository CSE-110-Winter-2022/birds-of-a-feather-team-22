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
import com.example.birdsofafeather.factory.EnterNameWithStopPromptFactory;
import com.example.birdsofafeather.factory.PromptFactory;
import com.example.birdsofafeather.factory.EnterNamePromptFactory;
import com.example.birdsofafeather.factory.SessionsPromptFactory;
import com.example.birdsofafeather.factory.StopPromptFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // currently open prompt
    private AlertDialog promptDialog;

    //prompt factory
    private PromptFactory factory;

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

        // TODO: choose/resume session
        //if previous sessions exist: session list pop-up occurs
        //createSessionListPrompt();
        /**refactor**/
        this.factory = new SessionsPromptFactory();
        this.promptDialog = factory.createPrompt(this, this.promptDialog, null);
        this.promptDialog.show();


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


    // When the stop button is clicked
    public void onClickStop(View view) {
        Log.d("<Home>", "Stop button pressed, stopping search for matches...");

        this.startButton.setVisibility(View.VISIBLE);
        this.stopButton.setVisibility(View.GONE);

        // TODO: Prompt user to save current session under a current course or other course name
        // String sessionName = getUserSavedName();
        // TODO: Update session name and add session to DB
        // this.session.setName(sessionName);

        List<String> currentQuarter = Utilities.getCurrentQuarter();

        //get profile of current user
        Profile user = this.db.profileDao().getUserProfile(true);
        List<Course> sessionCoursesList =
                this.db.courseDao().getCoursesByProfileId(user.getProfileId());
        List<Course> currentCoursesList = new ArrayList<Course>();

        for(Course c : sessionCoursesList){
            if(c.getQuarter().equals(currentQuarter.get(0))
                    && c.getYear().equals(currentQuarter.get(1))){
                currentCoursesList.add(c);
            }
        }

        /**refactor*/

        if(currentCoursesList.isEmpty()){
            this.factory = new EnterNamePromptFactory();
            this.promptDialog = this.factory.createPrompt(this, this.promptDialog, null);
        }else{
            this.factory = new StopPromptFactory<Course>();
            this.promptDialog =
                    this.factory.createPrompt(this, this.promptDialog, currentCoursesList);
        }

        this.promptDialog.show();
    }

    //onClick listener for Session items within the recyclerview of the
    public void onClickSessionLabel(View view){
        Log.d("<Home>", "Previous session selected, " +
                "displaying matches in HomeScreenActivity");

        //selected session object
        TextView selectedSessionName = view.findViewById(R.id.session_name_text_view);
        TextView selectedSessionId = view.findViewById(R.id.session_id_text_view);

        //grab selected session as current
        this.session = this.db.sessionDao().getSession(selectedSessionId.getText().toString());

        String regex  = "(0?[1-9]|1[012])\\/(0?[1-9]|[12][0-9]|3[01])\\/\\d{2}";
        Pattern pattern = Pattern.compile(regex);

        //Matching the compiled pattern in the String
        String toCheck = selectedSessionName.getText().toString().substring(0,6);
        Matcher matcher = pattern.matcher(toCheck);

        //if timestamp selected, change name
        if(matcher.matches()){
            this.session =
                    new Session(selectedSessionId.getText().toString(), "", false);
            //createFirstStopPrompt(false);
            this.factory = new EnterNameWithStopPromptFactory();
            this.promptDialog = this.factory.createPrompt(this, this.promptDialog, null);
            this.promptDialog.show();
            return;
        }

        //get list of discovered users from selected session
        List<DiscoveredUser> sessionDiscoveredUsers = this.db.discoveredUserDao()
                .getDiscoveredUsersFromSession(selectedSessionId.getText().toString());

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
        this.matchesViewAdapter = new MatchesViewAdapter(this.matches,this);
        this.matchesLayoutManager = new LinearLayoutManager(this);
        this.matchesRecyclerView.setAdapter(this.matchesViewAdapter);
        this.matchesRecyclerView.setLayoutManager(this.matchesLayoutManager);

        displaySessionTitle(selectedSessionName.getText().toString());

    }



    //dialog prompt listener
    public void onClickCourseLabel(View view){
        Log.d("<Home>", "Course selected from second stop prompt's course items," +
                "item becomes selected");

        //find selected item from recyclerview, grab views for course info
        TextView sessionCourseNameTextView = view.findViewById(R.id.session_course_name_text_view);
        TextView sessionCourseNumberTextView = view.findViewById(R.id.session_course_number_text_view);
        TextView setSessionTextView = this.promptDialog.findViewById(R.id.set_course);

        //adjust view elements of UI accordingly
        this.promptDialog.findViewById(R.id.enter_session_button).setVisibility(View.GONE); //hide enter
        this.promptDialog.findViewById(R.id.or).setVisibility(View.GONE); //hide or
        this.promptDialog.findViewById(R.id.submit_session_button).setVisibility(View.VISIBLE); // show save

        setSessionTextView.setText(""+sessionCourseNameTextView.getText() +
                sessionCourseNumberTextView.getText());

    }

    //dialog prompt listener for save button on first stop prompt
    public void onClickSaveSession(View view) {
        Log.d("<Home>", "Save Session Name button pressed on first stop prompt," +
                " saving session with name given...");

        TextView enteredCourseName = this.promptDialog.findViewById(R.id.enterSessionNameEditText);

        //update database
        this.db.sessionDao().delete(this.session);
        this.session.setName(enteredCourseName.getText().toString());
        this.db.sessionDao().insert(this.session);

        //close up prompt
        this.promptDialog.cancel();
        this.promptDialog = null;

        displaySessionTitle();
    }

    public void onCreateNewSession(View view){

        // Update the last session to no longer be the last session
        Session lastSession = this.db.sessionDao().getLastSession(true);

        if( lastSession != null) {
            this.db.sessionDao().delete(lastSession);
            lastSession.setIsLastSession(false);
            this.db.sessionDao().insert(lastSession);
        }

        //grab current timestamp for default session title
        DateFormat df = new SimpleDateFormat("M'/'d'/'yy h:mma");
        String timestamp = df.format(Calendar.getInstance().getTime());

        // Make new session
        String sessionId = UUID.randomUUID().toString();
        this.session = new Session(sessionId, timestamp, true);
        this.db.sessionDao().insert(this.session);

        //close up prompt
        this.promptDialog.cancel();
        this.promptDialog = null;

        displaySessionTitle();
    }

    //dialog prompt listener for save button on second stop prompt
    public void onClickSubmitSession(View view){
        Log.d("<Home>", "Save Session Name Button pressed on second stop prompt," +
                " saving session with name selected...");

        TextView selectedCourseName = this.promptDialog.findViewById(R.id.set_course); //grab name

        //update database
        this.db.sessionDao().delete(this.session);
        this.session.setName(selectedCourseName.getText().toString());
        this.db.sessionDao().insert(this.session);

        //close up prompt
        this.promptDialog.cancel();
        this.promptDialog = null;

        displaySessionTitle();
    }

    //dialog prompt listener
    public void onClickEnterSession(View view) {
        Log.d("<Home>", "Enter Session Name Button pressed," +
                " creating first stop prompt to enter name");

        //createFirstStopPrompt(false); //deployed from second prompt
        /**refactor*/
        this.factory = new EnterNameWithStopPromptFactory();
        this.promptDialog = factory.createPrompt(this, this.promptDialog, null);
        this.promptDialog.show();
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

    //helper function for session title
    private void displaySessionTitle(){
        //display session title
        ((TextView)(findViewById(R.id.change_session_name_text_view)))
                .setText(this.session.getName());
        ((TextView)(findViewById(R.id.change_session_name_text_view)))
                .setVisibility(View.VISIBLE);
    }

    private void displaySessionTitle(String sessionName) {
        //display session title
        ((TextView)(findViewById(R.id.change_session_name_text_view)))
                .setText(sessionName);
        ((TextView)(findViewById(R.id.change_session_name_text_view)))
                .setVisibility(View.VISIBLE);
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