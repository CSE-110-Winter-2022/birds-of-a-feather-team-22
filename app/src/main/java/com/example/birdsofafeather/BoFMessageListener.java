/*
 * This file is capable of creating a MessageListener and is able to receive messages, parse them,
 * and send out messages.
 *
 * Author: Group 22
 */

package com.example.birdsofafeather;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
 * The class is capable of receiving messages, parsing the data that is from the message, and is
 * able to send out messages.
 */
public class BoFMessageListener extends MessageListener implements BoFSubject {
    // Log tag
    private final String TAG = "<BoFMessageListener>";

    // Instance variables for class
    private AppDatabase db;
    private String sessionId;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private List<BoFObserver> observers;
    private Future<Profile> f1;
    private Future<Void> f2;
    private Future<Integer> f3;
    private Future<DiscoveredUser> f4;

    /**
     * Constructor for class.
     *
     * @param sessionId Current session
     * @param context The current context of the application
     * @return none
     */
    public BoFMessageListener(String sessionId, Context context) {
        this.sessionId = sessionId;
        this.db = AppDatabase.singleton(context);
        this.observers = new ArrayList<>();
    }

    /**
     * Receives a message to allow for parsing of data.
     *
     * @param message The message being received
     * @return none
     */
    @Override
    public synchronized void onFound(Message message) {
        Log.d(TAG, "Found message: " + new String(message.getContent()));

        parseInfo(new String(message.getContent()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "Unable to sleep thread for 1000ms");
        }
        for (BoFObserver observer : this.observers) {
            observer.updateMatchesList();
        }

    }

    /**
     * A message that is lost with its data.
     *
     * @param message The message being lost
     * @return none
     */
    @Override
    public void onLost(Message message) {
        Log.d(TAG, "Lost message: " + new String(message.getContent()));
    }

    /**
     * Receives data and parses through the data in order create profiles, course lists according
     * to each profile, and perform any actions from such user into the database.
     *
     * @param info A complete text with a user's information
     */
    private synchronized void parseInfo(String info) {
        String[] textBoxSeparated = info.split(",,,,");

        String profileId = textBoxSeparated[0].replaceAll("\n", "");
        String name = textBoxSeparated[1].replaceAll("\n", "");
        String photo = textBoxSeparated[2].replaceAll("\n", "");

        Log.d(TAG, "Parsed profile information: " + profileId + " " + name + " " + photo);
        this.f1 = this.backgroundThreadExecutor.submit(() -> {
            Profile user;
            if (this.db.profileDao().getProfile(profileId) == null) {
                user = new Profile(profileId, name, photo);
                this.db.profileDao().insert(user);
                Log.d(TAG, "Added Profile");
            }
            else {
                user = this.db.profileDao().getProfile(profileId);
            }

            return user;
        });

        Profile user = null;
        try {
            user = this.f1.get();
        } catch (Exception e) {
            Log.d(TAG, "Unable to retrieve profile!");
        }

        this.f1 = this.backgroundThreadExecutor.submit(() -> this.db.profileDao().getUserProfile(true));

        Profile self = null;
        try {
            self = this.f1.get();
        } catch (Exception e) {
            Log.d(TAG, "Unable to retrieve self profile!");
        }

        String[] classInfo = textBoxSeparated[3].split("\n");
        for (int i = 1; i < classInfo.length; i++) {
            String[] classInfoSeparated = classInfo[i].split(",");

            // TODO: Nearby for Waves
            if (classInfoSeparated[1].replaceAll("\n", "").equals("wave")) {
                String UUID = classInfoSeparated[0].replaceAll("\n", "");

                if (UUID.equals(self.getProfileId())) {
                    user.setIsWaving(true);
                    Profile finalUser = user;
                    this.f2 = this.backgroundThreadExecutor.submit(() -> {
                        this.db.profileDao().update(finalUser);
                        return null;
                    });
                }
                continue;
            }

            String year = classInfoSeparated[0].replaceAll("\n", "");
            String quarter = parseQuarter(classInfoSeparated[1].replaceAll("\n", ""));
            String subject = classInfoSeparated[2].replaceAll("\n", "");
            String number = classInfoSeparated[3].replaceAll("\n", "");
            String size = classInfoSeparated[4].replaceAll("\n", "");

            this.f2 = this.backgroundThreadExecutor.submit(() -> {
                if (db.courseDao().getCourse(profileId, year, quarter, subject, number, size) == null) {
                    Course course = new Course(profileId, year, quarter, subject, number, size);
                    db.courseDao().insert(course);
                    Log.d(TAG, "Added Course");
                }
                return null;
            });

        }

        Profile finalSelf = self;
        this.f3 = this.backgroundThreadExecutor.submit(() -> Utilities.getNumSharedCourses(db.courseDao().getCoursesByProfileId(finalSelf.getProfileId()),
                db.courseDao().getCoursesByProfileId(profileId)));

        int numSharedCourses = 0;
        try {
            numSharedCourses = this.f3.get();
        } catch (Exception e) {
            Log.d(TAG, "Unable to calculate number of shared courses!");
        }

        this.f4 = this.backgroundThreadExecutor.submit(() -> db.discoveredUserDao().getDiscoveredUserFromSession(profileId, sessionId));

        DiscoveredUser discovered = null;
        try {
            discovered = this.f4.get();
        } catch (Exception e) {
            Log.d(TAG, "Unable to retrieve discovered user!");
        }

        DiscoveredUser finalDiscovered = discovered;
        int finalNumSharedCourses = numSharedCourses;
        this.f2 = this.backgroundThreadExecutor.submit(() -> {
            if (finalDiscovered != null) {
                db.discoveredUserDao().delete(finalDiscovered);
                Log.d(TAG, "Deleted DiscoveredUser");
            }

            DiscoveredUser discoveredUser = new DiscoveredUser(profileId, this.sessionId, finalNumSharedCourses);
            db.discoveredUserDao().insert(discoveredUser);

            Log.d(TAG, "Added DiscoveredUser");

            return null;
        });
    }

    /**
     * Parse the encoding characters of a quarter to its equivalent name.
     *
     * @param quarter The encoded quarter name
     * @return The full quarter name
     */
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

    /**
     * Register any observers to the listener.
     *
     * @param observer An observer to listener
     * @return none
     */
    @Override
    public void register(BoFObserver observer) {
        this.observers.add(observer);
    }

    /**
     * Unregister/remove an observer from observing the listener.
     *
     * @param observer An observer to listener.
     * @return none
     */
    @Override
    public void unregister(BoFObserver observer) {
        this.observers.remove(observer);
    }
}
