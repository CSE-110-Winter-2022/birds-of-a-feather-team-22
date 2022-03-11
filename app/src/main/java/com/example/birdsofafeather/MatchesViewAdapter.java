/*
 * This file is capable of allowing information from profiles to be able to be displayed onto
 * views for the user to be able to view and interact with.
 *
 * Authors: CSE 110 Winter 2022, Group 22
 * Alvin Hsu, Drake Omar, Fernando Tello, Raul Martinez Beltran, Robert Jiang, Stephen Shen
 */

package com.example.birdsofafeather;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * View adapter for each match in the MatchActivity Recycler View.
 */
public class MatchesViewAdapter extends RecyclerView.Adapter<MatchesViewAdapter.ViewHolder> {
    // Log tag
    private final String TAG = "<MVA>";

    // DB/Thread fields
    private Context context;
    private AppDatabase db;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<List<Course>> f1;
    private Future<Profile> f2;

    // Self and Match information
    private final List<Pair<Profile, Integer>> matches;
    private List<Integer> numSharedThisQuarter;
    private Profile selfProfile;
    private List<Course> selfCourses;

    /**
     * Default Constructor for MatchesViewAdapter
     *
     * @param matches The list of matches
     * @param context The context
     */
    public MatchesViewAdapter(List<Pair<Profile, Integer>> matches, Context context) {
        super();
        this.matches = matches;
        this.context = context;
        this.db = AppDatabase.singleton(context);

        this.f2 = this.backgroundThreadExecutor.submit(() -> this.db.profileDao().getSelfProfile(true));
        this.selfProfile = null;
        try {
            Log.d(TAG, "Retrieved self profile!");
            this.selfProfile = this.f2.get();
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving self profile!");
            e.printStackTrace();
        }

        this.f1 = this.backgroundThreadExecutor.submit(() -> this.db.courseDao().getCoursesByProfileId(this.selfProfile.getProfileId()));
        this.selfCourses = null;
        try {
            Log.d(TAG, "Retrieved self Courses!");
            this.selfCourses = this.f1.get();
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving self courses!");
            e.printStackTrace();
        }

        // Get number of current courses shared with self
        getNumSharedCoursesThisQuarter();
    }

    /**
     * Computes the number of shared courses this quarter with self for all matches
     */
    private void getNumSharedCoursesThisQuarter() {
        this.numSharedThisQuarter = new ArrayList<>();
        for (Pair<Profile, Integer> match : matches) {
            this.numSharedThisQuarter.add(getNumSharedCoursesThisQuarterForProfile(match.first));
        }
    }

    /**
     * Computes the number of shared courses this quarter with self for a given Profile
     *
     * @param match The given Profile
     * @return The number of shared courses this quarter with self for match
     */
    private int getNumSharedCoursesThisQuarterForProfile(Profile match) {
        String currentYear = Utilities.getCurrentYear();
        String currentQuarter = Utilities.getCurrentQuarter();
        this.f1 = this.backgroundThreadExecutor.submit(() -> this.db.courseDao().getCoursesByProfileId(match.getProfileId()));
        List<Course> matchCourses = null;
        try {
            Log.d(TAG, "Retrieved match courses!");
            matchCourses = this.f1.get();
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving match courses!");
            e.printStackTrace();
        }

        int numSharedCoursesThisQuarter = 0;
        List<Course> sharedCourses =  Utilities.getSharedCourses(this.selfCourses, matchCourses);
        for (Course course : sharedCourses) {
            if (course.getQuarter().equals(currentQuarter) && course.getYear().equals(currentYear)) {
                numSharedCoursesThisQuarter++;
            }
        }

        return numSharedCoursesThisQuarter;
    }

    /**
     * On click method for when the view holder is created
     *
     * @param parent The parent ViewGroup
     * @param viewType The view type
     * @return A MatchesViewAdapter.ViewHolder
     */
    @NonNull
    @Override
    public MatchesViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.match_row, parent, false);

        return new ViewHolder(view);
    }

    /**
     * On click method for when the given ViewHolder is bound
     *
     * @param holder The given ViewHolder
     * @param position The position to bind.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setMatch(this.matches.get(position), this.numSharedThisQuarter.get(position), context);
    }

    /**
     * Gets the number of match items
     *
     * @return The number of matches
     */
    @Override
    public int getItemCount() {
        return this.matches.size();
    }

    /**
     * Match ViewHolder for the MatchesViewAdapter
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // View fields
        private final ImageView matchPhoto;
        private final TextView matchName;
        private final TextView matchNum;
        private final TextView matchNumThisQuarter;
        private final TextView matchId;
        private final ImageView star;
        private final ImageView wave;

        /**
         * Default constructor for MatchesViewAdapter.ViewHolder
         *
         * @param view The view
         */
        ViewHolder(View view) {
            super(view);
            this.matchPhoto = view.findViewById(R.id.match_photo_view);
            this.matchName = view.findViewById(R.id.match_name_view);
            this.matchNum = view.findViewById(R.id.match_num_courses_view);
            this.matchNumThisQuarter = view.findViewById(R.id.match_num_courses_quarter_view);
            this.matchId = view.findViewById(R.id.match_profile_id_view);
            this.star = view.findViewById(R.id.star);
            this.wave = view.findViewById(R.id.wave);
        }

        /**
         * Sets a match in the ViewHolder
         *
         * @param match The match Pair containing a Profile and Integer
         * @param numSharedThisQuarter The number of courses shared this quarter with self
         * @param context The context
         */
        public void setMatch(Pair<Profile, Integer> match, Integer numSharedThisQuarter, Context context) {
            this.matchName.setText(match.first.getName());

            // Check for semantics
            if (match.second == 1) {
                this.matchNum.setText(match.second + " Course");
            }
            else {
                this.matchNum.setText(match.second + " Courses");
            }

            // Check for semantics
            if (numSharedThisQuarter == 1) {
                this.matchNumThisQuarter.setText(numSharedThisQuarter + " Current Course");
            }
            else {
                this.matchNumThisQuarter.setText(numSharedThisQuarter + " Current Courses");
            }


            // Set favorite star
            if (match.first.getIsFavorite()) {
                this.star.setImageResource(R.drawable.filled_star);
            }
            else {
                this.star.setImageResource(R.drawable.hollow_star);
            }

            // Set hidden profile id view
            this.matchId.setText(match.first.getProfileId());

            // Set profile photo
            Glide.with(context)
                    .load(match.first.getPhoto())
                    .apply(RequestOptions.placeholderOf(R.drawable.feather_1)
                            .override(1000,1000).centerCrop())
                    .into(this.matchPhoto);

            // Set wave GIF if match is waving
            if (match.first.getIsWaving()) {
                this.wave.setVisibility(View.VISIBLE);
                Glide.with(context).asGif().load(R.drawable.waving_hand).into(this.wave);
            }
            else {
                this.wave.setVisibility(View.GONE);
            }
        }
    }
}