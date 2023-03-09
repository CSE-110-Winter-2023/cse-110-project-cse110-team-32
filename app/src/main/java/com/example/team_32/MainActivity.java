package com.example.team_32;

import static com.example.team_32.Angle.angleBetweenLocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import android.Manifest;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private OrientationService orientationService;
    private LocationService locationService;
    private mainUser mainuser;
    private String public_code;
    private UserRepo repo;
    private boolean first = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplication().getApplicationContext();
        var viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        var db = UserDatabase.provide(context);
        repo = new UserRepo(db.getDao(), UserAPI.provide());
        loadUser();
        if(public_code != null && !public_code.isEmpty()){
            Log.i("PRE11", public_code);
            var temp = repo.existsLocal(public_code);
            var temp2 = repo.getLocalMain(public_code);

            Log.i("PRE11", ("Founded? "+ String.valueOf(temp) + "value ? " + String.valueOf(temp2.public_code)));
            mainuser = mainUser.fromUser(temp2);
        }
        if (!mainUser.exists()){
            Log.i("nameActivity", "Went there ? ");
            Intent userNameAct = new Intent(this, UsernameActivity.class);
            startActivity(userNameAct);
        }


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
                    if (loc.first != null && loc.second != null) {
                        if (mainuser != null){ mainuser.updateLoc(loc.first, loc.second);
                            repo.upsertRemote(mainuser);
                            repo.upsertLocal(mainuser);
                        }
                    }
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
        mainuser = mainUser.singleton();
       if (mainuser != null) {
           saveUser();
           loadUser();
       }
        orientationService.regSensorListeners();
    }
    private void saveUser(){
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("public_code", mainuser.public_code);
        editor.apply();
    }

    private void loadUser(){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        public_code = preferences.getString("public_code","");
        TextView uidLabel =findViewById(R.id.UIDlable);
        uidLabel.setText("UID: "+public_code);
        Log.i("PRE11", "loadUser: " + public_code);
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
    public boolean onSetOrientationClicked(View view){
        TextView orientation = findViewById(R.id.orientationText);
        Optional<Double> ori = Utilities.parseDouble(orientation.getText().toString());
        if (!ori.isPresent()){
            orientationService.regSensorListeners();
            return false;
        } else {
            orientationService.unregSensors();
            Double inRad = Math.toRadians(-ori.get());
            System.out.println(inRad);
            orientationService.setOrientationValue(inRad.floatValue());
            return true;
        }
    }

    public void onSelectLocationLabelClicked(View view) {
        Spinner spinner = findViewById(R.id.spinner);
        spinner.performClick();
    }


    public void onAddFriendClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add friend");
        final EditText uid = new EditText(this);
        uid.setHint("UID");
        uid.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(uid);
        builder.setPositiveButton("confirm", (dialog, which) -> {
            // TODO: get users from real database and add friends to real database
            List<FakeUser> users = FakeDatabase.getUsers(); //the MainUser
            List<FakeUser> friends = FakeDatabase.getFriends(currentUid); //Friends
//            Optional<FakeUser> found = users.stream().filter(f -> f.getUid().equals(uid.getText().toString())).findFirst(); // find the new friend
//            if (found.isPresent()) {
//                if (friends.stream().anyMatch(f -> f.getUid().equals(found.get().getUid()))) {
//                    Snackbar.make(view, "Friend already added", Snackbar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
//                } else if (found.get().getUid().equals(currentUid)) {
//                    Snackbar.make(view, "You can't add yourself", Snackbar.LENGTH_SHORT).setTextColor(Color.WHITE).show();
//                } else {
//                    FakeDatabase.addFriend(currentUid, found.get());
//                    Snackbar.make(view, "Friend added", Snackbar.LENGTH_LONG).setTextColor(Color.GREEN).show();
//
//                }
//            } else {
//                Snackbar.make(view, "User not found", Snackbar.LENGTH_SHORT).setTextColor(Color.RED).show();
//            }
        });
        builder.setNegativeButton("cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}