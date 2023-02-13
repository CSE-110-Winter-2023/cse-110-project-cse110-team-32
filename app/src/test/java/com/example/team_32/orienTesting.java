package com.example.team_32;

import static org.junit.Assert.assertEquals;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;


@RunWith(RobolectricTestRunner.class)
public class orienTesting {

    @Test
    public void testOriSerRotated(){
        var mockOri = new MutableLiveData<Float>();
        var scenario = ActivityScenario.launch(MainActivity.class);
        var oriServ = OrientationService.singleton(null);
        oriServ.setMockOrientation(mockOri);
        var exp = (float)3;
        mockOri.postValue(exp);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(act ->{

           var expDeg = Float.toString(exp);
            TextView orin = act.findViewById(R.id.orienText);
           var obser = (orin.getText());
           assertEquals(expDeg, obser);
        });
    }

    @Test
    public void testMock(){
        var scenario = ActivityScenario.launch(MainActivity.class);
        var orientationService = OrientationService.singleton(null);
        var mockOrien2 = new MutableLiveData<Float>();
        var expected = (float)3;
        mockOrien2.setValue(expected);
        orientationService.setMockOrientation(mockOrien2);
        assert mockOrien2.equals(orientationService.getMock());
    }

}