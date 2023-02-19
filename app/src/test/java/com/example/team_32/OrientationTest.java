package com.example.team_32;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowContextWrapper;


@RunWith(RobolectricTestRunner.class)
public class OrientationTest {
    private MainActivity mainActivity;
    private OrientationService oriServ;
    private MutableLiveData<Float>mockOri;
    @Before
    public void setUp() {
        // Create and launch the MainActivity
        mainActivity = Robolectric.buildActivity(MainActivity.class).get();
        // Grant the location permission to the context
        ShadowContextWrapper shadowContextWrapper = shadowOf(mainActivity);
        shadowContextWrapper.grantPermissions("android.permission.ACCESS_FINE_LOCATION");
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();
        mockOri = new MutableLiveData<Float>();
        oriServ = OrientationService.singleton(null);
    }

    @Test
    public void testPosRotated(){
        oriServ.setMockOrientation(mockOri);
        var exp = (float)3;
        mockOri.setValue(exp);
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().start().get();
        assertEquals(Float.toString(exp), Double.toString(oriServ.getOrientation().getValue()));
    }
    @Test
    public void testNegRotated(){
        oriServ.setMockOrientation(mockOri);
        var exp = (float)-2;
        mockOri.setValue(exp);
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().start().get();
        assertEquals(Float.toString(exp), Double.toString(oriServ.getOrientation().getValue()));
    }

    @Test
    public void testNotRotated(){
        oriServ.setMockOrientation(mockOri);
        var exp = (float)0;
        mockOri.setValue(exp);
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().start().get();
        assertEquals(Float.toString(exp), Double.toString(oriServ.getOrientation().getValue()));
    }
    @Test
    public void testMockBtnValid(){
        var exp = (float)5;
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().start().get();
        mockOri = oriServ.getOrientation();
        var scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        oriServ = OrientationService.singleton(null);
        scenario.onActivity(act ->{
            TextView orientationText  = act.findViewById(R.id.orientationText);
            orientationText.setText(Float.toString(exp));
            act.onSetOrientationClicked(orientationText);
            String one = Float.toString(oriServ.getOrientation().getValue());
            assertEquals(Float.toString(exp), Float.toString(oriServ.getOrientation().getValue()));
        });
    }
    @Test
    public void testMockBtnInvalid(){
        var exp = "dsf";
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().start().get();
        mockOri = oriServ.getOrientation();
        var scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        oriServ = OrientationService.singleton(null);
        scenario.onActivity(act ->{
            TextView orientationText  = act.findViewById(R.id.orientationText);
            orientationText.setText(exp);
            try{
                act.onSetOrientationClicked(orientationText);
            } catch (IllegalArgumentException e){
                assert true;
                return;
            }
            assert false;
        });
    }

}