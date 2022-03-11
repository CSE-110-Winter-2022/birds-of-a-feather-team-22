/*
 * This file is capable of enabling a list of sessions to be displayed to the user.
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
import com.example.birdsofafeather.db.Session;

/**
 * View adapter for each session in the MatchProfileActivity start popup Recycler View.
 */
public class SessionsViewAdapter extends RecyclerView.Adapter<SessionsViewAdapter.ViewHolder>{
    // Sessions
    private final List<Session> sessions;

    /**
     * Default constructor for SessionViewAdapter
     * 
     * @param sessions A list of Sessions
     */
    public SessionsViewAdapter(List<Session> sessions){
        super();
        this.sessions = sessions;

    }

    /**
     * On click method for when the view holder is created
     *
     * @param parent The parent ViewGroup
     * @param viewType The view type
     * @return A SessionsViewAdapter.ViewHolder
     */
    @NonNull
    @Override
    public SessionsViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.session_row, parent, false);

        return new ViewHolder(view);
    }

    /**
     * On click method for when the ViewHolder is bound
     *
     * @param holder The given ViewHolder
     * @param position The position to bind
     */
    @Override
    public void onBindViewHolder(@NonNull SessionsViewAdapter.ViewHolder holder, int position){
        holder.setSession(sessions.get(position));
    }

    /**
     * Gets the number of session items
     *
     * @return The number of sessions
     */
    @Override
    public int getItemCount(){
        return this.sessions.size();
    }

    /**
     * Sessions ViewHolder for SessionsViewAdapter
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // View fields
        private final TextView sessionNameTextView;
        private final TextView sessionIdTextView;

        /**
         * Default constructor for SessionsViewAdapter.ViewHolder
         *
         * @param view The view
         */
        ViewHolder(View view){
            super(view);
            this.sessionNameTextView = view.findViewById(R.id.session_row_name_view);
            this.sessionIdTextView = view.findViewById(R.id.session_row_id_view);
        }

        /**
         * Sets session in the ViewHolder
         *
         * @param session The given Session
         */
        public void setSession(Session session){
            this.sessionNameTextView.setText(session.getName());
            this.sessionIdTextView.setText(session.getSessionId());
        }
    }


}

