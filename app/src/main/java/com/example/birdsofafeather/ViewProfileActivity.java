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
import com.example.birdsofafeather.db.Profile;
import com.example.birdsofafeather.db.ProfileDao;

import java.util.ArrayList;
import java.util.List;

public class ViewProfileActivity extends AppCompatActivity {
    protected RecyclerView sharedCoursesRecyclerView;
    protected RecyclerView.LayoutManager sharedCoursesLayoutManager;
    protected  ViewProfileAdapter viewProfileAdapter;

    private DatabaseTracker databaseTracker;
    private TextView nameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        /**comment out for now, until full implementation from HomeScreen*/
        //Intent intent = getIntent();
        //int profileId = intent.getIntExtra("profileId", 1);

        int profileId = 3;

        AppDatabase db = AppDatabase.singleton(this);
        CourseDao courseDao = db.courseDao();
        ProfileDao profileDao = db.profileDao();

        Profile testProfile = new Profile(3,"Drake", "https://www.google.com");
        profileDao.insert(testProfile);

        //set name
        nameTextView = findViewById(R.id.profile_name_textview);
        nameTextView.setText(profileDao.getProfile(profileId).getName());

        //List<Course> sharedCourses = databaseTracker.getSharedCourses(profileId);
        Course testCourse1 = new Course(3,"2016", "Fall","CSE","11");
        Course testCourse2 = new Course(3,"2017", "Spring","CSE","100");
        Course testCourse3 = new Course(3,"2017", "Spring","MATH","20");
        Course testCourse4 = new Course(3,"2018", "Fall","CSE","110");
        Course testCourse5 = new Course(3,"2018", "Winter","CSE","120");
        List<Course> sharedCourses = new ArrayList<Course>();
        
        sharedCourses.add(testCourse1);
        sharedCourses.add(testCourse2);
        sharedCourses.add(testCourse3);
        sharedCourses.add(testCourse4);
        sharedCourses.add(testCourse5);

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