/*
 * This file is capable of displaying courses associated with a profile and courses in which have
 * matched with the self user.
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
 * View adapter for each current course in the MatchActivity stop popup Recycler View
 */
public class CurrentCoursesViewAdapter extends RecyclerView.Adapter<CurrentCoursesViewAdapter.ViewHolder>{
    // Current courses
    private final List<Course> currentCourses;

    /**
     * Default constructor for CurrentCoursesViewAdapter
     *
     * @param currentCourses A list of current courses
     */
    public CurrentCoursesViewAdapter(List<Course> currentCourses){
        super();
        this.currentCourses = currentCourses;
    }

    /**
     * On click method for when the view holder is created
     *
     * @param parent The parent ViewGroup
     * @param viewType The view type
     * @return A CurrentCoursesViewAdapter.ViewHolder
     */
    @NonNull
    @Override
    public CurrentCoursesViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.course_row, parent, false);

        return new ViewHolder(view);
    }

    /**
     * On click method for when the given ViewHolder is bound
     *
     * @param holder The given ViewHolder
     * @param position The position to bind.
     */
    @Override
    public void onBindViewHolder(@NonNull CurrentCoursesViewAdapter.ViewHolder holder, int position){
        holder.setSession(currentCourses.get(position));
    }

    /**
     * Gets the number of course items
     *
     * @return The number of current courses
     */
    @Override
    public int getItemCount(){
        return this.currentCourses.size();
    }

    /**
     * Current Course ViewHolder for the CurrentCoursesViewAdapter
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // View fields
        private final TextView currentCourseNameTextView;
        private final TextView currentCourseNumberTextView;

        /**
         * Default constructor for CurrentCoursesViewAdapter.ViewHolder
         *
         * @param view The view
         */
        ViewHolder(View view){
            super(view);
            this.currentCourseNameTextView = view.findViewById(R.id.course_row_subject_view);
            this.currentCourseNumberTextView = view.findViewById(R.id.course_row_number_view);

        }

        /**
         * Sets currentCourse in the ViewHolder
         *
         * @param currentCourse The current Course
         */
        public void setSession(Course currentCourse){
            this.currentCourseNameTextView.setText(currentCourse.getSubject());
            this.currentCourseNumberTextView.setText(currentCourse.getNumber());
        }
    }
}


