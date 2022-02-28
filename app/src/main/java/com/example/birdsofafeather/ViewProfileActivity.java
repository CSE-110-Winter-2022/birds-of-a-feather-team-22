package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.DiscoveredUser;
import com.example.birdsofafeather.db.Profile;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Refers to the screen where the user can see a match's enlarged photo, name, and list of shared courses
public class ViewProfileActivity extends AppCompatActivity {
    private AppDatabase db;
    private String profileId;
    private Profile match;
    private List<Course> sharedCourses;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Profile> f1;
    private Future<List<Course>> f2;

    private TextView nameTextView;
    private ImageView photoImageView;
    private RecyclerView sharedCoursesRecyclerView;
    private RecyclerView.LayoutManager sharedCoursesLayoutManager;
    private ViewProfileAdapter viewProfileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Log.d("<Match>", "Setting up Match Screen");

        // DB-related initializations
        this.db = AppDatabase.singleton(this);
        this.profileId = getIntent().getStringExtra("profileId");

        Log.d("<Match>", "Retrieving match profile from DB...");
        // Get the match's profile from DB
        this.f1 = this.backgroundThreadExecutor.submit(() -> this.db.profileDao().getProfile(this.profileId));

        // Retrieve the match's profile from Future
        this.match = null;
        try {
            Log.d("<Match>", "Match profile retrieved from DB");
            this.match = this.f1.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e("<Match>", "Error retrieving match profile");
            e.printStackTrace();
        }

        // Get the name and photo Views and set them
        this.nameTextView = findViewById(R.id.viewprofile_name);
        this.photoImageView = findViewById(R.id.viewprofile_photo);

        this.nameTextView.setText(match.getName());
        Profile finalMatch = this.match;
        Glide.with(this).load(finalMatch.getPhoto()).into(this.photoImageView);

        Log.d("<Match>", "Retrieving shared courses...");
        // Get shared courses between user and match
        this.f2 = this.backgroundThreadExecutor.submit(() -> {
            Profile user = this.db.profileDao().getUserProfile(true);
            List<Course> userCourses = this.db.courseDao().getCoursesByProfileId(user.getProfileId());
            List<Course> matchCourses = this.db.courseDao().getCoursesByProfileId(this.profileId);

            return Utilities.getSharedCourses(userCourses, matchCourses);
        });

        // Retrieve shared courses between user and match from Future
        this.sharedCourses = null;
        try {
            Log.d("<Match>", "Retrieved shared courses");
            this.sharedCourses = this.f2.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e("<Match>", "Error retrieving shared courses");
            e.printStackTrace();
        }

        // Set the shared courses using a view adapter and recycler view
        this.sharedCoursesRecyclerView = findViewById(R.id.viewprofile_shared_courses);
        this.viewProfileAdapter = new ViewProfileAdapter(this.sharedCourses);
        this.sharedCoursesLayoutManager = new LinearLayoutManager(this);

        this.sharedCoursesRecyclerView.setAdapter(this.viewProfileAdapter);
        this.sharedCoursesRecyclerView.setLayoutManager(this.sharedCoursesLayoutManager);
    }

//    // For testing use
//    public void setProfileId(boolean testing, int profileId){
//        if(!testing) {
//            Intent intent = getIntent();
//            this.profileId = intent.getIntExtra("profileId",1);
//        } else {
//            this.profileId = profileId;
//        }
//    }

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