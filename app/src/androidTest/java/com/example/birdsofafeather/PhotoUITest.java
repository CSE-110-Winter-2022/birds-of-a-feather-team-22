package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PhotoUITest {

    @Test
    public void EnterPhotoAndSubmitTest() {
        ActivityScenario<PhotoActivity> photoScreen = ActivityScenario.launch(PhotoActivity.class);
        photoScreen.onActivity(activity -> {
            TextView photoView = activity.findViewById(R.id.photo_view);
            Button submitButton = activity.findViewById(R.id.submit_button);

            photoView.setText("https://th.bing.com/th/id/OIP.Sbr5C2YXgT73lBtQa24z6wHaE8?pid=ImgDet&rs=1");
            submitButton.performClick();

            assertEquals(photoView.getText().toString(), "https://th.bing.com/th/id/OIP.Sbr5C2YXgT73lBtQa24z6wHaE8?pid=ImgDet&rs=1");
        });
    }

    public void FirstLaunchViewTest() {
        ActivityScenario<PhotoActivity> photoScreen = ActivityScenario.launch(PhotoActivity.class);
        photoScreen.onActivity(activity -> {
            TextView photoView = activity.findViewById(R.id.photo_view);
            Button submitButton = activity.findViewById(R.id.submit_button);

            assertEquals(photoView.getText().toString(), "");
            assertEquals(submitButton.getVisibility(), View.VISIBLE);
        });
    }
}

