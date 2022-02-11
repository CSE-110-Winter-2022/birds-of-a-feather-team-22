package com.example.birdsofafeather;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.birdsofafeather.db.Profile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MatchesViewAdapter extends RecyclerView.Adapter<MatchesViewAdapter.ViewHolder> {
    private final List<Profile> matches;
    private Context context;

    public MatchesViewAdapter(List<Profile> matches, Context context) {
        super();
        this.matches = matches;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchesViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.match_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.setMatch(this.matches.get(position), this.context);
        } catch (IOException exception) {
            ViewHolder.picture.setImageResource(R.drawable.feather_1);
        }

    }

    @Override
    public int getItemCount() {
        return this.matches.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private static ImageView picture;
        private final TextView name;
        private final TextView numMatches;
        private final TextView userID;

        ViewHolder(View view) {
            super(view);
            this.picture = view.findViewById(R.id.match_userPicture);
            this.name = view.findViewById(R.id.match_name);
            this.numMatches = view.findViewById(R.id.match_classesMatched);
            this.userID = view.findViewById(R.id.match_userID);
        }

        public void setMatch(Profile profile, Context context) throws IOException {
            this.name.setText(profile.getName());
            this.numMatches.setText("Classes matched: " + profile.getProfileId());
            this.userID.setText(new Integer(profile.getProfileId()).toString());
            Glide.with(context).load(profile.getPhoto()).into(picture);
        }
    }
}