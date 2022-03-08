package com.example.birdsofafeather;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.birdsofafeather.db.Profile;

import java.util.List;

// View adapter for HomeScreenActivity
public class MatchViewAdapter extends RecyclerView.Adapter<MatchViewAdapter.ViewHolder> {
    private final List<Pair<Profile, Integer>> matches;
    private Context context;

    public MatchViewAdapter(List<Pair<Profile, Integer>> matches, Context context) {
        super();
        this.matches = matches;
        this.context = context;
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
        holder.setMatch(this.matches.get(position), this.context);
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
        private final TextView matchId;

        //assign profile ids
        ViewHolder(View view) {
            super(view);
            this.matchPhoto = view.findViewById(R.id.match_photo_view);
            this.matchName = view.findViewById(R.id.match_name_view);
            this.matchNum = view.findViewById(R.id.match_num_courses_view);
            this.matchId = view.findViewById(R.id.match_profile_id_view);
        }
        //assign matches information to correct view Ids
        public void setMatch(Pair<Profile, Integer> match, Context context) {
            this.matchName.setText(match.first.getName());
            if (match.second == 1) {
                this.matchNum.setText(match.second + " Shared Course");
            }
            else {
                this.matchNum.setText(match.second + " Shared Courses");
            }
            this.matchId.setText(new Integer(match.first.getProfileId()).toString());
            Glide.with(context).load(match.first.getPhoto()).into(this.matchPhoto);
        }
    }
}