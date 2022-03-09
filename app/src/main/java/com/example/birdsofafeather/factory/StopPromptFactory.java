package com.example.birdsofafeather.factory;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.R;
import com.example.birdsofafeather.SessionCoursesAdapter;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.factory.PromptFactory;

import java.util.List;

public class StopPromptFactory<Course> extends PromptFactory {

    @Override
    public AlertDialog createPrompt(Activity activity, AlertDialog current, List list) {
        Log.d("<Home>", "creating second stop prompt AlertDialog");

        LayoutInflater inflater = activity.getLayoutInflater();
        View contextView = inflater.inflate(R.layout.choose_session_name_popup,null);//activity_home_screen_stop_alert, null);

        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(activity);


        RecyclerView sessionsView = contextView.findViewById(R.id.classes_list);

        sessionsView.setLayoutManager(new LinearLayoutManager(activity));
        sessionsView.setHasFixedSize(true);

        SessionCoursesAdapter adapter = new SessionCoursesAdapter(list);
        sessionsView.setAdapter(adapter);
        promptBuilder.setView(contextView);

        return promptBuilder.create();
    }
}
