package com.example.team_32;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;


import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class UserViewModel extends AndroidViewModel {

    private LiveData<List<User>> users;
    private final UserDao userDao;
    private final UserRepo userRepo;
    private mainUser mainuser;

    public UserViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        UserDatabase db = UserDatabase.provide(context);
        userDao = db.getDao();
        userRepo = UserRepo.singleton(userDao, UserAPI.provide());
    }

    public void loadMainUser(String public_code) {
        if (!userRepo.existsLocal(public_code)) {
            this.mainuser = mainUser.singleton();
            updateMain();
        }else {
            this.mainuser = mainUser.fromUser(userDao.getMain(public_code));
        }
    }
    public void setUpGPSloss(Activity main){
        ScheduledExecutorService exe = Executors.newSingleThreadScheduledExecutor();
        exe.scheduleAtFixedRate(() -> {
            if (mainuser != null) {
                var timeSinceLastUpdate = ((System.currentTimeMillis() / 1000) - mainuser.updatedAt);
                var timeInMin = Math.floorDiv(timeSinceLastUpdate, 60L);
                var timeInHours = Math.floorDiv(timeInMin, 60L);
                    main.runOnUiThread(()->{
                        Log.i("GPSLoss", "time in sec:" + timeSinceLastUpdate + " in min: "+ timeInMin + " time in h: "+ timeInHours);
                        ImageView gpsDot  = main.findViewById(R.id.gpsIndicator);
                        TextView gpsLabel  = main.findViewById(R.id.GPS_time);
                        if (timeSinceLastUpdate > 60L){
                        gpsDot.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                        if (timeInHours >= 1){
                            gpsLabel.setText(timeInHours+"h");
                        }else if (timeInMin > 0 && timeInMin <=60){
                            gpsLabel.setText(timeInMin+"m");
                        }
                    } else {
                    gpsDot.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
                    gpsLabel.setText("");
                }});
            }
        }, 3, 3, TimeUnit.SECONDS);
    }

    public void updateMain(android.util.Pair<Double, Double> loc) {
        if (mainuser != null){
            if (!(Utilities.compFloat((float)(double)loc.first, mainuser.latitude)) && (!Utilities.compFloat((float)(double)loc.second, mainuser.longitude)))
                return;

            Log.i("MainUpdate", "updateMain: " + mainuser.latitude + " " + mainuser.longitude +" with " + (float)(double)loc.first + ", " +(float)(double)loc.second);
            mainuser.updateLoc(loc.first, loc.second);
            updateMain();
        }else {
            Log.i("MainUpdate", "updateMain: " +"null");
        }
    }
    public void updateMain() {
        if (mainuser != null){
            userRepo.upsertLocal(mainuser);
            userRepo.upsertRemote(mainuser);
        }
    }

    public LiveData<User> getUser(String code, LifecycleOwner lifeOwner){
        userRepo.getSynced(code);
        if (!userDao.exists(code)){
            var temp = userRepo.getRemote(code);
            temp.observe(lifeOwner, userEntity -> {
                // ...stop observing.
                if (userEntity != null) {
                    temp.removeObservers(lifeOwner);
                    userDao.upsert(userEntity);
                }
                else{
                    Log.i("DD", "getUser: null1!!");
                }
            });
        }
        return userRepo.getSynced(code);
    }

    public String getMainUserCode (){
        if (mainuser == null)
            mainuser = mainUser.singleton();
        return mainuser.public_code;
    }
    public void reSyncAll(){
        var data =getUsers();
        Utilities.observeOnce(data, usrs ->{
            for (var usr: usrs){
                if (!(usr.public_code+"_private2").equals(mainuser.private_code)){
                    userRepo.getSynced(usr.public_code);
                }
            }
        });
    }

    public LiveData<List<User>> getUsers() {
        return userRepo.getAllLocal();
    }

}
