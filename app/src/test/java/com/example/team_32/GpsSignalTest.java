package com.example.team_32;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowContextWrapper;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GpsSignalTest {
    LocationService locationService;
    mainUser testMainUser;
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void testWithNoGPS(){
        mainUser.resetMain();
        testMainUser = mainUser.singleton("test0", 32.88006F, -117.23402F, 0);
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        locationService = LocationService.singleton();
//        locationService.disable();
        scenario.onActivity( act -> {
//            System.out.println(act.viewModel.mainJson());
            ImageView gpsDot  = act.findViewById(R.id.gpsIndicator);
            TextView gpsLabel  = act.findViewById(R.id.GPS_time);
//            System.out.println(gpsDot.getColorFilter());
            // making sure that the UI is updated
            assertEquals(((PorterDuffColorFilter)gpsDot.getColorFilter()),  new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.ADD));
        });
    }
    @Test
    public void testWithGPS(){
        mainUser.resetMain();
        testMainUser = mainUser.singleton("testMainUser", 32.88006F, -117.23402F, System.currentTimeMillis()/1000);
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity( act -> {
            act.viewModel.resetMainUser();
            ImageView gpsDot  = act.findViewById(R.id.gpsIndicator);
            TextView gpsLabel  = act.findViewById(R.id.GPS_time);
            //Checking Color
            assertEquals("",gpsLabel.getText());
            assertEquals(((PorterDuffColorFilter)gpsDot.getColorFilter()), new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.ADD));
            act.viewModel.closeExe();
//            act.getMainLooper().quitSafely();
        });
        scenario.moveToState(Lifecycle.State.DESTROYED);
//        scenario.close();
        System.out.println("======================================================================");
        System.out.println("======================================================================");
    }
}
