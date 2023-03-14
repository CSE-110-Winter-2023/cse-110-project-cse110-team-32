package com.example.team_32;

import static com.example.team_32.Angle.angleBetweenLocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import android.Manifest;

import java.util.Collections;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {
    private OrientationService orientationService;
    private LocationService locationService;
    private String public_code;
    private boolean first = true;
    public UserPosAdapter userPosAdapter;

    UserViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = setUpViewModel();
        Context context = getApplication().getApplicationContext();

        loadMainUser();
        if (!mainUser.exists()){
            Log.i("nameActivity", "Went there ? ");
            Intent userNameAct = new Intent(this, UsernameActivity.class);
            startActivity(userNameAct);
        }

        orientationService = OrientationService.singleton(this);
        setUpOri();
        getPermissions();
        locationService = LocationService.singleton(this);
//        setUpLoc();
        locationService.getLocation().
                observe(this, loc ->
                {
                    String text = String.format("Lat: %.2f, Lon: %.2f", loc.first, loc.second);
                    if (loc.first != null && loc.second != null) {
                            viewModel.updateMain(loc);
                    }

                    orientationService.getOrientation().observe(this, ori -> {
                        float degrees = (float) Math.toDegrees((double) ori);
                        TextView longitude = (TextView) findViewById(R.id.longitude);
                        TextView latitude = (TextView) findViewById(R.id.latitude);
                        if (!this.inputValid())
                            return;
                        Pair<Double, Double> loc2 = new Pair<>(Utilities.parseDouble(latitude.getText().toString()).get(), Utilities.parseDouble(longitude.getText().toString()).get());
                    });
                });

        ListView listView = findViewById(R.id.listView1);
        userPosAdapter =  new UserPosAdapter(this);
        listView.setAdapter(userPosAdapter);
        viewModel.getUsers().observe(this, userPosAdapter::setUsers);
        userPosAdapter.notifyDataSetChanged();
    }

//    Todo: Fix this part
//    private void setupInput(UserViewModel viewModel) {
//        var input = (EditText) findViewById(R.id.input_new_UID);
//        input.setOnEditorActionListener((view, actionId, event) -> {
//            // If the event isn't "done" or "enter", do nothing.
//            if (actionId != EditorInfo.IME_ACTION_DONE) {
//                return false;
//            }
//
//            // Otherwise, create a new note, persist it...
//            var title = input.getText().toString();
//            var user = viewModel.getUser(title, this);
//
//            // ...wait for the database to finish persisting it...
//            user.observe(this, userEntity -> {
//                // ...stop observing.
//
//                user.removeObservers(this);
//
//                // ...and launch NoteActivity with it.
//                // bind it to the list ?
//            });
//
//            return true;
//        });
//    }

    //Todo: Needs something similar for testing
//    @SuppressLint("RestrictedApi")
//    private void setupRecycler(UserAdapter adapter) {
//        // We store the recycler view in a field _only_ because we will want to access it in tests.
//        recyclerView = findViewById(R.id.recView);
//        recyclerView.setLayoutManager(new RecyclerView.LayoutManager() {
//            @Override
//            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
//                return null;
//            }
//        });
//        recyclerView.setLayoutManager(new userLayOutManager());
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        recyclerView.setAdapter(adapter);
//    }


    private void getPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
    }

    // Todo: Move Location Set-Up here
    private void setUpLoc() {
    }

    // Todo: Fix Orientation Set-Up
    private void setUpOri() {
        if (orientationService.getOrientation().hasObservers())
            return;
        orientationService.getOrientation().
                observe(this, ori ->
                {
                    float degrees = (float) Math.toDegrees((double) ori);
//                    findViewById(R.id.arrowOri).setRotation(degrees);
                });
    }

    private UserViewModel setUpViewModel() {
        return new ViewModelProvider(this).get(UserViewModel.class);
    }

    // Todo: Update this
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
       if (mainUser.exists()) {
           saveMainUser();
           loadMainUser();
           viewModel.reSyncAll();
       }
        orientationService.regSensorListeners();
    }
    private void saveMainUser(){
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("public_code", viewModel.getMainUserCode());
        editor.apply();
    }

    private void loadMainUser(){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        public_code = preferences.getString("public_code","");
        TextView uidLabel =findViewById(R.id.UIDlable);
        uidLabel.setText("UID: "+public_code);
        if(public_code != null && !public_code.isEmpty()){
            Log.i("CODE", "loadMainUser: "+ public_code);
            viewModel.loadMainUser(public_code);
        }
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
}