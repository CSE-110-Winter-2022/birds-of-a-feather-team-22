package com.example.birdsofafeather.factory;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.example.birdsofafeather.R;
import com.example.birdsofafeather.factory.PromptFactory;

import java.util.List;

public class EnterNameWithStopPromptFactory extends PromptFactory {

    @Override
    public AlertDialog createPrompt(Activity activity, AlertDialog current, List list) {
        Log.d("<Home>", "creating first stop prompt AlertDialog");

        LayoutInflater inflater = activity.getLayoutInflater();
        View contextView = inflater.inflate(R.layout.select_or_enter_session_name_popup,null);//activity_home_screen_enter_name, null);

        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(activity);

        promptBuilder.setView(contextView);

        current.cancel();

        return promptBuilder.create();
    }
}
