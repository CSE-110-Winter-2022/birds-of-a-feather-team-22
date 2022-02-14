package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Refers to the screen where the user can see a match's enlarged photo, name, and list of shared courses
public class ViewProfileActivity extends AppCompatActivity {
    protected RecyclerView sharedCoursesRecyclerView;
    protected RecyclerView.LayoutManager sharedCoursesLayoutManager;
    protected ViewProfileAdapter viewProfileAdapter;

    private AppDatabase db;
    private TextView nameTextView;
    private int profileId;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Profile> f1;
    private Future<List<Course>> f2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        this.db = AppDatabase.singleton(this);

        // Get match's profile id from intent
        this.profileId = getIntent().getIntExtra("profileId",1);

        // Get the match's profile from DB
        f1 = backgroundThreadExecutor.submit(() -> {
            return db.profileDao().getProfile(this.profileId);
        });

        // Retrieve the match's profile from Future
        Profile match = null;
        try {
            match = f1.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // Get the name and photo Views and set them
        nameTextView = findViewById(R.id.viewprofile_name);
        nameTextView.setText(match.getName());
        ImageView viewProfilePhoto = findViewById(R.id.viewprofile_photo);
        Profile finalMatch = match;
        Glide.with(this).load(finalMatch.getPhoto()).into(viewProfilePhoto);

        // Get shared courses between user and match
        f2 = backgroundThreadExecutor.submit(() -> {
            List<Course> myCourses = db.courseDao().getCoursesByProfileId(1);
            List<Course> theirCourses = db.courseDao().getCoursesByProfileId(this.profileId);

            return Utilities.getSharedCourses(myCourses, theirCourses);
        });

        // Retrieve shared courses between user and match from Future
        List<Course> sharedCourses = null;
        try {
            sharedCourses = f2.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // Set the shared courses using a view adapter and recycler view
        sharedCoursesRecyclerView = findViewById(R.id.viewprofile_shared_courses);
        sharedCoursesLayoutManager = new LinearLayoutManager(this);
        sharedCoursesRecyclerView.setLayoutManager(sharedCoursesLayoutManager);
        viewProfileAdapter = new ViewProfileAdapter(sharedCourses);
        sharedCoursesRecyclerView.setAdapter(viewProfileAdapter);
    }

    // For testing use
    public void setProfileId(boolean testing, int profileId){
        if(!testing) {
            Intent intent = getIntent();
            this.profileId = intent.getIntExtra("profileId",1);
        } else {
            this.profileId = profileId;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (f1 != null) {
            f1.cancel(true);
        }
        if (f2 != null) {
            f2.cancel(true);
        }
    }
}