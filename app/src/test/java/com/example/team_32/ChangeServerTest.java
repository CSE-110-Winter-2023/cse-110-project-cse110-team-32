package com.example.team_32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.shadows.ShadowInstrumentation.getInstrumentation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowContextWrapper;

@RunWith(RobolectricTestRunner.class)
public class ChangeServerTest {
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
    public void testDialog() {
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().start().get();
        var scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(act -> {
            act.findViewById(R.id.change_server_btn).performClick();
            AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
            assertTrue(dialog.isShowing());
            EditText editText = (EditText) dialog.getCurrentFocus();
            System.out.println(editText.getText());
            String newServer = "example.com";
            editText.setText(newServer);
            System.out.println(editText.getText());
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
            act.runOnUiThread(() -> {
                System.out.println("Click?" + dialog.getButton(DialogInterface.BUTTON_POSITIVE));
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                System.out.println("Clicked");
            });
            getInstrumentation().waitForIdleSync();
            assertFalse(dialog.isShowing());
            assertEquals(newServer, UserAPI.server);
        });

    }
}
