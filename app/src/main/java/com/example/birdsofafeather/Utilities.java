package com.example.birdsofafeather;

import android.app.Activity;
import android.app.AlertDialog;

import java.util.Optional;

public class Utilities {
    public static void showAlert(Activity activity, String message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, id) -> dialog.cancel())
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static AlertDialog showError(Activity activity, String title, String message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, id) -> dialog.cancel())
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
        return alertDialog;
    }

    public static Optional<Integer> parseCount(String str){
        try {
            int maxCount = Integer.parseInt(str);
            return Optional.of(maxCount);
        } catch (NumberFormatException e){
            return Optional.empty();
        }
    }

    public static String formatString(String str) {
        return str.trim().toUpperCase();
    }
}
