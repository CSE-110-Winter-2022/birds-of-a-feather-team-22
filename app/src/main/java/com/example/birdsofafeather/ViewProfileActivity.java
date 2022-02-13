package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.birdsofafeather.db.Course;

import java.util.ArrayList;
import java.util.List;

public class ViewProfileActivity extends AppCompatActivity {
    protected RecyclerView sharedCoursesRecyclerView;
    protected RecyclerView.LayoutManager sharedCoursesLayoutManager;
    protected  ViewProfileAdapter viewProfileAdapter;

    private DatabaseTracker databaseTracker;
    private TextView nameTextView;
    private int profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        //set profileId
        setProfileId(false, 0);

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

    /**testing boolean parameter for unit tests*/
    public void setProfileId(boolean testing, int profileId){
        if(!testing) {
            Intent intent = getIntent();
            this.profileId = intent.getIntExtra("profileId",1);
        } else {
            this.profileId = profileId;
        }
    }
}