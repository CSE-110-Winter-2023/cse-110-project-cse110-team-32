package com.example.team_32;

import static com.example.team_32.Angle.angleBetweenLocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.util.Pair;
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
        ImageView home = findViewById(R.id.home);

        locationService.getLocation().observe(this, loc -> {
//            textView.setText(Double.toString(loc.first) + " , " +
//                    Double.toString(loc.second));

            Pair<Double, Double> loc2 = new Pair<>(21.3891, 39.8579);

//            Double angle = angleBetweenLocations(loc, loc2, 270);
//            textView.setText(Double.toString(angle));


            orientationService.getOrientation().observe(this, ori -> {
                float degrees = (float) Math.toDegrees((double) ori);
                Double angle = angleBetweenLocations(loc, loc2, degrees);
                textView.setText(Double.toString(angle));

//                textView.setText(Double.toString(degrees));

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) home.getLayoutParams();
                layoutParams.circleAngle = angle.floatValue();
                home.setLayoutParams(layoutParams);
            });

        });

        // get the other location -> have a box to enter it ?
        // add an arrow or a home icon to that points to the angel found.
        // add some testing
        // a label next to the icon
        // store anything like lab 4

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