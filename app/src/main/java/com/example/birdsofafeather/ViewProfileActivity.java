package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.CourseDao;
import com.example.birdsofafeather.db.ProfileDao;

import java.util.ArrayList;
import java.util.List;

public class ViewProfileActivity extends AppCompatActivity {
    protected RecyclerView sharedCoursesRecyclerView;
    protected RecyclerView.LayoutManager sharedCoursesLayoutManager;
    protected  ViewProfileAdapter viewProfileAdapter;

    private DatabaseTracker databaseTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        /**comment out for now, until full implementation from HomeScreen*/
        //Intent intent = getIntent();
        //int profileId = intent.getIntExtra("profile_id", 0);

        int profileId = 3;

        AppDatabase db = AppDatabase.singleton(BoFApplication.getContext());
        CourseDao courseDao = db.courseDao();
        ProfileDao profileDao = db.profileDao();

        //set name
        TextView nameTextView = findViewById(R.id.profile_name_textview);
        nameTextView.setText(profileDao.getProfile(profileId).getName());

        //List<Course> sharedCourses = databaseTracker.getSharedCourses(1);
        Course testCourse = new Course(1,"2016", "fall","CSE","110");
        List<Course> sharedCourses = new ArrayList<Course>();
        
        sharedCourses.add(testCourse); 
        
        sharedCoursesRecyclerView = findViewById(R.id.shared_courses_view);

        sharedCoursesLayoutManager = new LinearLayoutManager(this);
        sharedCoursesRecyclerView.setLayoutManager(sharedCoursesLayoutManager);

        viewProfileAdapter = new ViewProfileAdapter(sharedCourses, profileId);
        sharedCoursesRecyclerView.setAdapter(viewProfileAdapter);
    }

    public void onClickStart(View view) {

        Button stopButton = findViewById(R.id.stopButton);
        TextView findMatches = findViewById(R.id.textView6);
        Button startButton = findViewById(R.id.startButton);

        findMatches.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);
    }

    public void onClickStop(View view) {
        Button stopButton = findViewById(R.id.stopButton);
        TextView findMatches = findViewById(R.id.textView6);
        TextView matchesFound = findViewById(R.id.matchesFound);
        LinearLayout dummy2 = findViewById(R.id.dummy2);

        stopButton.setVisibility(View.GONE);
        findMatches.setVisibility(View.GONE);
        matchesFound.setVisibility(View.VISIBLE);
        dummy2.setVisibility(View.VISIBLE);

    }
}