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

        Intent intent = getIntent();
        int profileId = intent.getIntExtra("profileId", 1);

        //set name
        nameTextView = findViewById(R.id.profile_name_textview);
        nameTextView.setText(databaseTracker.getUserProfile(profileId).getName());

        List<Course> sharedCourses = databaseTracker.getSharedCourses(profileId);

        sharedCoursesRecyclerView = findViewById(R.id.shared_courses_view);

        sharedCoursesLayoutManager = new LinearLayoutManager(this);
        sharedCoursesRecyclerView.setLayoutManager(sharedCoursesLayoutManager);

        viewProfileAdapter = new ViewProfileAdapter(sharedCourses, profileId);
        sharedCoursesRecyclerView.setAdapter(viewProfileAdapter);
    }
}