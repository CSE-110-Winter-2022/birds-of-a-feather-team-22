package com.example.birdsofafeather.factory;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.R;
import com.example.birdsofafeather.SessionsAdapter;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Session;

import java.util.List;

public class SessionsPromptFactory<listType> extends PromptFactory{

    @Override
    public AlertDialog createPrompt(Activity activity, AlertDialog current, List list) {
        Log.d("<Home>", "creating session list prompt AlertDialog");

        //populate sessionsList with previously saved sessions
        AppDatabase db = AppDatabase.singleton(activity);
        List<Session> sessionsList = db.sessionDao().getAllSessions();

        LayoutInflater inflater = activity.getLayoutInflater();
        View contextView = inflater.inflate(R.layout.activity_home_screen_session_list, null);

        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(activity);
        //setListeners(contextView.findViewById(R.id.sessionFrameLayout), activity);

        RecyclerView sessionsView = contextView.findViewById(R.id.sessions_recycler_view);

        sessionsView.setLayoutManager(new LinearLayoutManager(activity));
        sessionsView.setHasFixedSize(true);

        SessionsAdapter adapter = new SessionsAdapter(sessionsList);
        sessionsView.setAdapter(adapter);
        promptBuilder.setView(contextView);

        return promptBuilder.create();
    }


    public void setListeners(View button, Activity activity){

    }

}
