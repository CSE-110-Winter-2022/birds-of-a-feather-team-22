/*
 * This file is capable of enabling a list of shared courses between the self user and another
 * user to be displayed.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.birdsofafeather.db.Course;

/**
 * View adapter for each shared course in the MatchProfileActivity Recycler View.
 */
public class SharedCoursesViewAdapter extends RecyclerView.Adapter<SharedCoursesViewAdapter.ViewHolder>{
    // Shared courses
    private final List<Course> sharedCourses;

    /**
     * Default constructor for SharedCoursesViewAdapter
     *
     * @param sharedCourses A list of shared courses
     */
    public SharedCoursesViewAdapter(List<Course> sharedCourses){
        super();
        this.sharedCourses = sharedCourses;
    }

    /**
     * On click method for when the view holder is created
     *
     * @param parent The parent ViewGroup
     * @param viewType The view type
     * @return A SharedCoursesViewAdapter.ViewHolder
     */
    @NonNull
    @Override
    public SharedCoursesViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.shared_course_row, parent, false);

        return new ViewHolder(view);
    }

    /**
     * On click method for when the ViewHolder is bound
     *
     * @param holder The given ViewHolder
     * @param position The position to bind
     */
    @Override
    public void onBindViewHolder(@NonNull SharedCoursesViewAdapter.ViewHolder holder, int position){
        holder.setCourse(this.sharedCourses.get(position));
    }

    /**
     * Gets the number of course items
     *
     * @return The number of shared courses
     */
    @Override
    public int getItemCount(){
        return this.sharedCourses.size();
    }

    /**
     * Shared Course ViewHolder for SharedCoursesViewAdapter
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // View fields
        private final TextView courseSubjectTextView;
        private final TextView courseIdTextView;
        private final TextView courseYearTextView;
        private final TextView courseQuarterTextView;

        /**
         * Default constructor for SharedCoursesViewAdapter.ViewHolder
         *
         * @param itemView The view
         */
        ViewHolder(View itemView){
            super(itemView);
            this.courseSubjectTextView = itemView.findViewById(R.id.course_subject_row_textview);
            this.courseIdTextView = itemView.findViewById(R.id.course_number_row_textview);
            this.courseYearTextView = itemView.findViewById(R.id.course_year_row_textview);
            this.courseQuarterTextView = itemView.findViewById(R.id.course_quarter_row_textview);

        }

        /**
         * Sets course in the ViewHolder
         *
         * @param course The given Course
         */
        public void setCourse(Course course){
            this.courseSubjectTextView.setText(course.getSubject());
            this.courseIdTextView.setText(course.getNumber());
            this.courseQuarterTextView.setText(course.getQuarter());
            this.courseYearTextView.setText(course.getYear());
        }
    }
}
