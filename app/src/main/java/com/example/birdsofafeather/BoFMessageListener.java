package com.example.birdsofafeather;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.DiscoveredUser;
import com.example.birdsofafeather.db.Profile;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BoFMessageListener extends MessageListener implements BoFSubject {
    private AppDatabase db;
    private String sessionId;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private List<BoFObserver> observers;

    public BoFMessageListener(String sessionId, Context context) {
        this.sessionId = sessionId;
        this.db = AppDatabase.singleton(context);
        this.observers = new ArrayList<>();
    }

    @Override
    public void onFound(Message message) {
        Log.d(TAG, "Found message: " + new String(message.getContent()));

        parseInfo(new String(message.getContent()));
        for (BoFObserver observer : this.observers) {
            observer.onChange();
        }
    }

    @Override
    public void onLost(Message message) {
        Log.d(TAG, "Lost message: " + new String(message.getContent()));
    }

    private void parseInfo(String info) {
        String[] textBoxSeparated = info.split(",,,,");

        String userProfileId = textBoxSeparated[0];
        String userName = textBoxSeparated[1];
        String userThumbnail = textBoxSeparated[2];
        Profile user = null;
//        backgroundThreadExecutor.submit(() -> {
            if (this.db.profileDao().getProfile(userProfileId) == null) {
                user = new Profile(userProfileId, userName, userThumbnail);
                this.db.profileDao().insert(user);
                Log.d(TAG, "Added Profile");
            }
            else {
                user = this.db.profileDao().getProfile(userProfileId);
            }

//        });

        String[] classInfo = textBoxSeparated[3].split("\n");
        for (int i = 1; i < classInfo.length; i++) {
            String[] classInfoSeparated = classInfo[i].split(",");

            // TODO: Nearby for Waves
            if (classInfoSeparated[1].equals("wave")) {
                String UUID = classInfoSeparated[0];
                Profile self = this.db.profileDao().getUserProfile(true);
                if (UUID.equals(self.getProfileId())) {
                    user.setIsWaving(true);
                    this.db.profileDao().update(user);
                }
                continue;
            }

            String year = classInfoSeparated[0];
            String quarter = parseQuarter(classInfoSeparated[1]);
            String subject = classInfoSeparated[2];
            String number = classInfoSeparated[3];
            String size = classInfoSeparated[4];

//            backgroundThreadExecutor.submit(() -> {
                if (db.courseDao().getCourse(userProfileId, year, quarter, subject, number, size) == null) {
                    Course course = new Course(userProfileId, year, quarter, subject, number, size);
                    db.courseDao().insert(course);
                    Log.d(TAG, "Added Course");
                }
//            });
        }

//        backgroundThreadExecutor.submit(() -> {
            int numSharedCourses = Utilities.getNumSharedCourses(db.courseDao().getCoursesByProfileId(db.profileDao().getUserProfile(true).getProfileId()),
                    db.courseDao().getCoursesByProfileId(userProfileId));

            DiscoveredUser discovered = db.discoveredUserDao().getDiscoveredUserFromSession(userProfileId, sessionId);

            if (discovered != null) {
                db.discoveredUserDao().delete(discovered);
                Log.d(TAG, "Deleted DiscoveredUser");
            }
            Log.d(TAG, "Added DiscoveredUser");
            DiscoveredUser discoveredUser = new DiscoveredUser(userProfileId, this.sessionId, numSharedCourses);
            db.discoveredUserDao().insert(discoveredUser);
//        });
    }

    public String parseQuarter(String quarter) {
        switch(quarter) {
            case "FA":
                return "Fall";
            case "WI":
                return "Winter";
            case "SP":
                return "Spring";
            case "S1":
                return "Summer Session 1";
            case "S2":
                return "Summer Session 2";
            case "SS":
                return "Special Summer Session";
            default:
                return null;
        }
    }

    @Override
    public void register(BoFObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void unregister(BoFObserver observer) {
        this.observers.remove(observer);
    }
}
