package com.example.birdsofafeather;

import android.content.Context;
import android.util.Log;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.DiscoveredUser;
import com.example.birdsofafeather.db.Profile;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MockMessageListener extends MessageListener {
    private static final String TAG = "Message";
    private final MessageListener messageListener;
    private final ScheduledExecutorService executor;
    private String sessionId;
    private AppDatabase db;

    public MockMessageListener(MessageListener realMessageListener, String messageStr, Context context, String sessionId) {
        this.messageListener = realMessageListener;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.db = AppDatabase.singleton(context);
        this.sessionId = sessionId;

        executor.execute(() -> {
            Message message = new Message(messageStr.getBytes(StandardCharsets.UTF_8));
            this.messageListener.onFound(message);
            parseInfo(messageStr);
            Log.d(TAG, "Received nearby message!");
        });
    }

    protected void parseInfo(String info) {
        String[] textBoxSeparated = info.split(",,,,");

        String UUID = textBoxSeparated[0];
        String userName = textBoxSeparated[1];
        String userThumbnail = textBoxSeparated[2];

        Profile profile = new Profile(UUID, userName, userThumbnail);
        db.profileDao().insert(profile);

        String[] classInfo = textBoxSeparated[3].split("\n");
        for (int i = 1; i < classInfo.length; i++) {
            String[] classInfoSeparated = classInfo[i].split(",");

            if (classInfoSeparated[1].equals("wave")) {
                String UUID_self = classInfoSeparated[0];
                String filter = classInfoSeparated[1];
                continue;
            }

            String year = classInfoSeparated[0];
            String quarter = classInfoSeparated[1];
            String subject = classInfoSeparated[2];
            String number = classInfoSeparated[3];
            String size = classInfoSeparated[4];

            Course course = new Course(UUID, year, quarter, subject, number, size);
            db.courseDao().insert(course);
        }

        int numSharedCourses = Utilities.getNumSharedCourses(db.courseDao().getCoursesByProfileId("1"),
                db.courseDao().getCoursesByProfileId(profile.getProfileId()));

        DiscoveredUser discovered = new DiscoveredUser(profile.getProfileId(), this.sessionId, numSharedCourses, false);
        db.discoveredUserDao().insert(discovered);

    }
}