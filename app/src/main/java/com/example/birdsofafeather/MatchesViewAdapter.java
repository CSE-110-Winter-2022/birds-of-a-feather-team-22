package com.example.birdsofafeather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MatchesViewAdapter extends RecyclerView.Adapter<MatchesViewAdapter.ViewHolder> {
    private final List<Profile> matches;

    public MatchesViewAdapter(List<Profile> matches) {
        super();
        this.matches = matches;
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

    }

    /**
    @Override
    public void onBindViewHolder(@NonNull NotesViewAdapter.ViewHolder holder, int position) {
        holder.setMatch
    }
    **/

    @Override
    public int getItemCount() {
        return this.matches.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView picture;
        private final TextView name;
        private final TextView numMatches;
        private Match match;

        ViewHolder(View view) {
            super(view);
            this.picture = view.findViewById(R.id.match_userPicture);
            this.name = view.findViewById(R.id.match_name);
            this.numMatches = view.findViewById(R.id.match_classesMatched);
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setNumMatches(int amount) {
            this.numMatches.setText("Classes Matches: " + amount);
        }

        public void setPicture(String url) {
        }

        public void setMatch(Match match) {
            this.match = match;
        }
    }
}
