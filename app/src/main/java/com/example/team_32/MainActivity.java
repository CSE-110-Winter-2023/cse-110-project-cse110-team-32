package com.example.team_32;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private OrientationService orientationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        orientationService = OrientationService.singleton(this);

        TextView orTxt = findViewById(R.id.orienText);

        orientationService.getOrientation().observe(this, ori -> {
            orTxt.setText(Float.toString(ori));
        });
    }

    @Override
    protected void onPause() {
         super.onPause();
        orientationService.unregSensors();
    }

    @Override
    protected void onResume() {
        super.onResume();
        orientationService.regSensorListeners();
    }
}