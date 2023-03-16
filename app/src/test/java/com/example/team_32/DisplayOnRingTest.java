package com.example.team_32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowContextWrapper;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class DisplayOnRingTest {
    private UserDatabase testDB;
    private UserDao testDao;
    private mainUser testMainUser;
    private UserRepo testRepo;

    MainActivity mainActivity;

    @Before
    public void resetDataBase(){
        Context context = ApplicationProvider.getApplicationContext();
        testDB = Room.inMemoryDatabaseBuilder(context, UserDatabase.class).allowMainThreadQueries().build();
        UserDatabase.inject(testDB);
        List<User> users = User.loadJSON(context, "user_app.json");
        //Located at UCSD
        testMainUser = mainUser.singleton("testMainUser", 32.88006F, -117.23402F, 0);
        testDao = testDB.getDao();
        testRepo = new UserRepo(testDao, null);
        testRepo.upsertLocal(testMainUser);
        testRepo.upsertAllLocal(users);
    }

    @Before
    public void grantPermissions(){
        mainActivity = Robolectric.buildActivity(MainActivity.class).get();
        ShadowContextWrapper shadowContextWrapper = shadowOf(mainActivity);
        shadowContextWrapper.grantPermissions("android.permission.ACCESS_FINE_LOCATION");
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();
    }

    @Test
    public void testMainUser(){
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity( act -> {
            ListView ringView = act.ringView;
            System.err.println(ringView.getChildCount());
            TextView label = ringView.getChildAt(ringView.getChildCount()-1).findViewById(R.id.usr_label);
            assertEquals(testMainUser.label,label.getText());
        });
    }
    @Test
    public void testUserOneMile(){
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity( act -> {
            ListView ringView = act.ringView;
            System.err.println(ringView.getChildCount());
            TextView label = ringView.getChildAt(ringView.getChildCount()-2).findViewById(R.id.usr_label);
            ImageView dot = ringView.getChildAt(ringView.getChildCount()-2).findViewById(R.id.usr_dot);
            assertEquals(View.VISIBLE,label.getVisibility());
            assertEquals(View.GONE,dot.getVisibility());
        });
    }
    @Test
    public void testUserOnRing(){
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity( act -> {
            ListView ringView = act.ringView;
            TextView label = ringView.getChildAt(ringView.getChildCount()-3).findViewById(R.id.usr_label);
            ImageView dot = ringView.getChildAt(ringView.getChildCount()-3).findViewById(R.id.usr_dot);
            assertEquals(View.GONE,label.getVisibility());
            assertEquals(View.VISIBLE,dot.getVisibility());
        });
    }
}

