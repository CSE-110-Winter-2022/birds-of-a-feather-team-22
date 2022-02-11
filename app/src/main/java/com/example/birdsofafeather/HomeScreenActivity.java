package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HomeScreenActivity extends AppCompatActivity {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;
    private AppDatabase db;
    private List<Profile> data;
    private RecyclerView matchesRecyclerView;
    private RecyclerView.LayoutManager matchesLayoutManager;
    private MatchesViewAdapter matchesViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        setTitle("Birds of a Feather");
        db = AppDatabase.singleton(this);
        this.future = backgroundThreadExecutor.submit(() -> {
            Profile p = db.profileDao().getProfile(1);
            if (p == null) {
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, NameActivity.class);
                    startActivity(intent);
                });
            }

            return null;
        });
        data = new ArrayList<>();
        Profile dummy1 = new Profile(3, "Bob","https://polisci.ucsd.edu/_images/210115-Geisel-135DSC_7396-UCSanDiego-ErikJepsen_1.jpeg");
        Profile dummy2 = new Profile(4, "Sam", "https://i0.wp.com/post.healthline.com/wp-content/uploads/2021/02/Female_Portrait_1296x728-header-1296x729.jpg?w=1155&h=2268");
        Profile dummy4 = new Profile(5, "Paul", "https://assets.pokemon.com/assets/cms2/img/pokedex/full/025.png");
        Profile dummy5 = new Profile(10, "Paul", "https://i0.wp.com/www.printmag.com/wp-content/uploads/2021/02/4cbe8d_f1ed2800a49649848102c68fc5a66e53mv2.gif?fit=476%2C280&ssl=1");
        Profile dummy6 = new Profile(12, "Paul", "https://i0.wp.com/www.printmag.com/wp-content/uploads/2021/02/4cbe8d_f1ed2800a49649848102c68fc5a66e53mv2.gif?fit=476%2C280&ssl=1");
        Profile dummy7 = new Profile(13, "Paul", "https://i0.wp.com/www.printmag.com/wp-content/uploads/2021/02/4cbe8d_f1ed2800a49649848102c68fc5a66e53mv2.gif?fit=476%2C280&ssl=1");
        Profile dummy8 = new Profile(154, "Paul", "https://i0.wp.com/www.printmag.com/wp-content/uploads/2021/02/4cbe8d_f1ed2800a49649848102c68fc5a66e53mv2.gif?fit=476%2C280&ssl=1");
        Profile dummy9 = new Profile(2222, "Paul", "https://i0.wp.com/www.printmag.com/wp-content/uploads/2021/02/4cbe8d_f1ed2800a49649848102c68fc5a66e53mv2.gif?fit=476%2C280&ssl=1");
        Profile dummy10 = new Profile(312312, "Paul", "https://i0.wp.com/www.printmag.com/wp-content/uploads/2021/02/4cbe8d_f1ed2800a49649848102c68fc5a66e53mv2.gif?fit=476%2C280&ssl=1");

        /*
        Profile dummy6 = new Profile(5, "Paul", "test");
        Profile dummy7 = new Profile(5, "Paul", "test");
        Profile dummy8 = new Profile(5, "Paul", "test");
        Profile dummy9 = new Profile(5, "Paul", "test");
        Profile dummy10 = new Profile(5, "Paul", "test");
        Profile dummy11 = new Profile(5, "Paul", "test");
         */
        data.add(dummy1);
        data.add(dummy2);
        data.add(dummy8);
        data.add(dummy4);
        data.add(dummy5);

        data.add(dummy6);
        data.add(dummy7);
        data.add(dummy9);
        data.add(dummy10);

        matchesRecyclerView = findViewById(R.id.matchesList);
        matchesViewAdapter = new MatchesViewAdapter(data,this);
        matchesRecyclerView.setAdapter(matchesViewAdapter);
        matchesLayoutManager = new LinearLayoutManager(this);
        matchesRecyclerView.setLayoutManager(matchesLayoutManager);
    }

    public void onClickStart(View view) {
        RecyclerView matchesList = findViewById(R.id.matchesList);
        Button stopButton = findViewById(R.id.stopButton);
        TextView findMatches = findViewById(R.id.textView6);
        Button startButton = findViewById(R.id.startButton);
        TextView matchesFound = findViewById(R.id.matchesFound);

        matchesFound.setVisibility(View.GONE);
        findMatches.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);
    }

    public void onClickStop(View view) {
        Button stopButton = findViewById(R.id.stopButton);
        Button startButton = findViewById(R.id.startButton);
        TextView matchesFound = findViewById(R.id.matchesFound);
        TextView findMatches = findViewById(R.id.textView6);
        RecyclerView recyclerView = findViewById(R.id.matchesList);

        stopButton.setVisibility(View.GONE);
        matchesFound.setVisibility(View.VISIBLE);
        findMatches.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.VISIBLE);
    }

    public void onClickMatch(View view) {
        TextView id = view.findViewById(R.id.match_userID);
        int userID = Integer.parseInt(id.getText().toString());
        Intent intent = new Intent(this, ViewProfileActivity.class);
        intent.putExtra("match", userID);
        startActivity(intent);
    }

    public void onDeleteDBClicked(View view) {
        db.clearAllTables();
    }

}