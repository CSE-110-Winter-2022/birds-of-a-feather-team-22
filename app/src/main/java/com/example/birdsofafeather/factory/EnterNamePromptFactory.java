package com.example.birdsofafeather.factory;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.example.birdsofafeather.R;

import java.util.List;

public class EnterNamePromptFactory extends PromptFactory {

    @Override
    public AlertDialog createPrompt(Activity activity, AlertDialog current, List list) {
        Log.d("<Home>", "creating first stop prompt AlertDialog");

        LayoutInflater inflater = activity.getLayoutInflater();
        View contextView = inflater.inflate(R.layout.prev_new_session_popup,null);//activity_home_screen_enter_name, null);

        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(activity);

        promptBuilder.setView(contextView);

        return promptBuilder.create();

    }
}
