package com.example.team_32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
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

    @Before
    public void resetDataBase(){
        mainUser.resetMain();
        UserRepo.resetRepo();
        Context context = ApplicationProvider.getApplicationContext();
        testDB = Room.inMemoryDatabaseBuilder(context, UserDatabase.class).allowMainThreadQueries().build();
        UserDatabase.inject(testDB);
        List<User> users = User.loadJSON(context, "user_app.json");
        //Located at UCSD
        testMainUser = mainUser.singleton("testMainUser", 32.88006F, -117.23402F, 0);
        testDao = testDB.getDao();
        testRepo = UserRepo.singleton(testDao, null);
        testRepo.upsertLocal(testMainUser);
        testRepo.upsertAllLocal(users);
    }

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_FINE_LOCATION);


    @Test
    public void testUserOneMile(){
        mainUser.resetMain();
        mainUser.singleton("testMainUser", 32.88006F, -117.23402F, 0);
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
        mainUser.singleton("testMainUser", 32.88006F, -117.23402F, 0);
        scenario.onActivity( act -> {
            act.closeExeInViewModel();
            act.ringAdapter.getMainJson();
            act.ringAdapter.resetMain();
            act.ringAdapter.notifyDataSetChanged();
            act.ringAdapter.getMainJson();
            shadowOf(Looper.getMainLooper()).idle();
            act.resetMainViewModel();
            act.zoomState = 1;
            ListView ringView = act.ringView;
            act.ringAdapter.getMainJson();
            System.err.println(ringView.getChildCount());
            TextView label = ringView.getChildAt(ringView.getChildCount()-1).findViewById(R.id.usr_label);
            ImageView dot = ringView.getChildAt(ringView.getChildCount()-1).findViewById(R.id.usr_dot);
            System.out.println(label.getText()+ "teext");
            assertEquals(View.VISIBLE,label.getVisibility());
            assertEquals(View.GONE,dot.getVisibility());
        });
    }
    @Test
    public void testUserOnRing(){
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity( act -> {
            act.zoomState = 1;
            act.onResume();
            act.closeExeInViewModel();
            ListView ringView = act.ringView;
            TextView label = ringView.getChildAt(ringView.getChildCount()-2).findViewById(R.id.usr_label);
            ImageView dot = ringView.getChildAt(ringView.getChildCount()-2).findViewById(R.id.usr_dot);
            assertEquals(View.GONE,label.getVisibility());
            assertEquals(View.VISIBLE,dot.getVisibility());
        });
    }
}

