package com.example.birdsofafeather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.birdsofafeather.db.Course;


public class ViewProfileAdapter extends RecyclerView.Adapter<ViewProfileAdapter.ViewHolder>{
        private final List<Course> sharedCourses;
        private final int profileId;

        //constructor
        public ViewProfileAdapter(List<Course> sharedCourses, int profileId){
            super();
            this.sharedCourses = sharedCourses;
            this.profileId = profileId;

        }


        @NonNull
        @Override
        public ViewProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.shared_class_row, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewProfileAdapter.ViewHolder holder, int position){
            holder.setCourse(sharedCourses.get(position));
        }

        @Override
        public int getItemCount(){
            return this.sharedCourses.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView courseSubjectTextView;
            private final TextView courseIdTextView;
            private final TextView courseYearTextView;
            private final TextView courseQuarterTextView;
            private Course course;

            ViewHolder(View itemView){
                super(itemView);
                this.courseSubjectTextView = itemView.findViewById(R.id.course_subject_row_textview);
                this.courseIdTextView = itemView.findViewById(R.id.course_number_row_textview);
                this.courseYearTextView = itemView.findViewById(R.id.course_year_row_textview);
                this.courseQuarterTextView = itemView.findViewById(R.id.course_quarter_row_textview);
            }

            public void setCourse(Course course){
                this.course = course;
                this.courseSubjectTextView.setText(course.getSubject());
                this.courseIdTextView.setText(course.getNumber());
                this.courseQuarterTextView.setText(course.getQuarter());
                this.courseYearTextView.setText(course.getYear());
            }

        }
}
