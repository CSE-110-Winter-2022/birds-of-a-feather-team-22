package com.example.birdsofafeather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.birdsofafeather.db.Session;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.ViewHolder>{
    private final List<Session> sessions;

    public SessionsAdapter(List<Session> sessionCourses){
        super();
        this.sessions = sessionCourses;

    }

    @NonNull
    @Override
    public SessionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.session_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionsAdapter.ViewHolder holder, int position){
        holder.setSession(sessions.get(position));
    }

    @Override
    //return number of profiles in course
    public int getItemCount(){
        return this.sessions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView sessionNameTextView;
            private final TextView sessionIdTextView;

        ViewHolder(View itemView){
            super(itemView);
            this.sessionNameTextView = itemView.findViewById(R.id.session_name_text_view);
            this.sessionIdTextView = itemView.findViewById(R.id.session_id_text_view);
        }
        public void setSession(Session session){
            this.sessionNameTextView.setText(session.getName());
            this.sessionIdTextView.setText(session.getSessionId());
        }
    }


}

