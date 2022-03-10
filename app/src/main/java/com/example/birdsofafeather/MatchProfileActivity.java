package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Refers to the screen where the user can see a match's enlarged photo, name, and list of shared courses
public class MatchProfileActivity extends AppCompatActivity {
    private final String TAG = "<Profile>";

    private AppDatabase db;

    private String matchId;
    private Profile match;
    private List<Course> sharedCourses;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Profile> f1;
    private Future<List<Course>> f2;
    private Profile selfProfile;
    private List<Course> selfCourses;

    private TextView nameTextView;
    private ImageView photoImageView;
    private RecyclerView sharedCoursesRecyclerView;
    private RecyclerView.LayoutManager sharedCoursesLayoutManager;
    private ProfileViewAdapter viewProfileAdapter;

    private Message wave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d(TAG, "Setting up Match Profile Screen");

        // DB-related initializations
        this.db = AppDatabase.singleton(this);
        this.matchId = getIntent().getStringExtra("match_id");

        if (matchId != null) {
            // Get the match's profile from DB
            Log.d(TAG, "Retrieving match profile from DB...");
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
            Log.d(TAG, "Error, match id is null!");
        }

        // Get the name and photo Views and set them
        this.nameTextView = findViewById(R.id.viewprofile_name);
        this.photoImageView = findViewById(R.id.viewprofile_photo);

        this.nameTextView.setText(this.match.getName());
        Profile finalMatch = this.match;
        Glide.with(this).load(finalMatch.getPhoto()).into(this.photoImageView);

        // Get shared courses between user and match
        this.f2 = this.backgroundThreadExecutor.submit(() -> {
            Profile user = this.db.profileDao().getUserProfile(true);
            List<Course> userCourses = this.db.courseDao().getCoursesByProfileId(user.getProfileId());
            List<Course> matchCourses = this.db.courseDao().getCoursesByProfileId(this.matchId);

            return Utilities.getSharedCourses(userCourses, matchCourses);
        });

        // Retrieve shared courses between user and match from Future
        this.sharedCourses = null;
        try {
            Log.d(TAG, "Retrieved shared courses");
            this.sharedCourses = this.f2.get();
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving shared courses");
            e.printStackTrace();
        }

        Log.d(TAG, "Shared courses retrieved successfully!");

        // Set the shared courses using a view adapter and recycler view
        this.sharedCoursesRecyclerView = findViewById(R.id.viewprofile_shared_courses);
        this.viewProfileAdapter = new ProfileViewAdapter(this.sharedCourses);
        this.sharedCoursesLayoutManager = new LinearLayoutManager(this);

        this.sharedCoursesRecyclerView.setAdapter(this.viewProfileAdapter);
        this.sharedCoursesRecyclerView.setLayoutManager(this.sharedCoursesLayoutManager);

        this.backgroundThreadExecutor.submit(() -> {
            this.selfProfile = this.db.profileDao().getUserProfile(true);
            this.selfCourses = this.db.courseDao().getCoursesByProfileId(this.selfProfile.getProfileId());
        });

        this.wave = null;
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

    public void onClickSendWave(View view) {
        String selfInformation = encodeSelfInformation();
        selfInformation += this.matchId + ",wave,,,";
        this.wave = new Message(selfInformation.getBytes());
        Nearby.getMessagesClient(this).publish(this.wave);
        Toast.makeText(this, "Wave sent!", Toast.LENGTH_SHORT).show();
    }

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
                Log.d("<MatchProfileActivity>", "Quarter cannot be encoded");
                return null;
        }
    }
}