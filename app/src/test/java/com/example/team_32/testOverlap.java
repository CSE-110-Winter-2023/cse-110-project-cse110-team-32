package com.example.team_32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.opengl.Visibility;
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
import org.robolectric.RobolectricTestRunner;

import java.util.List;
@RunWith(RobolectricTestRunner.class)
public class testOverlap {
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
        List<User> users = User.loadJSON(context, "user_app2.json");
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
    public void testOverLapTruncatedOneMile(){
        // Testing Opening the app (having def. zoom) then seeing friends that overlaping
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity( act -> {
            act.zoomState = 1;
            act.onResume();
            act.closeExeInViewModel();
            ListView ringView = act.ringView;
            //seventh long label
            System.out.println("========");
            TextView label = ringView.getChildAt(ringView.getChildCount()-2).findViewById(R.id.usr_label);
            ImageView dot = ringView.getChildAt(ringView.getChildCount()-2).findViewById(R.id.usr_dot);
            System.out.println(label.getText().toString() + " 1 ");
            //vegas  label
            TextView label2 = ringView.getChildAt(ringView.getChildCount()-1).findViewById(R.id.usr_label);
            ImageView dot2 = ringView.getChildAt(ringView.getChildCount()-1).findViewById(R.id.usr_dot);
            System.out.println(label2.getText().toString() +  " 2");
            //seventh
            TextView label3 = ringView.getChildAt(ringView.getChildCount()-3).findViewById(R.id.usr_label);
            ImageView dot3 = ringView.getChildAt(ringView.getChildCount()-3).findViewById(R.id.usr_dot);
            System.out.println(label3.getText().toString()  + " 3");
            //check 0-1, 1-10 mile case
            assertEquals(label2.getText().toString(), "vegas");
            assertEquals(label.getText().toString(), "seven...");
            assertNotEquals(ringView.getChildAt(ringView.getChildCount()-1).getX(), ringView.getChildAt(ringView.getChildCount()-2).getX());
            assertNotEquals(ringView.getChildAt(ringView.getChildCount()-1).getY(), ringView.getChildAt(ringView.getChildCount()-2).getY());
            assertEquals(label3.getText().toString(), "seventh");
            assertEquals(label2.getVisibility(), View.GONE);
            assertEquals(dot2.getVisibility(), View.VISIBLE);


        });
    }


}


