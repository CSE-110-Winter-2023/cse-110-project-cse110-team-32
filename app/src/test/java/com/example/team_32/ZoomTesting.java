package com.example.team_32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.view.View;
import android.widget.ImageView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowContextWrapper;

@RunWith(RobolectricTestRunner.class)
public class ZoomTesting {
    MainActivity mainActivity;
    @Before
    public void grantPermissions(){
        mainActivity = Robolectric.buildActivity(MainActivity.class).get();
        ShadowContextWrapper shadowContextWrapper = shadowOf(mainActivity);
        shadowContextWrapper.grantPermissions("android.permission.ACCESS_FINE_LOCATION");
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();
    }

    @Test
    public void testDefaultZoom(){
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity( act -> {
            assertEquals(1, act.zoomState);
            assertEquals(View.GONE, act.findViewById(R.id.fiveHMileRing).getVisibility());
            assertEquals(View.VISIBLE, act.findViewById(R.id.tenMileRing).getVisibility());
            assertEquals(View.VISIBLE, act.findViewById(R.id.oneMileRing).getVisibility());
        });
    }
    @Test
    public void testOneMileZoom(){
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity( act -> {
            //start with two zones
            assertEquals(1, act.zoomState);
            act.findViewById(R.id.ZoomInBtn).performClick();
            // MOVED to one ring state (0)
            assertEquals(0, act.zoomState);
            assertEquals(View.GONE, act.findViewById(R.id.fiveHMileRing).getVisibility());
            assertEquals(View.GONE, act.findViewById(R.id.tenMileRing).getVisibility());
            assertEquals(View.VISIBLE, act.findViewById(R.id.oneMileRing).getVisibility());
        });
    }
    @Test
    public void testFiveHMileZoom(){
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity( act -> {
            //start with two zones
            assertEquals(1, act.zoomState);
            act.findViewById(R.id.ZoomOutBtn).performClick();
            // MOVED to one ring state (2)
            assertEquals(2, act.zoomState);
            assertEquals(View.VISIBLE, act.findViewById(R.id.fiveHMileRing).getVisibility());
            assertEquals(View.VISIBLE, act.findViewById(R.id.tenMileRing).getVisibility());
            assertEquals(View.VISIBLE, act.findViewById(R.id.oneMileRing).getVisibility());
        });
    }
    @Test
    public void testZoomBtnClickable(){
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity( act -> {
            //start with two zones
            //both btn clickable
            assertEquals(1, act.zoomState);
            ImageView zoomInBtn = act.findViewById(R.id.ZoomInBtn);
            ImageView zoomOutBtn = act.findViewById(R.id.ZoomOutBtn);
            assertTrue(zoomInBtn.isClickable());
            assertTrue(zoomOutBtn.isClickable());

            // MOVED to one ring state (2)
            //zoom-in btn clickable, out not
            zoomOutBtn.performClick();
            assertEquals(2, act.zoomState);
            assertTrue(zoomInBtn.isClickable());
            assertFalse(zoomOutBtn.isClickable());

            // MOVE to ring state (1)
            //both btn clickable
            zoomInBtn.performClick();
            assertEquals(1, act.zoomState);
            assertTrue(zoomInBtn.isClickable());
            assertTrue(zoomOutBtn.isClickable());

            // MOVE to ring state (0)
            //zoom-out btn clickable, in not
            zoomInBtn.performClick();
            assertEquals(0, act.zoomState);
            assertFalse(zoomInBtn.isClickable());
            assertTrue(zoomOutBtn.isClickable());

            assertEquals(View.GONE, act.findViewById(R.id.fiveHMileRing).getVisibility());
            assertEquals(View.GONE, act.findViewById(R.id.tenMileRing).getVisibility());
            assertEquals(View.VISIBLE, act.findViewById(R.id.oneMileRing).getVisibility());
        });
    }
}
