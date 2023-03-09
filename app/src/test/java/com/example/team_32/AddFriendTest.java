package com.example.team_32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.widget.Button;
import android.widget.EditText;
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
import org.robolectric.shadows.ShadowDialog;

@RunWith(RobolectricTestRunner.class)
public class AddFriendTest {
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
    public void testDialog(){
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().start().get();
        var scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(act ->{
            act.findViewById(R.id.add_friend_btn).performClick();
            var dialog = ShadowDialog.getLatestDialog();
            assertTrue(dialog.isShowing());
    });
}
}
