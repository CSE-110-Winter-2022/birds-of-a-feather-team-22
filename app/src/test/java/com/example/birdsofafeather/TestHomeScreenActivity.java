package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.birdsofafeather.db.Profile;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestHomeScreenActivity {
    @Rule
    public ActivityScenarioRule<HomeScreenActivity> scenarioRule = new ActivityScenarioRule<>(HomeScreenActivity.class);

    @Test
    public void testDummy(){

    }
}
