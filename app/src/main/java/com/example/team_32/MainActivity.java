package com.example.team_32;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Matrix;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private OrientationService orientationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        orientationService = OrientationService.singleton(this);
        ImageView comFace = findViewById(R.id.compassFace);
        TextView orin = findViewById(R.id.orienText);

        orientationService.getOrientation().observe(this, ori -> {
            float degrees = (float) Math.toDegrees((double) ori);
            orin.setText(Float.toString(ori));
            comFace.setRotation(-degrees);
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