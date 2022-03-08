package com.example.birdsofafeather.factory;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

public abstract class PromptFactory<listType> {

    public abstract AlertDialog createPrompt(Activity activity, AlertDialog current, List<Object> list);

    public enum PromptType {
        ENTERNAME,
        ENTERNAMEWITHSTOP,
        STOP,
        SESSIONS
    }
}
