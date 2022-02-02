package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class SetCoursesActivity extends AppCompatActivity {


    /*UI elements*/
    private TextView courseNameTextView;
    private TextView courseNumberTextView;
    private Spinner quarterAndYearSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_courses);

        /*instantiate field variables*/
        TextView courseNameTextView = findViewById(R.id.course_name_textview);
        TextView courseNumberTextView = findViewById(R.id.course_number_textview);
        //get the spinner from the xml.

//create a list of items for the spinner.
        String[] items = new String[]{"Fall 2021", "Spring 2021", "Winter 2020", "Fall 2020", "Spring 2020"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        quarterAndYearSpinner.setAdapter(adapter);

    }

    /*display alertbox showing user-readable usage statement*/
    public void onCoursesHelpClicked(View view){
        String help_message = "";
        Utilities.showAlert(this, help_message);

    }

    public void onCoursesSubmitClicked(View view) {
        /*get input and load appropriately*/


        /*clear all form fields*/
        courseNameTextView.setText("");
        //courseNumberTextView.setText("");

    }

    public void onNextClicked(View view){

    }
}