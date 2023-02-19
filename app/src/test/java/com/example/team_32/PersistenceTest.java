package com.example.team_32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.util.Pair;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import org.robolectric.shadows.ShadowContextWrapper;

@RunWith(RobolectricTestRunner.class)
public class PersistenceTest {
    private MainActivity mainActivity;
    @Before
    public void setUp() {
        // Create and launch the MainActivity
        mainActivity = Robolectric.buildActivity(MainActivity.class).get();
        // Grant the location permission to the context
        ShadowContextWrapper shadowContextWrapper = shadowOf(mainActivity);
        shadowContextWrapper.grantPermissions("android.permission.ACCESS_FINE_LOCATION");
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();
    }
    @Test
    public void testNoDataSaved(){
        String lonData = "90";
        String latData = "130.2";
        String locLabel = "Home";
        var scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(act ->{
           TextView longitude = act.findViewById(R.id.longitude);
           TextView latitude = act.findViewById(R.id.latitude);
           TextView locationLabel = act.findViewById(R.id.location_label);
           longitude.setText(lonData);
           latitude.setText(latData);
           locationLabel.setText(locLabel);
        });
        scenario.moveToState(Lifecycle.State.DESTROYED);
        scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(act ->{
            TextView longitude = act.findViewById(R.id.longitude);
            TextView latitude = act.findViewById(R.id.latitude);
            TextView locationLabel = act.findViewById(R.id.location_label);
            assertNotEquals(longitude.getText().toString(), lonData);
            assertNotEquals(latitude.getText().toString(), latData);
            assertNotEquals(locationLabel.getText().toString(), locLabel);
        });
    }
    @Test
    public void testDataSaved(){
        String lonData = "90";
        String latData = "130.2";
        String locLabel = "Home";
        var scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(act ->{
            TextView longitude = act.findViewById(R.id.longitude);
            TextView latitude = act.findViewById(R.id.latitude);
            TextView locationLabel = act.findViewById(R.id.location_label);
            longitude.setText(lonData);
            latitude.setText(latData);
            locationLabel.setText(locLabel);
            act.onSaveClicked(longitude);
        });
        scenario.moveToState(Lifecycle.State.DESTROYED);
        scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(act ->{
            TextView longitude = act.findViewById(R.id.longitude);
            TextView latitude = act.findViewById(R.id.latitude);
            TextView locationLabel = act.findViewById(R.id.location_label);
            assertEquals(longitude.getText().toString(), lonData);
            assertEquals(latitude.getText().toString(), latData);
            assertEquals(locationLabel.getText().toString(), locLabel);
        });
    }
}
