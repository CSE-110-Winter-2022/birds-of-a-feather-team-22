package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Optional;

public class SetCoursesActivity extends AppCompatActivity {

    /*UI elements*/
    private TextView courseNameTextView;
    private TextView courseNumberTextView;
    private Spinner quarterSpinner;
    private Spinner yearSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_courses);

        /*instantiate field variables*/
        TextView courseNameTextView = findViewById(R.id.course_name_textview);
        TextView courseNumberTextView = findViewById(R.id.course_number_textview);
        //get the spinner from the xml.
        quarterSpinner = findViewById(R.id.quarter_spinner);
        yearSpinner = findViewById(R.id.year_spinner);
        //create a list of items for the spinner.
        //String[] items = new String[]{"Fall 2021", "Summer 2021","Spring 2021", "Winter 2020", "Fall 2020", "Summer 2020", "Spring 2020", "Winter 2020", "Fall 2019"};

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.

        ArrayAdapter<CharSequence> adapter_quarter = ArrayAdapter.createFromResource(this, R.array.quarter_array, android.R.layout.simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        adapter_quarter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapter_year = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        quarterSpinner.setAdapter(adapter_quarter);
        yearSpinner.setAdapter(adapter_year);

    }

    /*display alertbox showing user-readable usage statement*/
    public void onCoursesHelpClicked(View view){
        String help_message = "";
        Utilities.showAlert(this, help_message);
    }

    public void onCoursesSubmitClicked(View view) {
        /*get input and load appropriately*/
        String courseName = courseNameTextView.getText().toString();
        Optional<Integer> maybeCourseNumber = Utilities.parseCount(courseNumberTextView.getText().toString());

        //if input is invalid, show alert
        if(!maybeCourseNumber.isPresent()){
            Utilities.showAlert(this, "Please enter a valid course number, such as 15 or 120");
            return;
        }


        /*clear all form fields*/
        courseNameTextView.setText("");
        courseNumberTextView.setText("");

    }

    public void onNextClicked(View view){
        Intent intent = new Intent(this,BluetoothActivity.class);

        //testing
        ProgressBar progressBar = findViewById(R.id.progressBar3);

        ValueAnimator animator = ValueAnimator.ofInt(0, progressBar.getMax());
        animator.setDuration(1200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation){
                progressBar.setProgress((Integer)animation.getAnimatedValue());
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startActivity(intent);

            }
        });
        animator.start();
    }
}