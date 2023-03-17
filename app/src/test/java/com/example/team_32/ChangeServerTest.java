package com.example.team_32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.shadows.ShadowInstrumentation.getInstrumentation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Looper;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowContextWrapper;

@RunWith(RobolectricTestRunner.class)
public class ChangeServerTest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_FINE_LOCATION);



    @Test
    public void testDialog() throws InterruptedException {
        mainUser.singleton("empty",0,0,0);
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().start().get();
        mainActivity.closeExeInViewModel();

        mainActivity.findViewById(R.id.change_server_btn).performClick();
        shadowOf(Looper.getMainLooper()).idle();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertTrue(dialog.isShowing());

        EditText editText = dialog.findViewById(R.id.edittext_uid);
        assertEquals(UserAPI.server.toString(), editText.getText().toString());
        
    }

}
