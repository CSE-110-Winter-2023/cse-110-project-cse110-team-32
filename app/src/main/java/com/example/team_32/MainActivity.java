package com.example.team_32;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import android.Manifest;

import java.util.Optional;

public class MainActivity extends AppCompatActivity {
    public int zoomState;
    private OrientationService orientationService;
    private LocationService locationService;
    private String public_code;
    private ImageView oneMileRing;
    private ImageView tenMileRing;
    private ImageView fiveHMileRing;
    public RingAdapter ringAdapter;
    public ListView ringView;


    UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadZoomState();
        oneMileRing = findViewById(R.id.oneMileRing);
        tenMileRing = findViewById(R.id.tenMileRing);
        fiveHMileRing = findViewById(R.id.fiveHMileRing);

        Log.i("ZOOM", "SETTING " + zoomState);
        setZoomState(zoomState);
        getPermissions();
        setupServer();
        viewModel = setUpViewModel();
        loadMainUser();
        if (!mainUser.exists()) {
            Log.i("nameActivity", "Went there ? ");
            Intent userNameAct = new Intent(this, UsernameActivity.class);
            startActivity(userNameAct);
        }

        orientationService = OrientationService.singleton(this);
        setUpOri();
        Log.i("Location", "Setting up");

        Log.i("Location", "Done");
//        setUpLoc();

        ringView = findViewById(R.id.listView1);
        ringAdapter = new RingAdapter(this);
        ringView.setAdapter(ringAdapter);
        viewModel.getUsers().observe(this, ringAdapter::setUsers);
        ringAdapter.notifyDataSetChanged();
    }

    private void getPermissions() {
        Log.i("Location", "getPermissions: ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            setUpLoc();
        }else {
            setUpLoc();
        }
    }

    private void setUpLoc() {
        if (locationService == null){
            locationService = LocationService.singleton(this);
        }
        locationService.getLocation().
                observe(this, loc ->
                {
                    Log.i("Location updated", String.valueOf(loc));
                    String text = String.format("Lat: %.2f, Lon: %.2f", loc.first, loc.second);
                    if (loc.first != null && loc.second != null) {
                        viewModel.updateMain(loc);
                    }
                });
    }

    // Todo: Fix Orientation Set-Up
    private void setUpOri() {
        if (orientationService.getOrientation().hasObservers())
            return;
        orientationService.getOrientation().
                observe(this, ori ->
                {
                    float degrees = (float) Math.toDegrees((double) ori);
                    findViewById(R.id.mainUserInd).setRotation(degrees);
                });
    }

    private UserViewModel setUpViewModel() {
        return new ViewModelProvider(this).get(UserViewModel.class);
    }


    @Override
    protected void onPause() {
        super.onPause();
        orientationService.unregSensors();
        saveZoomState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mainUser.exists()) {
            saveMainUser();
            loadMainUser();
            viewModel.reSyncAll();
        }
        orientationService.regSensorListeners();
        loadZoomState();
        setZoomState(zoomState);
    }

    private void saveMainUser() {
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("public_code", viewModel.getMainUserCode());
        editor.apply();
    }

    private void loadMainUser() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        public_code = preferences.getString("public_code", "");
        TextView uidLabel = findViewById(R.id.UIDlable);
        uidLabel.setText("UID: " + public_code);
        if (public_code != null && !public_code.isEmpty()) {
            Log.i("CODE", "loadMainUser: " + public_code);
            viewModel.loadMainUser(public_code);
        }
    }
    private void saveZoomState(){
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("zoomState", zoomState);
        editor.apply();
    }
    private void loadZoomState(){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        zoomState = preferences.getInt("zoomState", 1);

    }

    public boolean onSetOrientationClicked(View view) {
        TextView orientation = findViewById(R.id.orientationText);
        Optional<Double> ori = Utilities.parseDouble(orientation.getText().toString());
        if (!ori.isPresent()) {
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

    public void onAddFriendClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add friend");
        final EditText uid = new EditText(this);
        Log.i("UID", "onAddFriendClicked: " + uid.getId());
        uid.setHint("UID");
        uid.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(uid);
        builder.setPositiveButton("confirm", (dialog, which) -> {
            String newUID = uid.getText().toString();
            viewModel.getUser(newUID, this);
        });
        builder.setNegativeButton("cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void onChangeServerClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Server");
        final EditText server = new EditText(this);
        server.setHint("Server");
        server.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(server);
        server.setText(UserAPI.server);
        builder.setPositiveButton("confirm", (dialog, which) -> {
            String newServer = server.getText().toString();
            UserAPI.server = newServer;
            SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("server", newServer);
            editor.apply();
            Log.i("Server", "change server to: " + newServer);
        });
        builder.setNegativeButton("cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    // state 0: only the 1 mile rings
    // state 1: 1,10 miles rings
    // state 2: 1, 10, 500 miles rings
    public void onZoomInClicked(View view){
        if (zoomState == 0)
            return;
        zoomState--;
        findViewById(R.id.ZoomOutBtn).setClickable(true);
        if (zoomState == 0){
            findViewById(R.id.ZoomInBtn).setClickable(false);
        }
        setZoomState(zoomState);
    }
    public void onZoomOutClicked(View view){
        if (zoomState == 2)
            return;
        zoomState++;
        findViewById(R.id.ZoomInBtn).setClickable(true);
        if (zoomState == 2){
            findViewById(R.id.ZoomOutBtn).setClickable(false);
        }
        setZoomState(zoomState);
    }
    private void setZoomState(int zoomState) {
        if (zoomState == 0){
            // only 1 mile ring
            fiveHMileRing.setVisibility(View.GONE);
            tenMileRing.setVisibility(View.GONE);
            ////////////////////
            ViewGroup.LayoutParams nineFivePxPara = oneMileRing.getLayoutParams();
            nineFivePxPara.width = 950;
            nineFivePxPara.height = 950;
            ///////////////////
            oneMileRing.setLayoutParams(nineFivePxPara);
        } else if (zoomState == 1) {
            fiveHMileRing.setVisibility(View.GONE);
            tenMileRing.setVisibility(View.VISIBLE);
            /////////////////////
            ViewGroup.LayoutParams ninePxPara = tenMileRing.getLayoutParams();
            ninePxPara.width = 900;
            ninePxPara.height = 900;
            ViewGroup.LayoutParams fourFivePxPara = oneMileRing.getLayoutParams();
            fourFivePxPara.width = 450;
            fourFivePxPara.height = 450;
            ////////////////////////
            Log.i("LayOut", tenMileRing.getMeasuredWidth() + " " + tenMileRing.getMeasuredHeight() + " " + tenMileRing.getX() + " ," + tenMileRing.getY());
            tenMileRing.setLayoutParams(ninePxPara);
            Log.i("LayOut", tenMileRing.getMeasuredWidth() + " " + tenMileRing.getMeasuredHeight() + " " + tenMileRing.getX() + " ," + tenMileRing.getY());
            oneMileRing.setLayoutParams(fourFivePxPara);
        }else {
            fiveHMileRing.setVisibility(View.VISIBLE);
            tenMileRing.setVisibility(View.VISIBLE);
            /////////////////////
            ViewGroup.LayoutParams nineFivePxPara = fiveHMileRing.getLayoutParams();
            nineFivePxPara.width = 950;
            nineFivePxPara.height = 950;
            ViewGroup.LayoutParams sevenFivePxPara = tenMileRing.getLayoutParams();
            sevenFivePxPara.width = 750;
            sevenFivePxPara.height = 750;
            ViewGroup.LayoutParams fourFivePxPara = oneMileRing.getLayoutParams();
            fourFivePxPara.width = 350;
            fourFivePxPara.height = 350;
            /////////////////////
            fiveHMileRing.setLayoutParams(nineFivePxPara);
            tenMileRing.setLayoutParams(sevenFivePxPara);
            oneMileRing.setLayoutParams(fourFivePxPara);
        }
        if (ringAdapter != null)
            ringAdapter.setZoomState(zoomState);
    }
    public void setupServer(){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String server = preferences.getString("server", "");
        if (!server.isEmpty()) {
            UserAPI.server = server;
        }
    }
}