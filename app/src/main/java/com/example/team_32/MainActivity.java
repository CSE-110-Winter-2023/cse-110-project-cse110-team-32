package com.example.team_32;

import static com.example.team_32.Angle.angleBetweenLocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import android.Manifest;

import java.util.Optional;
import java.util.OptionalDouble;

public class MainActivity extends AppCompatActivity {
    private OrientationService orientationService;
    private LocationService locationService;
    private boolean first = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.labels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        TextView locationLabel = findViewById(R.id.location_label);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (first) {
                    first = false;
                    return;
                }
                locationLabel.setText(((TextView) arg1).getText());
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        orientationService = OrientationService.singleton(this);
        ImageView comFace = findViewById(R.id.compassFace);
        orientationService.getOrientation().
                observe(this, ori ->
                {
                    float degrees = (float) Math.toDegrees((double) ori);
                    comFace.setRotation(-degrees);
                });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
        locationService = LocationService.singleton(this);
        TextView textView = (TextView) findViewById(R.id.serviceTextView);
        ImageView home = findViewById(R.id.home);
        locationService.getLocation().
                observe(this, loc ->
                {
                    String text = String.format("Lat: %.2f, Lon: %.2f", loc.first, loc.second);
                    textView.setText(text);
                    orientationService.getOrientation().observe(this, ori -> {
                        float degrees = (float) Math.toDegrees((double) ori);
                        TextView longitude = (TextView) findViewById(R.id.longitude);
                        TextView latitude = (TextView) findViewById(R.id.latitude);
                        if (!this.inputValid())
                            return;
                        Pair<Double, Double> loc2 = new Pair<>(Utilities.parseDouble(latitude.getText().toString()).get(), Utilities.parseDouble(longitude.getText().toString()).get());
                        Double angle = angleBetweenLocations(loc, loc2, degrees);
                        textView.setText(Double.toString(angle));
                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) home.getLayoutParams();
                        layoutParams.circleAngle = angle.floatValue();
                        home.setLayoutParams(layoutParams);
                    });
                });
        loadHomeLocation();
    }

    private boolean inputValid() {
        TextView longitude = (TextView) findViewById(R.id.longitude);
        TextView latitude = (TextView) findViewById(R.id.latitude);
        return Utilities.parseDouble(longitude.getText().toString()).isPresent() && Utilities.parseDouble(latitude.getText().toString()).isPresent();
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

    private void loadHomeLocation() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        TextView latitude = findViewById(R.id.latitude);
        String loadedLatitude = preferences.getString("latitude", "");
        latitude.setText(loadedLatitude);

        TextView longitude = findViewById(R.id.longitude);
        String loadedLongitude = preferences.getString("longitude", "");
        longitude.setText(loadedLongitude);

        TextView locationLabel = findViewById(R.id.location_label);
        String loadedLocation_label = preferences.getString("location_label", "");
        locationLabel.setText(loadedLocation_label);
    }

    private void saveHomeLocation() {
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        TextView latitude = findViewById(R.id.latitude);
        TextView longitude = findViewById(R.id.longitude);
        TextView locationLabel = findViewById(R.id.location_label);
        editor.putString("latitude", latitude.getText().toString());
        editor.putString("longitude", longitude.getText().toString());
        editor.putString("location_label", locationLabel.getText().toString());
        editor.apply();
    }

    public void onSaveClicked(View view) {
        saveHomeLocation();
    }
    public void onSetOrientationClicked(View view){
        TextView orientation = findViewById(R.id.orientationText);
        Optional<Double> ori = Utilities.parseDouble(orientation.getText().toString());
        if (!ori.isPresent()){
            orientationService.regSensorListeners();
            throw new IllegalArgumentException("Invalid orientation");
        } else {
            orientationService.unregSensors();
            orientationService.setOrientationValue(ori.get().floatValue());
        }
    }

    public void onSelectLocationLabelClicked(View view) {
        Spinner spinner = findViewById(R.id.spinner);
        spinner.performClick();
    }
}