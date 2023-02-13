package com.example.team_32;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import android.Manifest;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private OrientationService orientationService;

    private LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orientationService = OrientationService.singleton(this);
        ImageView comFace = findViewById(R.id.compassFace);

        orientationService.getOrientation().observe(this, ori -> {
            float degrees = (float) Math.toDegrees((double) ori);
            comFace.setRotation(-degrees);
        });



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        locationService= LocationService.singleton(this);

        TextView textView = (TextView) findViewById(R.id.serviceTextView);

        locationService.getLocation().observe(this, loc -> {
            textView.setText(Double.toString(loc.first) + " , " +
                    Double.toString(loc.second));
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