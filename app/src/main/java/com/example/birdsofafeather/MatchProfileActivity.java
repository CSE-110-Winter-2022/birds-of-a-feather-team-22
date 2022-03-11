package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Refers to the screen where the user can see a match's enlarged photo, name, and list of shared courses
 */
public class MatchProfileActivity extends AppCompatActivity {
    // Log Tag
    private final String TAG = "<Profile>";

    // DB/Thread initializations
    private AppDatabase db;
    private Future<Profile> f1;
    private Future<List<Course>> f2;
    private Future<Void> f3;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    // Self and Match initializations
    private String matchId;
    private Profile match;
    private List<Course> sharedCourses;
    private Profile selfProfile;
    private List<Course> selfCourses;

    // View initializations
    private TextView nameTextView;
    private ImageView photoImageView;
    private RecyclerView sharedCoursesRecyclerView;
    private RecyclerView.LayoutManager sharedCoursesLayoutManager;
    private SharedCoursesViewAdapter viewProfileAdapter;
    private ImageView star;
    private ImageView wave;
    private ImageView sendWave;

    // Nearby and Message
    private BoFMessagesClient messagesClient;
    private Message waveMessage;

    /**
     * Initializes the activity and screen for MatchProfileActivity.
     *
     * @param savedInstanceState A bundle that contains information regarding layout and data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d(TAG, "Setting up Match Profile Screen");

        // DB-related instantiations
        this.db = AppDatabase.singleton(this);

        // Get match profile id from intent
        this.matchId = getIntent().getStringExtra("match_id");
        if (matchId != null) {
            // Get the match's profile from DB
            this.f1 = this.backgroundThreadExecutor.submit(() -> this.db.profileDao().getProfile(this.matchId));

            // Retrieve the match's profile from Future
            this.match = null;
            try {
                Log.d(TAG, "Match profile retrieved from DB");
                this.match = this.f1.get();
            } catch (Exception e) {
                Log.e(TAG, "Error retrieving match profile");
                e.printStackTrace();
            }
        }
        else {
            Log.e(TAG, "Error, match id is null!");
        }

        // Instantiate views
        this.nameTextView = findViewById(R.id.viewprofile_name);
        this.photoImageView = findViewById(R.id.viewprofile_photo);
        this.star = findViewById(R.id.profile_star);
        this.wave = findViewById(R.id.profile_wave);
        this.sendWave = findViewById(R.id.profile_send_wave);

        // Set sendWave view depending on whether the match has been waved at or not
        if (this.match.getIsWaved()) {
            this.sendWave.setImageResource(R.drawable.filled_hand);
        }
        else {
            this.sendWave.setImageResource(R.drawable.hollow_hand);
        }

        // Set star view depending on whether the match is a favorite or not
        if (match.getIsFavorite()) {
            this.star.setImageResource(R.drawable.filled_star);
        }
        else {
            this.star.setImageResource(R.drawable.hollow_star);
        }

        // Set wave view depending on whether the match is waving or not
        if (match.getIsWaving()) {
            this.wave.setVisibility(View.VISIBLE);
            Glide.with(this).load(R.drawable.waving_hand).into(this.wave);
        }
        else {
            this.wave.setVisibility(View.GONE);
        }

        // Set name and photo views
        this.nameTextView.setText(this.match.getName());
        Profile finalMatch = this.match;
        Glide.with(this)
                .load(finalMatch.getPhoto())
                .apply(RequestOptions.placeholderOf(R.drawable.feather_1)
                        .override(1000,1000).centerCrop())
                .into(this.photoImageView);

        // Get shared courses between user and match
        this.f2 = this.backgroundThreadExecutor.submit(() -> {
            Profile user = this.db.profileDao().getSelfProfile(true);
            List<Course> userCourses = this.db.courseDao().getCoursesByProfileId(user.getProfileId());
            List<Course> matchCourses = this.db.courseDao().getCoursesByProfileId(this.matchId);

            return Utilities.getSharedCourses(userCourses, matchCourses);
        });

        // Retrieve shared courses between user and match from Future
        this.sharedCourses = null;
        try {
            this.sharedCourses = this.f2.get();
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving shared courses");
            e.printStackTrace();
        }

        Log.d(TAG, "Shared courses retrieved successfully!");

        // Set the shared courses using a view adapter and recycler view
        this.sharedCoursesRecyclerView = findViewById(R.id.viewprofile_shared_courses);
        this.viewProfileAdapter = new SharedCoursesViewAdapter(this.sharedCourses);
        this.sharedCoursesLayoutManager = new LinearLayoutManager(this);

        this.sharedCoursesRecyclerView.setAdapter(this.viewProfileAdapter);
        this.sharedCoursesRecyclerView.setLayoutManager(this.sharedCoursesLayoutManager);

        this.f1 = this.backgroundThreadExecutor.submit(() -> this.db.profileDao().getSelfProfile(true));

        try {
            this.selfProfile = this.f1.get();
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving self profile!");
            e.printStackTrace();
        }

        this.f2 = this.backgroundThreadExecutor.submit(() -> this.db.courseDao().getCoursesByProfileId(this.selfProfile.getProfileId()));

        try {
            this.selfCourses = this.f2.get();
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving self courses!");
            e.printStackTrace();
        }
        this.messagesClient = new BoFMessagesClient(Nearby.getMessagesClient(this));
        this.waveMessage = null;
    }

    /**
     * Destroys MatchProfileActivity by unpublishing the wave Message and cancelling futures.
     */
    @Override
    protected void onDestroy() {
        this.messagesClient.unpublish(waveMessage);

        if (this.f1 != null) {
            this.f1.cancel(true);
        }
        if (this.f2 != null) {
            this.f2.cancel(true);
        }
        if (this.f3 != null) {
            this.f3.cancel(true);
        }
        super.onDestroy();
        Log.d(TAG, "MatchProfileActivity destroyed!");
    }

    /**
     * On click method for when the user clicks on the favorite star.
     *
     * @param view The favorite star.
     */
    public void onFavoriteStarClicked(View view) {
        // Match already a favorite, need to unfavorite
        if (this.match.getIsFavorite()) {
            this.match.setIsFavorite(false);

            // Update UI to reflect that the match is no longer a favorite
            Toast.makeText(this, "Unsaved from Favorites!", Toast.LENGTH_SHORT).show();
            this.star.setImageResource(R.drawable.hollow_star);
            Log.d(TAG, "Making selected match not a favorite.");
        }
        // Match not already a favorite, need to favorite
        else {
            this.match.setIsFavorite(true);

            // Update UI to reflect that the match is no longer a favorite
            Toast.makeText(this, "Saved to Favorites!", Toast.LENGTH_SHORT).show();
            this.star.setImageResource(R.drawable.filled_star);
            Log.d(TAG, "Making selected match a favorite.");
        }

        // Update DB to reflect change in favorite status for match
        backgroundThreadExecutor.submit(() -> {
            this.db.profileDao().update(this.match);
            Log.d(TAG, "Updated match favorite status in DB.");
        });
    }

    /**
     * On click method when the user clicks on the send wave button.
     *
     * @param view The send wave button.
     */
    public void onSendWaveClicked(View view) {
        String selfInformation = encodeSelfInformation();
        selfInformation += this.matchId + ",wave,,,";

        // Publish wave message
        this.waveMessage = new Message(selfInformation.getBytes(StandardCharsets.UTF_8));
        this.messagesClient.publish(this.waveMessage);
        Toast.makeText(this, "Wave sent!", Toast.LENGTH_SHORT).show();

        // Update waved status
        this.f3 = backgroundThreadExecutor.submit(() -> {
            this.db.profileDao().update(this.match);

            return null;
        });

        // Change send wave button to be filled
        this.sendWave.setImageResource(R.drawable.filled_hand);
        this.match.setIsWaved(true);

        Log.d(TAG, "Wave sent: " + new String(this.waveMessage.getContent()));
    }

    /**
     * Overrides the back button to go back to MatchActivity.
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button pressed, going back to MatchActivity!");
        Intent intent = new Intent(this, MatchActivity.class);
        startActivity(intent);
    }

    /**
     * Encodes the information of the self user into a CSV format.
     *
     * @return The CSV String of the user's information.
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
     * Encodes the full quarter name to an abbreviation.
     *
     * @param quarter A given quarter.
     * @return The abbreviation of the full quarter name.
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
                Log.e("<MatchProfileActivity>", "Quarter cannot be encoded");
                return null;
        }
    }
}