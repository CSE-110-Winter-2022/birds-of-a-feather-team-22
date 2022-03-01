package com.example.birdsofafeather;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Session;
import com.example.birdsofafeather.HomeScreenActivity;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.ViewHolder>{
    private final List<Course> sessionCourses;

    public SessionsAdapter(List<Course> sessionCourses){
        super();
        this.sessionCourses = sessionCourses;

    }

    @NonNull
    @Override
    public SessionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.session_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionsAdapter.ViewHolder holder, int position){
        holder.setSession(sessionCourses.get(position));
    }

    @Override
    //return number of profiles in course
    public int getItemCount(){
        return this.sessionCourses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView sessionCourseNameTextView;
            private final TextView sessionCourseNumberTextView;

        ViewHolder(View itemView){
            super(itemView);
            this.sessionCourseNameTextView = itemView.findViewById(R.id.session_course_name_text_view);
            this.sessionCourseNumberTextView = itemView.findViewById(R.id.session_course_number_text_view);

        }
        public void setSession(Course sessionCourse){
            this.sessionCourseNameTextView.setText(sessionCourse.getSubject());
            this.sessionCourseNumberTextView.setText(sessionCourse.getNumber());

        }
    }


}

