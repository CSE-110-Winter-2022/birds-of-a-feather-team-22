package com.example.birdsofafeather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.birdsofafeather.db.Course;

public class SessionCoursesAdapter extends RecyclerView.Adapter<SessionCoursesAdapter.ViewHolder>{
    private final List<Course> sessionCourses;

    public SessionCoursesAdapter(List<Course> sessionCourses){
        super();
        this.sessionCourses = sessionCourses;

    }

    @NonNull
    @Override
    public SessionCoursesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.course_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionCoursesAdapter.ViewHolder holder, int position){
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
            this.sessionCourseNameTextView = itemView.findViewById(R.id.session_course_name_view);
            this.sessionCourseNumberTextView = itemView.findViewById(R.id.session_course_number_view);

        }
        public void setSession(Course sessionCourse){
            this.sessionCourseNameTextView.setText(sessionCourse.getSubject());
            this.sessionCourseNumberTextView.setText(sessionCourse.getNumber());

        }
    }


}


