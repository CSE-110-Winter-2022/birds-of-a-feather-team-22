package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

        setTitle("Pick Your Classes");

        //progress bar animation at start
        /*ProgressBar progressBar = findViewById(R.id.progressBar3);

        ValueAnimator animator = ValueAnimator.ofInt(0, progressBar.getMax());
        animator.setDuration(1000);
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


            }
        });
        animator.start();
        */
        /*instantiate field variables*/
        courseNameTextView = findViewById(R.id.course_name_textview);
        courseNumberTextView = findViewById(R.id.course_number_textview);
        //get the spinner from the xml.
        quarterSpinner = findViewById(R.id.quarter_spinner);
        yearSpinner = findViewById(R.id.year_spinner);

        //create a list of items for the spinner.
        //String[] items = new String[]{"Fall 2021", "Summer 2021","Spring 2021", "Winter 2020", "Fall 2020", "Summer 2020", "Spring 2020", "Winter 2020", "Fall 2019"};

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.

        ArrayAdapter<CharSequence> quarter_adapter = ArrayAdapter.createFromResource(this, R.array.quarter_array, android.R.layout.simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        quarter_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        ArrayAdapter<CharSequence> year_adapter = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        quarterSpinner.setAdapter(quarter_adapter);
        yearSpinner.setAdapter(year_adapter);



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
        Button doneButton = findViewById(R.id.done_button);
        doneButton.setVisibility(View.VISIBLE);
    }

    public void onNextClicked(View view){
        Intent intent = new Intent(this,BluetoothActivity.class);
        startActivity(intent);

    }
}