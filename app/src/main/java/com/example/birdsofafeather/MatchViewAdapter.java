package com.example.birdsofafeather;

import android.content.Context;
import android.graphics.Color;
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

// View adapter for HomeScreenActivity
public class MatchViewAdapter extends RecyclerView.Adapter<MatchViewAdapter.ViewHolder> {
    private final List<Pair<Profile, Integer>> matches;
    private List<Integer> numSharedThisQuarter;
    private Context context;
    private AppDatabase db;

    public MatchViewAdapter(List<Pair<Profile, Integer>> matches, Context context) {
        super();
        this.matches = matches;
        this.context = context;
        this.db = AppDatabase.singleton(context);
        getNumSharedCoursesThisQuarter();
    }

    private void getNumSharedCoursesThisQuarter() {
        this.numSharedThisQuarter = new ArrayList<>();
        for (Pair<Profile, Integer> match : matches) {
            this.numSharedThisQuarter.add(getNumSharedCoursesFromProfile(match.first));
        }
    }

    private int getNumSharedCoursesFromProfile(Profile match) {
        String currentYear = Utilities.getCurrentYear();
        String currentQuarter = Utilities.getCurrentQuarter();

        List<Course> matchCourses = this.db.courseDao().getCoursesByProfileId(match.getProfileId());
        String userId = this.db.profileDao().getUserProfile(true).getProfileId();
        List<Course> userCourses= this.db.courseDao().getCoursesByProfileId(userId);

        int numSharedCoursesThisQuarter = 0;
        List<Course> sharedCourses =  Utilities.getSharedCourses(userCourses, matchCourses);
        for (Course course : sharedCourses) {
            if (course.getQuarter().equals(currentQuarter) && course.getYear().equals(currentYear)) {
                numSharedCoursesThisQuarter++;
            }
        }

        return numSharedCoursesThisQuarter;
    }

    @NonNull
    @Override
    public MatchViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.match_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setMatch(this.matches.get(position), this.numSharedThisQuarter.get(position), context);
    }

    //Matches List length
    @Override
    public int getItemCount() {
        return this.matches.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView matchPhoto;
        private final TextView matchName;
        private final TextView matchNum;
        private final TextView matchNumThisQuarter;
        private final TextView matchId;
        private final ImageView star;
        private final ImageView wave;

        //assign profile ids
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
        //assign matches information to correct view Ids
        public void setMatch(Pair<Profile, Integer> match, Integer numSharedThisQuarter, Context context) {
            this.matchName.setText(match.first.getName());
            if (match.second == 1) {
                this.matchNum.setText(match.second + " Course");
            }
            else {
                this.matchNum.setText(match.second + " Courses");
            }
            if (numSharedThisQuarter == 1) {
                this.matchNumThisQuarter.setText(numSharedThisQuarter + " Current Course");
            }
            else {
                this.matchNumThisQuarter.setText(numSharedThisQuarter + " Current Courses");
            }


            // Set star
            if (match.first.getIsFavorite()) {
                this.star.setImageResource(R.drawable.filled_star);
//                this.star.setText("★");
//                this.star.setTextColor(Color.parseColor("#FFFF00"));
            }
            else {
                this.star.setImageResource(R.drawable.hollow_star);
//                this.star.setText("☆");
//                this.star.setTextColor(Color.parseColor("#BBBBBB"));
            }
            this.matchId.setText(match.first.getProfileId());
//            Glide.with(context).load(match.first.getPhoto()).apply(new RequestOptions().override(350, 350)).into(this.matchPhoto);
            Glide.with(context)
                    .load(match.first.getPhoto())
                    .apply(RequestOptions.placeholderOf(R.drawable.feather_1)
                            .override(1000,1000).centerCrop())
                    .into(this.matchPhoto);

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