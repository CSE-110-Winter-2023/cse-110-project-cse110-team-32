package com.example.team_32;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.util.Pair;
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
public class LocationTesting {
    private MainActivity mainActivity;
    private LocationService LocServ;
    private ShadowContextWrapper shadowContextWrapper;
    private MutableLiveData<Pair<Double, Double>>mockLoc;
    @Before
    public void setUp() throws Exception {
        // Create and launch the MainActivity
        mainActivity = Robolectric.buildActivity(MainActivity.class).get();
        shadowContextWrapper = shadowOf(mainActivity);
    }

    @Test
    public void testLocationWhenGranted(){
        // Grant the location permission to the context
        ShadowContextWrapper shadowContextWrapper = shadowOf(mainActivity);
        shadowContextWrapper.grantPermissions("android.permission.ACCESS_FINE_LOCATION");
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().start().get();
        mockLoc = new MutableLiveData<Pair<Double, Double>>();
        LocServ = LocationService.singleton(null);
        LocServ.setMockLocationSource(mockLoc);
        var exp = new Pair<Double, Double>(32.4, 10.3);
        mockLoc.setValue(exp);
        assertEquals(exp, LocServ.getLocation().getValue());
        LocServ.finalize();
//        Robolectric.buildActivity(MainActivity.class).destroy();
    }

    @Test
    public void testLocationWhenNotGranted(){
        // deny the location permission to the context
        shadowContextWrapper.denyPermissions("android.permission.ACCESS_FINE_LOCATION");
        try{
            ActivityScenario m = ActivityScenario.launch(MainActivity.class);
        }catch (IllegalStateException e){
            assert true;
            return;
        }
        assert false;
    }
}